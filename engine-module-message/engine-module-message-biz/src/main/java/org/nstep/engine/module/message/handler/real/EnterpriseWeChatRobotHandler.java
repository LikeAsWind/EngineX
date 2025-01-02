package org.nstep.engine.module.message.handler.real;

import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.dto.weChat.EnterpriseWeChatRobotConfig;
import org.nstep.engine.module.message.util.AccountUtil;
import org.nstep.engine.module.message.util.DataUtil;
import org.nstep.engine.module.message.util.tencent.EnterpriseWeChatRobotUtils;
import org.springframework.stereotype.Component;

/**
 * 企业微信机器人处理器
 * 该类继承自 ChannelHandler，用于处理企业微信机器人消息发送的相关逻辑。
 * 它从模板发送任务中获取配置信息，调用企业微信机器人工具类进行消息发送，并记录发送结果。
 */
@Component
@Slf4j
public class EnterpriseWeChatRobotHandler extends ChannelHandler {

    @Resource
    private AccountUtil accountUtil;  // 账户工具类，用于获取企业微信机器人账户配置信息

    @Resource
    private DataUtil dataUtil;  // 数据工具类，用于记录任务执行状态

    /**
     * 处理企业微信机器人消息发送任务
     *
     * @param TemplateSendTask 消息模板发送任务对象，包含任务和消息的相关信息
     */
    @Override
    void doHandler(TemplateSendTask TemplateSendTask) {
        try {
            // 获取企业微信机器人账户配置信息
            EnterpriseWeChatRobotConfig account = accountUtil.getAccount(TemplateSendTask.getMessageTemplate().getSendAccount(), EnterpriseWeChatRobotConfig.class);
            // 调用企业微信机器人工具类发送消息
            String result = EnterpriseWeChatRobotUtils.send(account, TemplateSendTask);
            // 记录发送结果，发送成功
            dataUtil.confirmSend(result, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.sendTaskId, new Exception());
        } catch (Exception e) {
            // 记录发送失败的异常信息
            dataUtil.confirmSend(null, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(), e);
            // 记录异常日志
            log.error("企业微信机器人发送异常:{}", Throwables.getStackTraceAsString(e));
        }
    }
}
