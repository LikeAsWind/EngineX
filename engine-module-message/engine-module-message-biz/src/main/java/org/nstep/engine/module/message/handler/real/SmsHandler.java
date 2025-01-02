package org.nstep.engine.module.message.handler.real;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dal.dataobject.account.AccountDO;
import org.nstep.engine.module.message.dal.mysql.account.AccountMapper;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.util.DataUtil;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 短信服务统一处理器
 * 该类继承自 ChannelHandler，主要用于根据配置路由到具体的第三方短信服务处理器，处理短信发送任务。
 * 它从消息模板获取账户配置信息，并根据配置路由到对应的短信服务处理器进行处理。
 */
@Component
@Slf4j
public class SmsHandler extends ChannelHandler {

    @Resource
    private Map<String, ChannelHandler> smsHandlers;  // 存储所有短信服务的处理器，按服务类型路由

    @Resource
    private AccountMapper accountMapper;  // 用于查询账户信息

    @Resource
    private DataUtil dataUtil;  // 数据工具类，用于记录任务执行状态

    /**
     * 处理短信发送任务
     * 从消息模板中获取账户配置，并根据配置路由到具体的短信服务处理器进行处理。
     *
     * @param TemplateSendTask 消息模板发送任务对象，包含任务和消息的相关信息
     */
    @Override
    void doHandler(TemplateSendTask TemplateSendTask) {
        try {
            // 从数据库中获取账户配置信息
            AccountDO channelAccount = accountMapper.selectById(TemplateSendTask.getMessageTemplate().getSendAccount());
            String accountConfig = channelAccount.getAccountConfig();
            // 将账户配置转化为JSON对象
            JSONObject jsonObject = JSONUtil.toBean(accountConfig, JSONObject.class);
            // 根据配置中的短信服务类型路由到具体的短信服务处理器
            smsHandlers.get(jsonObject.get(MessageDataConstants.SMS_SERVICE_KEY) + MessageDataConstants.HANDLER_SUFFIX).handler(TemplateSendTask);
        } catch (Exception e) {
            // 处理异常，记录失败信息
            dataUtil.confirmSend(null, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(), e);
            log.error("短信服务异常:{}", Throwables.getStackTraceAsString(e));
        }
    }
}
