package org.nstep.engine.module.system.mq.producer.mail;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.system.mq.message.mail.MailSendMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Mail 邮件相关消息的 Producer
 *
 * @since 2021/4/19 13:33
 */
@Slf4j
@Component
public class MailProducer {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 发送 {@link MailSendMessage} 消息
     *
     * @param sendLogId 发送日志编码
     * @param mail      接收邮件地址
     * @param accountId 邮件账号编号
     * @param nickname  邮件发件人
     * @param title     邮件标题
     * @param content   邮件内容
     */
    public void sendMailSendMessage(Long sendLogId, String mail, Long accountId,
                                    String nickname, String title, String content) {
        MailSendMessage message = new MailSendMessage();
        message.setLogId(sendLogId);
        message.setMail(mail);
        message.setAccountId(accountId);
        message.setNickname(nickname);
        message.setTitle(title);
        message.setContent(content);
        applicationContext.publishEvent(message);
    }

}
