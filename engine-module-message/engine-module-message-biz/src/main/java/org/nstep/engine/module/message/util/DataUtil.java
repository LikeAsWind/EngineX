package org.nstep.engine.module.message.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.xxljob.domain.CronTaskCords;
import org.nstep.engine.module.message.config.ChannelConfig;
import org.nstep.engine.module.message.constant.DDingDingSendMessageTypeConstants;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.constant.RedissonConstants;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.domain.SendTaskInfo;
import org.nstep.engine.module.message.domain.content.SendContent;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;


/**
 * 消息数据统计/记录工具类
 * <p>
 * 该类包含了多个方法用于管理和操作消息的发送状态、记录定时任务的执行情况、进行数据映射（如渠道映射）、更新消息发送状态等。
 * 它主要与 Redis 和 Redisson 一起使用，提供了功能强大的数据管理与同步操作。
 *
 * @Author: hanabi
 * @DateTime: 2023/11/17
 */
@Component
@Slf4j
@Data
public class DataUtil {

    /**
     * Redis 操作模板，用于执行 Redis 相关的操作
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Redisson 客户端，用于处理分布式锁和其他同步操作
     */
    @Resource
    private RedissonClient redissonClient;

    /**
     * 存储消息状态的映射（整型 -> 字符串）
     * 用于映射不同消息状态的描述，例如：正常、已停用、发送中等。
     */
    public Map<Integer, String> statusMapping;

    /**
     * 存储发送类型的映射（字符串 -> 字符串）
     * 用于映射不同类型的发送消息类型，例如：文本消息、链接消息、Markdown 消息等。
     */
    public Map<String, String> sendTypeMapping;

    /**
     * 渠道映射：整型映射到字符串（例如：1 -> "短信"）
     *
     * @return 渠道映射的 Map
     */
    public Map<Integer, String> channelMapping() {
        Map<Integer, String> map = new HashMap<>();
        // 遍历 ChannelConfig 中的渠道信息，建立整型到字符串的映射关系
        for (int i = 0; i < ChannelConfig.CHANNELS.size(); i++) {
            map.put(ChannelConfig.CHANNELS.get(i), ChannelConfig.CHANNEL_NAMES.get(i));
        }
        return map;
    }

    /**
     * 渠道映射：字符串映射到整型（例如："短信" -> 1）
     *
     * @return 渠道映射的 Map
     */
    public Map<String, Integer> channelMappingToInteger() {
        Map<String, Integer> map = new HashMap<>();
        // 遍历 ChannelConfig 中的渠道信息，建立字符串到整型的映射关系
        for (int i = 0; i < ChannelConfig.CHANNELS.size(); i++) {
            map.put(ChannelConfig.CHANNEL_NAMES.get(i), ChannelConfig.CHANNELS.get(i));
        }
        return map;
    }

    /**
     * 渠道中文映射：整型映射到中文字符串（例如：1 -> "短信"）
     *
     * @return 渠道中文映射的 Map
     */
    public Map<Integer, String> channelCNMapping() {
        Map<Integer, String> map = new HashMap<>();
        // 遍历 ChannelConfig 中的渠道信息，建立整型到中文字符串的映射关系
        for (int i = 0; i < ChannelConfig.CHANNELS.size(); i++) {
            map.put(ChannelConfig.CHANNELS.get(i), ChannelConfig.CHANNEL_CN_NAMES.get(i));
        }
        return map;
    }

    /**
     * 初始化消息状态和发送类型的映射
     * 在应用启动时，系统会使用这些映射来进行状态和发送类型的转换。
     */
    @PostConstruct
    public void typeMapping() {
        Map<Integer, String> statusMap = new HashMap<>();
        Map<String, String> sendTypeMap = new HashMap<>();

        // 消息状态映射
        statusMap.put(MessageDataConstants.MSG_NEW, "正常");
        statusMap.put(MessageDataConstants.MSG_STOP, "已停用");
        statusMap.put(MessageDataConstants.MSG_START, "已启用");
        statusMap.put(MessageDataConstants.MSG_SENDING, "发送中");
        statusMap.put(MessageDataConstants.MSG_FAIL, "发送失败");
        statusMap.put(MessageDataConstants.MSG_SUCCESS, "发送成功");

        // 发送类型映射
        sendTypeMap.put(DDingDingSendMessageTypeConstants.TEXT, DDingDingSendMessageTypeConstants.TEXT_NAME);
        sendTypeMap.put(DDingDingSendMessageTypeConstants.LINK, DDingDingSendMessageTypeConstants.LINK_NAME);
        sendTypeMap.put(DDingDingSendMessageTypeConstants.MARKDOWN, DDingDingSendMessageTypeConstants.MARKDOWN_NAME);
        sendTypeMap.put(DDingDingSendMessageTypeConstants.ACTION_CARD, DDingDingSendMessageTypeConstants.ACTION_CARD_NAME);
        sendTypeMap.put(DDingDingSendMessageTypeConstants.FEED_CARD, DDingDingSendMessageTypeConstants.FEED_CARD_NAME);

        // 将映射设置到成员变量中
        this.statusMapping = statusMap;
        this.sendTypeMapping = sendTypeMap;
    }

