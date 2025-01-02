package org.nstep.engine.module.message.util.dingding;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.common.base.Throwables;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.nstep.engine.module.message.constant.DDingDingSendMessageTypeConstants;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dto.dingding.DingDingRobotConfig;
import org.nstep.engine.module.message.dto.dingding.DingDingRobotParam;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.dto.model.DingDingRobotContentModel;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception0;


/**
 * 钉钉自定义机器人发送服务工具类
 **/
@Slf4j
@Component
public class DingDingRobotUtils {

    private static final String regex = "^1[3456789]\\d{9}$"; // 手机号正则表达式

    public void send(DingDingRobotConfig config, TemplateSendTask TemplateSendTask) {
        DingTalkClient client = new DefaultDingTalkClient(createSignUrl(config));
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        DingDingRobotContentModel contentModel = JSON.parseObject(TemplateSendTask.getMessageTemplate().getMsgContent(), DingDingRobotContentModel.class);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        //判断是否需要@群成员
        if (DDingDingSendMessageTypeConstants.SEND_ALL.equals(CollUtil.getFirst(TemplateSendTask.getReceivers()))) {
            //@所有人
            at.setIsAtAll(true);
        } else if (Pattern.matches(regex, CollUtil.getFirst(TemplateSendTask.getReceivers()))) {
            //输入的是手机号
            at.setAtMobiles(new ArrayList<>(TemplateSendTask.getReceivers()));
        } else if (!MessageDataConstants.PUSH_NOW.equals(CollUtil.getFirst(TemplateSendTask.getReceivers())) && !Pattern.matches(regex, CollUtil.getFirst(TemplateSendTask.getReceivers()))) {
            //接受者不为零且不是手机号 也就是钉钉号
            at.setAtUserIds(new ArrayList<>(TemplateSendTask.getReceivers()));
        }

        JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
        if (DDingDingSendMessageTypeConstants.TEXT_NAME.equals(contentModel.getSendType())) {
            //文本类型
            DingDingRobotParam.Text text = jsonObject.toJavaObject(DingDingRobotParam.Text.class);
            request.setText(JSON.toJSONString(text));
            request.setMsgtype("text");
        }
        if (DDingDingSendMessageTypeConstants.LINK_NAME.equals(contentModel.getSendType())) {
            //link类型
            DingDingRobotParam.Link link = jsonObject.toJavaObject(DingDingRobotParam.Link.class);
            request.setLink(JSON.toJSONString(link));
            request.setMsgtype("link");
        }
        if (DDingDingSendMessageTypeConstants.MARKDOWN_NAME.equals(contentModel.getSendType())) {
            //markdown类型
            DingDingRobotParam.Markdown markdown = jsonObject.toJavaObject(DingDingRobotParam.Markdown.class);
            request.setMarkdown(JSON.toJSONString(markdown));
            request.setMsgtype("markdown");
        }
        if (DDingDingSendMessageTypeConstants.ACTION_CARD_NAME.equals(contentModel.getSendType())) {
            //actionCard类型
            DingDingRobotParam.ActionCard actionCard = jsonObject.toJavaObject(DingDingRobotParam.ActionCard.class);
            request.setActionCard(JSON.toJSONString(actionCard));
            request.setMsgtype("actionCard");
        }
        if (DDingDingSendMessageTypeConstants.FEED_CARD_NAME.equals(contentModel.getSendType())) {
            //feedCard类型
            DingDingRobotParam.FeedCard feedCard = jsonObject.toJavaObject(DingDingRobotParam.FeedCard.class);
            request.setFeedCard(JSON.toJSONString(feedCard));
            request.setMsgtype("feedCard");
        }
        request.setAt(at);
        OapiRobotSendResponse response;
        try {
            response = client.execute(request);
            if (!response.isSuccess()) {
                throw exception(
                        ErrorCodeConstants.DINGTALK_ROBOT_SEND_EXCEPTION,
                        response.getErrmsg()
                );
            }
        } catch (Exception e) {
            log.error("钉钉自定义机器人发送异常:{}", Throwables.getStackTraceAsString(e));
            throw exception(
                    ErrorCodeConstants.DINGTALK_ROBOT_SEND_EXCEPTION,
                    Throwables.getStackTraceAsString(e)
            );
        }
    }

    /**
     * 生成加签后的Webhook地址
     */
    @SneakyThrows
    public static String createSignUrl(DingDingRobotConfig config) {
        long timestamp = System.currentTimeMillis();
        String secret = config.getSecret();
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8);

        return (config.getWebhook() + "&timestamp=" + timestamp + "&sign=" + sign);
    }
}
