package org.nstep.engine.module.message.process.management;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.message.config.ChannelConfig;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.constant.RedissonConstants;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.dto.content.ProcessContent;
import org.nstep.engine.module.message.dto.content.SendContent;
import org.nstep.engine.module.message.dto.message.DelayQueueTask;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.nstep.engine.module.message.rabbitmq.service.DelayMqService;
import org.nstep.engine.module.message.rabbitmq.service.RabbitMQService;
import org.nstep.engine.module.message.util.DataUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * 发送消息到 MQ 的处理器
 * <p>
 * 该类实现了 `BusinessProcess` 接口，处理发送消息到 RabbitMQ 的相关操作。
 * 它在执行过程中会将消息发送到 RabbitMQ，如果配置了延迟队列，则将任务发送到延迟交换机。
 * 还包括发送过程中一些统计逻辑，例如统计当前用户的发送人数和模板发送次数。
 * <p>
 * 该类通过 Spring 的 `@Component` 注解进行注册，方便在其他组件中进行注入使用。
 */
@Component
@Slf4j
public class SendMqProcess implements BusinessProcess {

    /**
     * 是否启用延迟队列的配置值
     */
    @Value("${engine.rabbitmq.delayQueues.enabled}")
    private String delayQueues;

    /**
     * RabbitMQ 服务接口，用于发送消息
     */
    @Resource
    private RabbitMQService rabbitMQService;

    /**
     * Redis 模板，用于操作 Redis 数据
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 工具类，用于数据相关操作
     */
    @Resource
    private DataUtil dataUtil;

    /**
     * 延迟消息队列服务，用于发送延迟队列任务
     */
    @Resource
    private DelayMqService delayMqService;

    /**
     * Redisson 客户端，用于分布式锁操作
     */
    @Resource
    private RedissonClient redissonClient;

    /**
     * 处理发送任务
     *
     * @param context 发送任务上下文，包含任务的详细信息
     * @return 处理后的上下文对象
     */
    @Override
    public ProcessContent process(ProcessContent context) {
        // 将上下文转换为发送内容对象
        SendContent sendContext = (SendContent) context;

        try {
            // 将发送上下文转换为 JSON 字符串，并发送消息
            String sendContextJson = JSON.toJSONString(sendContext);
            rabbitMQService.send(sendContextJson, sendContext.getSendCode());

            // 如果配置启用延迟队列，则将任务发送到延迟交换机
            if ("true".equals(delayQueues)) {
                sendXdl(sendContext);
            }
        } catch (Exception e) {
            // 处理异常并记录日志
            log.error("消息发送mq异常:{}", Throwables.getStackTraceAsString(e));

            // 设置错误日志
            sendContext.setSendLogs("errorMsg:" + Throwables.getStackTraceAsString(e));
            // 标记需要中断处理
            context.setIsNeedBreak(true);
            context.setResponse(
                    CommonResult.error0(
                            ErrorCodeConstants.MQ_SEND_EXCEPTION.getCode(),
                            ErrorCodeConstants.MQ_SEND_EXCEPTION.getMsg(),
                            e.getMessage()
                    ));
        } finally {
            // 将消息内容推送到 Redis 中的消息队列
            String messageKey = sendContext.getSendTasks().get(0).getSendMessageKey();
            stringRedisTemplate.opsForList()
                    .rightPush(messageKey, JSON.toJSONString(sendContext));

            // 统计发送任务相关信息
            calculateNumberOfSenders(sendContext, sendContext.getSender());
            calculateNumberOfTemplate(sendContext, sendContext.getSender());

            // 如果是定时任务，记录任务进入发送阶段
            if (MessageDataConstants.TIMING.equals(sendContext.getSendTasks().get(0).getMessageTemplate().getPushType())) {
                dataUtil.recordCronTaskStatus(MessageDataConstants.CRON_TASK_SENDING, sendContext.getSendTasks().get(0).getMessageTemplate().getId(), sendContext.getSender(), "消息任务进入发送阶段，正在推送消息...");
            }

            // 设置响应状态
            context.setResponse(CommonResult.success("消息发送成功"));
        }

        return context;
    }