    /**
     * 记录定时任务模板最近一次的发送状态
     * <p>
     * 该方法用于更新并记录定时任务的发送状态，状态包括调度开始、发送中、发送成功、失败等。
     * 还会记录每个阶段的开始时间、结束时间及总耗时。
     *
     * @param nextStatus        下一个状态
     * @param messageTemplateId 消息模板 ID
     * @param sender            发送者 ID
     * @param log               日志信息
     */
    public void recordCronTaskStatus(String nextStatus, Long messageTemplateId, Long sender, String log) {
        // 从 Redis 获取当前的 CronTaskCords 对象
        CronTaskCords cronTaskCords = JSONUtil.toBean(stringRedisTemplate.opsForValue().get(MessageDataConstants.CRON_TASK_STATUS_KEY + sender + ":" + messageTemplateId), CronTaskCords.class);

        if (Objects.isNull(cronTaskCords)) {
            throw exception(ErrorCodeConstants.ILLEGAL_OPERATION_USER);  // 如果获取不到，抛出异常
        }

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 根据状态更新 CronTaskCords
        if (MessageDataConstants.CRON_TASK_SCHEDULING.equals(nextStatus)) {
            // 如果是调度开始，记录调度时间并重置相关字段
            cronTaskCords.setSchedulingTime(now);
            cronTaskCords.setSendTakeTime(0);
            cronTaskCords.setStartTakeTime(0);
            cronTaskCords.setSendingTime(null);
            cronTaskCords.setFailTime(null);
            cronTaskCords.setTotalTakeTime(new BigInteger("0"));
            cronTaskCords.setSuccessTime(null);
        }

        if (MessageDataConstants.CRON_TASK_SENDING.equals(nextStatus)) {
            // 如果是发送阶段，计算启动阶段花费的时间
            cronTaskCords.setStartTakeTime(Duration.between(cronTaskCords.getSchedulingTime(), now).toMillis());
            cronTaskCords.setSendingTime(now);
        }

        if (MessageDataConstants.CRON_TASK_SUCCESS.equals(nextStatus)) {
            // 如果是发送成功阶段，计算发送阶段花费的时间
            cronTaskCords.setSendTakeTime(Duration.between(cronTaskCords.getSendingTime(), now).toMillis());
            cronTaskCords.setSuccessTime(now);
            BigInteger total = new BigInteger(Long.toString(cronTaskCords.getStartTakeTime())).add(new BigInteger(Long.toString(cronTaskCords.getSendTakeTime())));
            cronTaskCords.setTotalTakeTime(total);
        }

        if (MessageDataConstants.CRON_TASK_FAIL.equals(nextStatus)) {
            // 如果是发送失败，记录失败时间
            cronTaskCords.setFailTime(now);
        }

        if (MessageDataConstants.CRON_TASK_STOP.equals(nextStatus)) {
            // 如果任务停止，记录停止时间
            cronTaskCords.setStopTime(now);
        }

        // 设置任务状态和日志信息
        cronTaskCords.setStatus(nextStatus);
        if (StrUtil.isNotBlank(log)) {
            cronTaskCords.setLog(log);
        }

        // 将更新后的 CronTaskCords 存入 Redis
        stringRedisTemplate.opsForValue().set(MessageDataConstants.CRON_TASK_STATUS_KEY + cronTaskCords.getSender() + ":" + messageTemplateId, JSON.toJSONString(cronTaskCords));
    }

    /**
     * 确认发送任务的某一组消息的发送状态
     * <p>
     * 该方法用于确认一个发送任务中的某一条消息的发送状态，并根据发送结果更新 Redis 中的消息数据。
     *
     * @param sendId          发送 ID
     * @param messageId       消息 ID
     * @param messageRedisKey Redis 中的消息存储键
     * @param sendTaskId      发送任务 ID
     * @param ex              异常信息（失败时传入）
     */
    public synchronized void confirmSend(String sendId, Long messageId, String messageRedisKey, Long sendTaskId, Exception ex) {
        if (StrUtil.isBlank(messageRedisKey)) {
            log.error("{} is null", MessageDataConstants.SEND_MESSAGE_KEY);
            throw exception(ErrorCodeConstants.IS_NULL_TEMPLATE, MessageDataConstants.SEND_MESSAGE_KEY);
        }

        RLock rLock = redissonClient.getLock(RedissonConstants.SEND_CONTENT_LOCK + sendTaskId);

        try {
            rLock.lock();  // 获取锁，确保并发时只有一个线程能修改消息状态
            List<String> list = stringRedisTemplate.opsForList().range(messageRedisKey, 0, -1);
            if (Objects.isNull(list)) {
                log.error("消息数据丢失");
                throw exception(ErrorCodeConstants.MESSAGE_DATA_LOST);
            }
            // 将从 Redis 获取的字符串列表转换为 SendContext 对象列表
            List<SendContent> sendContexts = stringConvertSendContext(list);
            // 更新消息发送状态
            updateMsgStatus(sendContexts, messageId, sendId, messageRedisKey, sendTaskId, ex);
        } catch (Exception e) {
            log.error("发送流程出现异常:{}", Throwables.getStackTraceAsString(e));
        } finally {
            rLock.unlock();  // 释放锁
        }
    }

