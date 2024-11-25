package org.nstep.engine.module.system.mq.producer.sms;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.core.KeyValue;
import org.nstep.engine.module.system.mq.message.sms.SmsSendMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sms 短信相关消息的 Producer
 *
 * @since 2021/3/9 16:35
 */
@Slf4j
@Component
public class SmsProducer {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 发送 {@link SmsSendMessage} 消息
     *
     * @param logId          短信日志编号
     * @param mobile         手机号
     * @param channelId      渠道编号
     * @param apiTemplateId  短信模板编号
     * @param templateParams 短信模板参数
     */
    public void sendSmsSendMessage(Long logId, String mobile,
                                   Long channelId, String apiTemplateId, List<KeyValue<String, Object>> templateParams) {
        SmsSendMessage message = new SmsSendMessage();
        message.setLogId(logId);
        message.setMobile(mobile);
        message.setChannelId(channelId);
        message.setApiTemplateId(apiTemplateId);
        message.setTemplateParams(templateParams);
        applicationContext.publishEvent(message);
    }

}
