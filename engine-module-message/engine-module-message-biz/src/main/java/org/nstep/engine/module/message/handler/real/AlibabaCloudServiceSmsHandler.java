package org.nstep.engine.module.message.handler.real;


import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dto.aliyun.AlibabaCloudSmsConfig;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.dto.model.SmsContentModel;
import org.nstep.engine.module.message.util.AccountUtil;
import org.nstep.engine.module.message.util.DataUtil;
import org.nstep.engine.module.message.util.aliyun.AlibabaCloudSMSSendUtils;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 * 阿里云短信服务处理器，继承自ChannelHandler类，负责处理短信发送的业务逻辑。
 * 该处理器通过阿里云短信API发送短信，并在发送成功或失败后进行确认。
 */
@Component 
@Slf4j
public class AlibabaCloudServiceSmsHandler extends ChannelHandler {

    @Resource
    private AccountUtil accountUtil; // 自动注入AccountUtil，用于获取阿里云短信的账号配置信息

    @Resource
    private DataUtil dataUtil; // 自动注入DataUtil，用于处理数据和确认短信发送状态

    /**
     * 处理短信发送任务的方法。
     * 从任务中提取短信模板、接收者、短信内容等信息，调用阿里云短信服务API发送短信。
     *
     * @param TemplateSendTask 短信发送任务，包含发送所需的各种信息（如短信模板、接收者、发送任务ID等）
     */
    @Override
    void doHandler(TemplateSendTask TemplateSendTask) {

        try {
            // 获取阿里云短信账号配置信息
            AlibabaCloudSmsConfig account = accountUtil.getAccount(TemplateSendTask.getMessageTemplate().getSendAccount(), AlibabaCloudSmsConfig.class);

            // 获取接收者手机号集合，并将其转换为以分隔符分隔的字符串
            Set<String> receivers = TemplateSendTask.getReceivers();
            String phones = String.join(MessageDataConstants.SEPARATOR, receivers);

            // 从模板中获取短信内容，并转换为SmsContentModel对象
            SmsContentModel smsContentModel = JSON.parseObject(TemplateSendTask.getMessageTemplate().getMsgContent(), SmsContentModel.class);

            // 调用阿里云短信发送工具类发送短信，获取回执ID
            String bZid = AlibabaCloudSMSSendUtils.sendMessage(account, phones, getSmsContent(smsContentModel).toJSONString());

            // 短信发送后，确认发送状态
            dataUtil.confirmSend(bZid, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(), new Exception());
        } catch (Exception e) {
            // 如果发生异常，记录失败并确认发送状态
            dataUtil.confirmSend(null, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(), e);
            log.error("阿里云短信发送异常:{}", Throwables.getStackTraceAsString(e)); // 记录错误日志
        }

    }
}