    /**
     * 更新消息发送状态
     * <p>
     * 根据发送结果（成功或失败）更新 Redis 中的消息数据，成功时记录发送成功，失败时记录失败，并更新任务的完成状态。
     *
     * @param sendContexts    消息上下文列表
     * @param messageId       消息 ID
     * @param sendId          发送 ID
     * @param messageRedisKey Redis 中的消息存储键
     * @param sendTaskId      发送任务 ID
     * @param ex              异常信息（失败时传入）
     */
    public void updateMsgStatus(List<SendContent> sendContexts, Long messageId, String sendId, String messageRedisKey, Long sendTaskId, Exception ex) {
        if (sendTaskId == null || sendTaskId == 0) {
            throw exception(ErrorCodeConstants.IS_NULL_TEMPLATE, MessageDataConstants.SEND_TASK_ID);
        }

        // 从后往前遍历消息上下文，找到对应的发送任务
        for (int i = sendContexts.size() - 1; i >= 0; i--) {
            if (Objects.equals(sendContexts.get(i).getSendTaskId(), sendTaskId)) {
                List<SendTaskInfo> sendTasks = sendContexts.get(i).getSendTasks();
                for (SendTaskInfo sendTask : sendTasks) {
                    if (Objects.equals(sendTask.getMessageId(), messageId)) {
                        TemplateDO messageTemplate = sendTask.getMessageTemplate();
                        if (!MessageDataConstants.MSG_SENDING.equals(messageTemplate.getMsgStatus())) {
                            return;  // 如果不是正在发送状态，直接退出
                        }
                        // 根据发送结果更新消息状态
                        if (StrUtil.isNotBlank(sendId)) {
                            // 发送成功
                            messageTemplate.setMsgStatus(MessageDataConstants.MSG_SUCCESS);
                            log.info("消息发送成功,返回信息:{}", sendId);
                            LocalDateTime now = LocalDateTime.now();
                            sendTask.setSendEndTime(now);
                            sendTask.setTakeTime(Duration.between(sendTask.getSendStartTime(), now).toMillis());
                            if (MessageDataConstants.TIMING.equals(messageTemplate.getPushType())) {
                                recordCronTaskStatus(MessageDataConstants.CRON_TASK_SUCCESS, messageTemplate.getId(), sendContexts.get(i).getSender(), "消息发送成功,返回信息:" + sendId);
                            }
                            sendTask.setMessageTemplate(messageTemplate);
                        } else {
                            // 发送失败
                            messageTemplate.setMsgStatus(MessageDataConstants.MSG_FAIL);
                            log.error("消息发送失败,返回信息:{}", Throwables.getStackTraceAsString(ex));

                            LocalDateTime now = LocalDateTime.now();
                            sendTask.setSendEndTime(now);
                            sendTask.setTakeTime(Duration.between(sendTask.getSendStartTime(), now).toMillis());
                            if (MessageDataConstants.TIMING.equals(messageTemplate.getPushType())) {
                                recordCronTaskStatus(MessageDataConstants.CRON_TASK_FAIL, messageTemplate.getId(), sendContexts.get(i).getSender(), Throwables.getStackTraceAsString(ex));
                            }
                            sendTask.setMessageTemplate(messageTemplate);
                        }
                    }
                }
                // 更新 Redis 中的消息上下文
                stringRedisTemplate.opsForList().set(messageRedisKey, i, JSON.toJSONString(sendContexts.get(i)));
            }
        }
    }

    /**
     * 将 Redis 中取出的字符串列表转换成 `SendContent` 类型
     *
     * @param list 从 Redis 中取出的字符串列表
     * @return 转换后的 `SendContent` 对象列表
     */
    public List<SendContent> stringConvertSendContext(List<String> list) {
        return list.stream()
                .map(s -> JSONUtil.toBean(s, SendContent.class))
                .collect(Collectors.toList());
    }
}