    /**
     * 将消息发送到延迟交换机
     *
     * @param sendContext 发送的任务内容
     */
    private void sendXdl(SendContent sendContext) {
        // 创建延迟队列任务列表
        List<DelayQueueTask> delayTasks = new ArrayList<>();
        for (TemplateSendTask sendTask : sendContext.getSendTasks()) {
            // 将任务信息封装到 DelayQueueTask 对象中
            delayTasks.add(DelayQueueTask.builder().sendTaskId(sendTask.getSendTaskId())
                    .messageRedisKey(sendTask.getSendMessageKey())
                    .messageId(sendTask.getMessageId()).build());
        }

        // 获取发送频道并将延迟任务发送到相应的延迟队列
        Integer channel = sendContext.getSendTasks().get(0).getMessageTemplate().getSendChannel();
        delayMqService.send(JSON.toJSONString(delayTasks), ChannelConfig.CHANNEL_EXP_TIME.get(channel));
    }

    /**
     * 统计当前用户的发送人数，并将统计结果存储到 Redis
     *
     * @param sendContext 发送的任务内容
     * @param userId      发送用户的 ID
     */
    private void calculateNumberOfSenders(SendContent sendContext, Long userId) {
        // 统计本次任务的发送人数
        Integer sendNumber = countSendNumber(sendContext);

        // 获取分布式锁，确保同一用户的发送任务是串行执行
        RLock lock = redissonClient.getLock(RedissonConstants.USER_TOTAL_LOCK + sendContext.getSender());
        try {
            lock.lock();  // 获取锁
            // 获取该用户当前的发送人数
            String count = stringRedisTemplate.opsForValue().get(MessageDataConstants.USER_SEND_NUMBER + userId);

            Integer number;
            // 如果该用户有发送记录，更新发送次数
            if (Objects.nonNull(count) && !MessageDataConstants.PUSH_NOW.equals(count)) {
                number = Integer.parseInt(count);
                number += sendNumber;
            } else {
                // 如果没有发送记录，则设置为本次任务的发送人数
                number = sendNumber;
            }
            // 更新用户的发送次数
            stringRedisTemplate.opsForValue().set(MessageDataConstants.USER_SEND_NUMBER + userId, String.valueOf(number));
        } catch (Exception e) {
            log.error("统计用户总发送人数异常:{}", Throwables.getStackTraceAsString(e));
        } finally {
            lock.unlock();  // 释放锁
        }
    }

    /**
     * 统计当前模板的发送次数，并将统计结果存储到 Redis
     *
     * @param sendContext 发送的任务内容
     * @param userId      发送用户的 ID
     */
    private void calculateNumberOfTemplate(SendContent sendContext, Long userId) {
        // 统计本次任务的发送人数
        Integer sendNumber = countSendNumber(sendContext);

        // 获取分布式锁，确保同一模板的发送任务是串行执行
        RLock lock = redissonClient.getLock(RedissonConstants.TEMPLATE_TOTAL_LOCK + sendContext.getSender());
        try {
            lock.lock();  // 获取锁
            // 获取消息模板的 ID
            Long messageId = sendContext.getSendTasks().get(0).getMessageTemplate().getId();
            Map<Object, Object> entries = stringRedisTemplate.opsForHash()
                    .entries(MessageDataConstants.TEMPLATE_SEND_NUMBER_NAME + userId);

            // 如果该模板有发送记录，更新发送次数
            if (CollectionUtil.isNotEmpty(entries)) {
                Integer number;
                if (entries.get(messageId.toString()) != null) {
                    number = Integer.valueOf(entries.get(messageId.toString()).toString());
                    number += sendNumber;
                } else {
                    number = sendNumber;
                }
                entries.put(messageId.toString(), number.toString());
                stringRedisTemplate.opsForHash()
                        .putAll(MessageDataConstants.TEMPLATE_SEND_NUMBER_NAME + userId, entries);
            } else {
                // 如果该模板没有发送记录，创建新的记录
                Map<String, String> map = new HashMap<>();
                map.put(messageId.toString(), sendNumber.toString());
                stringRedisTemplate.opsForHash()
                        .putAll(MessageDataConstants.TEMPLATE_SEND_NUMBER_NAME + userId, map);
            }
        } catch (Exception e) {
            log.error("统计模板总发送人数异常:{}", Throwables.getStackTraceAsString(e));
        } finally {
            lock.unlock();  // 释放锁
        }
    }

    /**
     * 计算本次任务的总发送人数
     *
     * @param sendContext 发送的任务内容
     * @return 发送人数
     */
    public static Integer countSendNumber(SendContent sendContext) {
        int sendNumber = 0;
        for (TemplateSendTask sendTask : sendContext.getSendTasks()) {
            sendNumber += sendTask.getReceivers().size();
        }
        return sendNumber;
    }
}
