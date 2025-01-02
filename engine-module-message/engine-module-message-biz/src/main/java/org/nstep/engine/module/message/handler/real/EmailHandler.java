package org.nstep.engine.module.message.handler.real;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.util.io.FileUtils;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.dto.model.EmailContentModel;
import org.nstep.engine.module.message.util.AccountUtil;
import org.nstep.engine.module.message.util.DataUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;


/**
 * 邮箱处理器
 * 该类继承自 ChannelHandler，用于处理邮箱消息发送的相关逻辑。
 * 它从模板发送任务中获取配置信息，调用邮件工具类进行消息发送，并记录发送结果。
 */
@Component
@Slf4j
public class EmailHandler extends ChannelHandler {

    @Resource
    private AccountUtil accountUtil;  // 账户工具类，用于获取邮箱账户配置信息

    @Value("${engine.cacheFilePath}")
    private String path;  // 配置文件中的路径，用于获取文件存储路径

    @Resource
    private DataUtil dataUtil;  // 数据工具类，用于记录任务执行状态

    /**
     * 处理邮箱消息发送任务
     *
     * @param TemplateSendTask 消息模板发送任务对象，包含任务和消息的相关信息
     */
    @Override
    public void doHandler(TemplateSendTask TemplateSendTask) {
        String sendId = null;  // 邮件发送ID
        Long sendTaskId = TemplateSendTask.getSendTaskId();  // 获取任务ID
        String sendMessageKey = TemplateSendTask.getSendMessageKey();  // 获取消息唯一标识

        try {
            // 解析消息模板中的邮件内容
            EmailContentModel mailContent = JSON.parseObject(TemplateSendTask.getMessageTemplate().getMsgContent(), EmailContentModel.class);
            // 获取邮件账户配置信息
            MailAccount mailAccount = accountUtil.getAccount(TemplateSendTask.getMessageTemplate().getSendAccount(), MailAccount.class);

            // 解析邮件内容中的URL并下载附件
            List<String> urls = StrUtil.isNotBlank(mailContent.getUrl()) ? StrUtil.split(mailContent.getUrl(), MessageDataConstants.SEPARATOR) : null;
            List<File> files = CollectionUtil.isNotEmpty(urls) ? FileUtils.getRemoteUrl2File(path, urls) : null;

            // 如果有附件，发送带附件的邮件
            if (CollectionUtil.isNotEmpty(files)) {
                sendId = MailUtil.send(mailAccount, TemplateSendTask.getReceivers(), mailContent.getTitle(), mailContent.getContent(),
                        true, files.toArray(new File[0]));
            } else {
                // 否则发送不带附件的邮件
                sendId = MailUtil.send(mailAccount, TemplateSendTask.getReceivers(), mailContent.getTitle(), mailContent.getContent(),
                        true);
            }

            // 记录邮件发送成功
            dataUtil.confirmSend(sendId, TemplateSendTask.getMessageId(), sendMessageKey, sendTaskId, new Exception());
        } catch (Exception e) {
            // 记录邮件发送失败并捕获异常
            dataUtil.confirmSend(sendId, TemplateSendTask.getMessageId(), sendMessageKey, sendTaskId, e);
            // 记录异常日志
            log.error("邮件消息推送失败:{}", Throwables.getStackTraceAsString(e));
        }
    }
}
