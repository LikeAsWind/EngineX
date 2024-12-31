package org.nstep.engine.module.message.process.management;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.message.config.ChannelConfig;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.dal.mysql.template.TemplateMapper;
import org.nstep.engine.module.message.domain.SendTaskInfo;
import org.nstep.engine.module.message.domain.content.ProcessContent;
import org.nstep.engine.module.message.domain.content.SendContent;
import org.nstep.engine.module.message.domain.content.SendTaskParamContent;
import org.nstep.engine.module.message.dto.content.SmsContentModel;
import org.nstep.engine.module.message.dto.content.WeChatServiceAccountContentModel;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.nstep.engine.module.message.util.ContentHolderUtil;
import org.nstep.engine.module.message.util.RedisKeyUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


/**
 * 为发送前进行数据填充，处理占位符和发送任务的生成
 * 实现了 BusinessProcess 接口，负责在消息发送前填充占位符数据并构建发送任务。
 */
@Service
@Slf4j
public class DataPlaceholderProcess implements BusinessProcess {

    @Resource
    private TemplateMapper templateMapper; // 用于从数据库获取模板

    @Resource
    private ContentHolderUtil contentHolderUtil; // 用于替换占位符

    @Resource
    private RedisKeyUtil redisKeyUtil; // 用于生成Redis的键值

    /**
     * 处理发送任务的逻辑，填充占位符并生成发送任务
     *
     * @param context 传入的上下文对象，包含任务参数
     * @return 处理后的上下文对象，包含生成的发送任务信息
     */
    @Override
    public ProcessContent process(ProcessContent context) {
        // 检查上下文是否为 SendTaskParamContent 类型，如果不是则中断流程
        if (!(context instanceof SendTaskParamContent sendTaskParamContext)) {
            context.setIsNeedBreak(true); // 标记需要中断
            context.setResponse(
                    CommonResult.error(
                            ErrorCodeConstants.PROCESS_CONTEXT_INTERRUPTION.getCode(),
                            ErrorCodeConstants.PROCESS_CONTEXT_INTERRUPTION.getMsg()
                    ));
            return context;
        }

        // 获取模板ID并从数据库中查询模板
        Long templateId = sendTaskParamContext.getMessageTemplateId();
        TemplateDO messageTemplate = templateMapper.selectById(templateId);

        // 设置消息模板的发送状态为发送中
        messageTemplate.setMsgStatus(MessageDataConstants.MSG_SENDING);

        // 生成发送任务ID
        Long sendTaskId = redisKeyUtil.createSendTaskId();

        // 判断是否有占位符，如果没有占位符，则直接生成一个发送任务
        if (sendTaskParamContext.getIsExitVariables() == 0) {
            return handleNoPlaceholderSend(sendTaskParamContext, messageTemplate, sendTaskId);
        }

        // 如果有占位符，则调用 buildSendContext 方法生成带占位符的发送任务
        return buildSendContext(sendTaskParamContext, messageTemplate, sendTaskId);
    }

    /**
     * 处理无占位符的发送任务
     *
     * @param sendTaskParamContext 发送任务的上下文，包含任务参数
     * @param messageTemplate      消息模板，包含占位符内容
     * @param sendTaskId           发送任务ID
     * @return 处理后的 SendContent 对象，包含生成的发送任务信息
     */
    private SendContent handleNoPlaceholderSend(SendTaskParamContent sendTaskParamContext, TemplateDO messageTemplate, Long sendTaskId) {
        // 生成发送任务的Redis键
        String messageRedisKey = RedisKeyUtil.createMessageRedisKey(sendTaskParamContext.getSender());

        // 处理需要特殊处理的渠道（不需要自己替换占位符）
        processSpecialChannels(sendTaskParamContext.getSendChannel(), messageTemplate);

        // 构建发送任务信息
        SendTaskInfo sendTask = SendTaskInfo.builder().messageTemplate(messageTemplate)
                .receivers(sendTaskParamContext.getSendTaskParams().get(StrUtil.EMPTY))
                .messageId(redisKeyUtil.createMessageId(messageTemplate.getId()))
                .sendMessageKey(messageRedisKey)
                .sendTaskId(sendTaskId)
                .sendStartTime(LocalDateTime.now())
                .build();

        // 返回构建的 SendContent 对象
        return SendContent.builder().sendTasks(Collections.singletonList(sendTask))
                .sendCode(MessageDataConstants.SEND_CODE)
                .sendTime(LocalDateTime.now())
                .sendChannel(sendTaskParamContext.getSendChannel())
                .sendTaskId(sendTaskId)
                .sender(sendTaskParamContext.getSender())
                .build();
    }

