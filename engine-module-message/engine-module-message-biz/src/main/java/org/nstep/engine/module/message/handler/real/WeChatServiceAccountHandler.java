package org.nstep.engine.module.message.handler.real;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.constant.WeChatConstants;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.dto.model.WeChatServiceAccountContentModel;
import org.nstep.engine.module.message.util.AccountUtil;
import org.nstep.engine.module.message.util.DataUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 微信服务号消息处理器
 * 该类继承自 ChannelHandler，用于处理通过微信服务号发送的模板消息。
 * 它从消息模板中获取微信服务号的内容和接收者信息，调用微信公众平台的 API 发送模板消息。
 */
@Component
@Slf4j
public class WeChatServiceAccountHandler extends ChannelHandler {

    @Resource
    private AccountUtil accountUtil;  // 用于获取账户配置信息

    @Resource
    private DataUtil dataUtil;  // 数据工具类，用于记录任务执行状态

    /**
     * 处理微信服务号模板消息发送任务
     * 该方法从消息模板中获取模板消息的内容和接收者信息，并通过微信服务号 API 发送模板消息。
     *
     * @param TemplateSendTask 消息模板发送任务对象，包含任务和消息的相关信息
     */
    @Override
    void doHandler(TemplateSendTask TemplateSendTask) {
        try {
            // 获取微信服务号消息内容
            WeChatServiceAccountContentModel contentModel = JSON.parseObject(TemplateSendTask.getMessageTemplate().getMsgContent(), WeChatServiceAccountContentModel.class);
            // 获取微信公众平台服务
            WxMpService wxMpService = accountUtil.getAccount(TemplateSendTask.getMessageTemplate().getSendAccount(), WxMpService.class);
            // 组装每个接收者的模板消息
            List<WxMpTemplateMessage> wxMpTemplateMessages = assembleParameters(TemplateSendTask.getReceivers(), contentModel);
            StringBuilder msg = new StringBuilder();
            // 发送每个模板消息
            for (WxMpTemplateMessage message : wxMpTemplateMessages) {
                msg.append(wxMpService.getTemplateMsgService().sendTemplateMsg(message)).append(MessageDataConstants.SEPARATOR);
            }
            // 删除最后一个分隔符
            msg.deleteCharAt(msg.lastIndexOf(MessageDataConstants.SEPARATOR));
            // 记录发送结果
            dataUtil.confirmSend(msg.toString(), TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(), new Exception());
        } catch (Exception e) {
            // 处理异常，记录失败信息
            dataUtil.confirmSend(null, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(), e);
            log.error("微信服务号消息发送异常:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 组装每一个模板消息参数
     * 根据接收者信息和内容模型，生成对应的微信模板消息列表。
     *
     * @param users        接收者用户集合
     * @param contentModel 微信服务号内容模型
     * @return 微信模板消息列表
     */
    public List<WxMpTemplateMessage> assembleParameters(Set<String> users, WeChatServiceAccountContentModel contentModel) {
        // 解析模板消息的内容
        Map<String, String> map = JSON.parseObject(contentModel.getContent(), Map.class);
        // 提前获取跳转链接
        String url = map.get(WeChatConstants.WECHAT_SERVICE_ACCOUNT_URL_NAME);
        if (StrUtil.isBlank(url)) {
            url = contentModel.getUrl();  // 如果没有指定 URL，则使用模板中的 URL
        } else {
            map.remove(WeChatConstants.WECHAT_SERVICE_ACCOUNT_URL_NAME);  // 移除 URL 配置项
        }
        // 创建一个微信模板消息列表
        List<WxMpTemplateMessage> wxMpServices = new ArrayList<>(users.size());
        // 为每个用户构建模板消息
        for (String user : users) {
            WxMpTemplateMessage message = WxMpTemplateMessage.builder().toUser(user)  // 设置接收者
                    .url(url)  // 设置跳转链接
                    .data(buildWxTemplateData(map))  // 设置模板消息数据
                    .templateId(contentModel.getTemplateId())  // 设置模板 ID
                    .miniProgram(new WxMpTemplateMessage.MiniProgram(null, null, false))  // 设置小程序信息
                    .build();
            wxMpServices.add(message);
        }
        return wxMpServices;
    }

    /**
     * 构建模板消息里面的参数数据
     * 根据传入的内容，构建微信模板消息所需的数据列表。
     *
     * @param map 模板消息参数
     * @return 微信模板数据列表
     */
    public List<WxMpTemplateData> buildWxTemplateData(Map<String, String> map) {
        List<WxMpTemplateData> wxMpTemplateData = new ArrayList<>(map.size());
        // 遍历每个参数，构建模板数据对象
        map.forEach((k, v) -> wxMpTemplateData.add(new WxMpTemplateData(k, v)));
        return wxMpTemplateData;
    }
}
