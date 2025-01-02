package org.nstep.engine.module.message.handler.real;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.dto.model.SmsContentModel;
import org.nstep.engine.module.message.dto.tencent.TencentSmsConfig;
import org.nstep.engine.module.message.util.AccountUtil;
import org.nstep.engine.module.message.util.DataUtil;
import org.nstep.engine.module.message.util.tencent.TencentCloudSmsSendUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 腾讯云短信服务处理器
 * 该类继承自 ChannelHandler，用于处理通过腾讯云短信服务发送的短信任务。
 * 它从消息模板中获取短信内容和接收者信息，调用腾讯云短信 API 发送短信。
 * 相关文档：https://cloud.tencent.com/document/product/382/43194#.E9.80.9A.E8.BF.87-maven-.E5.AAE.8A.82
 */
@Component
@Slf4j
public class TencentCloudServiceSmsHandler extends ChannelHandler {

    @Resource
    private AccountUtil accountUtil;  // 用于获取账户配置信息

    @Resource
    private DataUtil dataUtil;  // 数据工具类，用于记录任务执行状态

    /**
     * 处理短信发送任务
     * 该方法从消息模板中获取短信发送所需的账户信息、短信内容及接收者信息，并通过腾讯云短信 API 发送短信。
     *
     * @param TemplateSendTask 消息模板发送任务对象，包含任务和消息的相关信息
     */
    @Override
    void doHandler(TemplateSendTask TemplateSendTask) {
        try {
            // 获取腾讯云短信账户配置
            TencentSmsConfig account = accountUtil.getAccount(TemplateSendTask.getMessageTemplate().getSendAccount(), TencentSmsConfig.class);
            // 构建接收者的手机号数组
            String[] phones = buildPhone(TemplateSendTask);
            // 构建短信参数数组
            String[] params = buildParams(TemplateSendTask);
            // 调用腾讯云短信发送工具类发送短信
            String send = TencentCloudSmsSendUtils.send(account, params, phones);
            // 记录发送结果
            dataUtil.confirmSend(send, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(), new Exception());
        } catch (Exception e) {
            // 处理异常，记录失败信息
            dataUtil.confirmSend(null, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(), e);
            log.error("腾讯云短信发送异常:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 构建短信参数数组
     * 从消息模板中解析短信内容，并构建参数数组。
     *
     * @param TemplateSendTask 消息模板发送任务对象
     * @return 短信参数数组
     */
    private String[] buildParams(TemplateSendTask TemplateSendTask) {
        // 从消息模板中获取短信内容
        SmsContentModel smsContentModel = JSON.parseObject(TemplateSendTask.getMessageTemplate().getMsgContent(), SmsContentModel.class);
        // 获取短信内容的 JSON 对象
        JSONObject jsonObject = getSmsContent(smsContentModel);
        // 构建短信参数数组
        String[] phones = new String[jsonObject.size()];
        int index = 0;
        for (String key : jsonObject.keySet()) {
            phones[index] = jsonObject.getString(key);
            ++index;
        }
        return phones;
    }

    /**
     * 构建接收者手机号数组
     * 将接收者手机号添加国际区号前缀，并返回数组。
     *
     * @param TemplateSendTask 消息模板发送任务对象
     * @return 接收者手机号数组
     */
    private String[] buildPhone(TemplateSendTask TemplateSendTask) {
        List<String> list = new ArrayList<>();
        // 遍历接收者手机号列表，添加国际区号前缀
        for (String receiver : TemplateSendTask.getReceivers()) {
            String phoneNumberWithPrefix = "+86" + receiver;  // 假设是中国手机号
            list.add(phoneNumberWithPrefix);
        }
        // 返回手机号数组
        return list.toArray(new String[list.size() - 1]);
    }
}