    /**
     * 处理无占位符的特殊渠道
     *
     * @param channel         发送渠道
     * @param messageTemplate 消息模板
     */
    private void processSpecialChannels(Integer channel, TemplateDO messageTemplate) {
        // 如果渠道需要特殊处理
        if (ChannelConfig.NEED_SKID.contains(channel)) {
            // 对短信渠道进行特殊处理，不需要设置内容
            if (Objects.equals(channel, MessageDataConstants.SMS)) {
                messageTemplate.setMsgContent(StrUtil.EMPTY); // 短信渠道无需内容
            }
            // 其他渠道暂不处理
        }
    }

    /**
     * 处理特殊渠道的占位符替换
     *
     * @param channel             发送渠道
     * @param copyMessageTemplate 消息模板副本
     * @param entry               占位符和其对应的接收者
     */
    private void processSpecialChannels(Integer channel, TemplateDO copyMessageTemplate, Map.Entry<String, Set<String>> entry) {
        // 如果渠道需要特殊处理
        if (ChannelConfig.NEED_SKID.contains(channel)) {
            // 对短信渠道进行占位符替换
            if (Objects.equals(channel, MessageDataConstants.SMS)) {
                SmsContentModel smsContentModel = JSON.parseObject(copyMessageTemplate.getMsgContent(), SmsContentModel.class);
                smsContentModel.setContent(entry.getKey()); // 设置短信内容
                copyMessageTemplate.setMsgContent(JSON.toJSONString(smsContentModel)); // 更新模板内容
            }

            // 对微信服务号渠道进行占位符替换
            if (Objects.equals(channel, MessageDataConstants.WECHAT_SERVICE_ACCOUNT)) {
                WeChatServiceAccountContentModel weChatServiceAccountContentModel = JSON.parseObject(copyMessageTemplate.getMsgContent(), WeChatServiceAccountContentModel.class);
                weChatServiceAccountContentModel.setContent(entry.getKey()); // 设置微信内容
                copyMessageTemplate.setMsgContent(JSON.toJSONString(weChatServiceAccountContentModel)); // 更新模板内容
            }
        }
    }

    /**
     * 构建含占位符数据的 SendContent 对象
     *
     * @param sendTaskParamContext 发送任务的上下文，包含任务参数
     * @param messageTemplate      消息模板，包含占位符内容
     * @param sendTaskId           发送任务ID
     * @return 构建的 SendContent 对象，包含发送任务信息
     */
    private SendContent buildSendContext(SendTaskParamContent sendTaskParamContext, TemplateDO messageTemplate, Long sendTaskId) {
        // 获取发送任务的参数，包含占位符和接收者信息
        Map<String, Set<String>> sendTaskParams = sendTaskParamContext.getSendTaskParams();

        // 生成发送任务的Redis键
        String messageRedisKey = RedisKeyUtil.createMessageRedisKey(sendTaskParamContext.getSender());

        // 存储所有的发送任务
        List<SendTaskInfo> sendTasks = new ArrayList<>();
        String content = messageTemplate.getMsgContent();

        // 遍历所有的占位符参数
        for (Map.Entry<String, Set<String>> entry : sendTaskParams.entrySet()) {
            // 创建模板副本，避免直接修改原模板
            TemplateDO copyMessageTemplate = BeanUtil.copyProperties(messageTemplate, TemplateDO.class);

            // 处理需要特殊处理的渠道（不需要自己替换占位符）
            processSpecialChannels(sendTaskParamContext.getSendChannel(), copyMessageTemplate, entry);

            // 如果渠道不在特殊处理列表中，则替换占位符
            if (!ChannelConfig.NEED_SKID.contains(sendTaskParamContext.getSendChannel())) {
                String completeContent = contentHolderUtil.replacePlaceHolder(content, entry.getKey());
                copyMessageTemplate.setMsgContent(completeContent); // 更新模板内容
            }

            // 构建发送任务信息
            SendTaskInfo sendTask = SendTaskInfo.builder().receivers(entry.getValue()).messageTemplate(copyMessageTemplate)
                    .messageId(redisKeyUtil.createMessageId(copyMessageTemplate.getId()))
                    .sendMessageKey(messageRedisKey)
                    .sendTaskId(sendTaskId)
                    .sendStartTime(LocalDateTime.now())
                    .build();

            // 添加到发送任务列表
            sendTasks.add(sendTask);
        }

        // 返回构建的 SendContent 对象
        return SendContent.builder().sendCode(MessageDataConstants.SEND_CODE)
                .sendTasks(sendTasks)
                .sendTime(LocalDateTime.now())
                .sendChannel(sendTaskParamContext.getSendChannel())
                .sendTaskId(sendTaskId)
                .sender(sendTaskParamContext.getSender())
                .build();
    }
}
