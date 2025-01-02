package org.nstep.engine.module.message.util.tencent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.message.constant.WeChatConstants;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.dto.model.EnterpriseWeChatRobotContentModel;
import org.nstep.engine.module.message.dto.weChat.EnterpriseWeChatRobotConfig;
import org.nstep.engine.module.message.dto.weChat.EnterpriseWeChatRobotParam;
import org.nstep.engine.module.message.dto.weChat.EnterpriseWeChatRootResult;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception0;

/**
 * 企业微信机器人工具类
 * 该类用于通过企业微信机器人接口发送不同类型的消息（如文本、图片、文件等）。
 * 根据消息内容构建请求参数，并通过HTTP请求将消息发送到企业微信机器人。
 */
@Slf4j
public class EnterpriseWeChatRobotUtils {

    // 手机号正则表达式，用于判断接收者是否为手机号
    private static final String regex = "^1[3456789]\\d{9}$";

    /**
     * 发送企业微信机器人消息
     * 该方法根据给定的配置信息和消息任务信息，构建消息参数并发送给企业微信机器人。
     *
     * @param config       配置对象，包含企业微信机器人的Webhook地址等信息
     * @param sendTaskInfo 消息发送任务信息，包含消息模板和接收者等内容
     * @return 返回企业微信机器人接口的响应结果
     */
    public static String send(EnterpriseWeChatRobotConfig config, TemplateSendTask sendTaskInfo) {
        // 解析消息模板内容
        EnterpriseWeChatRobotContentModel contentModel = JSON.parseObject(sendTaskInfo.getMessageTemplate().getMsgContent(), EnterpriseWeChatRobotContentModel.class);
        // 构建企业微信机器人消息参数
        EnterpriseWeChatRobotParam robotParam = buildEnterpriseWeChatRobotParam(sendTaskInfo, contentModel);

        // 发送POST请求到企业微信机器人Webhook
        String result = HttpRequest.post(config.getWebhook())
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .body(JSON.toJSONString(robotParam))  // 将请求参数转为JSON字符串
                .timeout(2000)  // 设置超时时间为2秒
                .execute().body();  // 执行请求并获取响应体

        // 解析响应结果
        EnterpriseWeChatRootResult rootResult = JSON.parseObject(result, EnterpriseWeChatRootResult.class);

        // 如果返回的错误码不为0，表示发送失败，抛出异常
        if (rootResult.getErrCode() != 0) {
            throw exception(
                    ErrorCodeConstants.EMPTY_OBJECT,
                    rootResult.toString()
            );
        }
        return rootResult.toString();  // 返回响应结果
    }

    /**
     * 构建企业微信机器人消息参数
     * 根据消息类型（如文本、图片、文件等）构建对应的消息参数对象。
     *
     * @param sendTaskInfo 消息发送任务信息，包含接收者和消息模板等
     * @param contentModel 企业微信机器人消息内容模型，包含消息内容和类型
     * @return 返回构建好的企业微信机器人消息参数
     */
    private static EnterpriseWeChatRobotParam buildEnterpriseWeChatRobotParam(TemplateSendTask sendTaskInfo, EnterpriseWeChatRobotContentModel contentModel) {
        EnterpriseWeChatRobotParam robotParam = EnterpriseWeChatRobotParam.builder().build();
        // 将消息内容转换为JSON对象
        JSONObject jsonObject = JSON.parseObject(contentModel.getContent());

        // 根据消息类型构建不同的消息参数
        if (WeChatConstants.TEXT.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.TextDTO textDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.TextDTO.class);
            robotParam.setMsgType(WeChatConstants.TEXT_NAME);
            if (Pattern.matches(regex, CollUtil.getFirst(sendTaskInfo.getReceivers()))) {
                // 如果接收者是手机号列表
                textDTO.setMentionedMobileList(new ArrayList<>(sendTaskInfo.getReceivers()));
            } else {
                // 如果接收者是用户ID列表
                textDTO.setMentionedList(new ArrayList<>(sendTaskInfo.getReceivers()));
            }
            robotParam.setText(textDTO);
        }
        if (WeChatConstants.MARKDOWN.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.MarkdownDTO markdownDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.MarkdownDTO.class);
            robotParam.setMsgType(WeChatConstants.MARKDOWN_NAME);
            robotParam.setMarkdown(markdownDTO);
        }
        if (WeChatConstants.IMAGE.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.ImageDTO imageDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.ImageDTO.class);
            robotParam.setMsgType(WeChatConstants.IMAGE_NAME);
            robotParam.setImage(imageDTO);
        }
        if (WeChatConstants.NEWS.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.NewsDTO newsDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.NewsDTO.class);
            robotParam.setMsgType(WeChatConstants.NEWS_NAME);
            robotParam.setNews(newsDTO);
        }
        if (WeChatConstants.FILE.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.FileDTO fileDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.FileDTO.class);
            robotParam.setMsgType(WeChatConstants.FILE_NAME);
            robotParam.setFile(fileDTO);
        }
        if (WeChatConstants.VOICE.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.VoiceDTO voiceDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.VoiceDTO.class);
            robotParam.setMsgType(WeChatConstants.VOICE_NAME);
            robotParam.setVoice(voiceDTO);
        }
        return robotParam;  // 返回构建好的消息参数
    }
}
