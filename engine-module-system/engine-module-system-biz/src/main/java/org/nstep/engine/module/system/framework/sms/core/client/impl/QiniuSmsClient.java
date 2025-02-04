package org.nstep.engine.module.system.framework.sms.core.client.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.core.KeyValue;
import org.nstep.engine.framework.common.util.http.HttpUtils;
import org.nstep.engine.module.system.framework.sms.core.client.dto.SmsReceiveRespDTO;
import org.nstep.engine.module.system.framework.sms.core.client.dto.SmsSendRespDTO;
import org.nstep.engine.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import org.nstep.engine.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import org.nstep.engine.module.system.framework.sms.core.property.SmsChannelProperties;

import java.util.*;
import java.util.function.Function;

import static org.nstep.engine.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 七牛云短信客户端的实现类
 *
 * @since 2024/08/26 15:35
 */
@Slf4j
public class QiniuSmsClient extends AbstractSmsClient {

    private static final String HOST = "sms.qiniuapi.com";

    public QiniuSmsClient(SmsChannelProperties properties) {
        super(properties);
        Assert.notEmpty(properties.getApiKey(), "apiKey 不能为空");
        Assert.notEmpty(properties.getApiSecret(), "apiSecret 不能为空");
    }

    public SmsSendRespDTO sendSms(Long sendLogId, String mobile, String apiTemplateId,
                                  List<KeyValue<String, Object>> templateParams) throws Throwable {
        // 1. 执行请求
        // 参考链接 https://developer.qiniu.com/sms/5824/through-the-api-send-text-messages
        LinkedHashMap<String, Object> body = new LinkedHashMap<>();
        body.put("template_id", apiTemplateId);
        body.put("mobile", mobile);
        body.put("parameters", CollStreamUtil.toMap(templateParams, KeyValue::getKey, KeyValue::getValue));
        body.put("seq", Long.toString(sendLogId));
        JSONObject response = request("POST", body, "/v1/message/single");

        // 2. 解析请求
        SmsSendRespDTO smsSendRespDTO = new SmsSendRespDTO();
        if (ObjectUtil.isNotEmpty(response.getStr("error"))) {
            // 短信请求失败
            smsSendRespDTO.setSuccess(false);
            smsSendRespDTO.setApiCode(response.getStr("error"));
            smsSendRespDTO.setApiRequestId(response.getStr("request_id"));
            smsSendRespDTO.setApiMsg(response.getStr("message"));

            return smsSendRespDTO;
        }

        smsSendRespDTO.setSuccess(response.containsKey("message_id"));
        smsSendRespDTO.setSerialNo(response.getStr("message_id"));
        return smsSendRespDTO;
    }

    /**
     * 请求七牛云短信
     *
     * @param httpMethod http请求方法
     * @param body       http请求消息体
     * @param path       URL path
     * @return 请求结果
     * @see <a href="https://developer.qiniu.com/sms/5842/sms-api-authentication"</>
     */
    private JSONObject request(String httpMethod, LinkedHashMap<String, Object> body, String path) {
        String signDate = DateUtil.date().setTimeZone(TimeZone.getTimeZone("UTC")).toString("yyyyMMdd'T'HHmmss'Z'");
        // 1. 请求头
        Map<String, String> header = new HashMap<>(4);
        header.put("HOST", HOST);
        header.put("Authorization", getSignature(httpMethod, path, body != null ? JSONUtil.toJsonStr(body) : "", signDate));
        header.put("Content-Type", "application/json");
        header.put("X-Qiniu-Date", signDate);

        // 2. 发起请求
        String responseBody;
        if (Objects.equals(httpMethod, "POST")) {
            responseBody = HttpUtils.post("https://" + HOST + path, header, JSONUtil.toJsonStr(body));
        } else {
            responseBody = HttpUtils.get("https://" + HOST + path, header);
        }
        return JSONUtil.parseObj(responseBody);
    }

    private String getSignature(String method, String path, String body, String signDate) {
        StringBuilder dataToSign = new StringBuilder();
        dataToSign.append(method.toUpperCase()).append(" ").append(path)
                .append("\nHost: ").append(HOST)
                .append("\n").append("Content-Type").append(": ").append("application/json")
                .append("\n").append("X-Qiniu-Date").append(": ").append(signDate)
                .append("\n\n");
        if (ObjectUtil.isNotEmpty(body)) {
            dataToSign.append(body);
        }
        String signature = SecureUtil.hmac(HmacAlgorithm.HmacSHA1, properties.getApiSecret())
                .digestBase64(dataToSign.toString(), true);
        return "Qiniu " + properties.getApiKey() + ":" + signature;
    }

    @Override
    public List<SmsReceiveRespDTO> parseSmsReceiveStatus(String text) {
        JSONObject status = JSONUtil.parseObj(text);
        // 字段参考 https://developer.qiniu.com/sms/5910/message-push
        return convertList(status.getJSONArray("items"), new Function<Object, SmsReceiveRespDTO>() {

            @Override
            public SmsReceiveRespDTO apply(Object item) {
                JSONObject statusObj = (JSONObject) item;
                SmsReceiveRespDTO smsReceiveRespDTO = new SmsReceiveRespDTO();
                smsReceiveRespDTO.setSuccess("DELIVRD".equals(statusObj.getStr("status")));// 是否接收成功
                smsReceiveRespDTO.setErrorMsg(statusObj.getStr("status"));// 状态报告编码
                smsReceiveRespDTO.setMobile(statusObj.getStr("mobile"));// 手机号
                smsReceiveRespDTO.setReceiveTime(LocalDateTimeUtil.of(statusObj.getLong("delivrd_at") * 1000L));// 状态报告时间
                smsReceiveRespDTO.setSerialNo(statusObj.getStr("message_id")); // 发送序列号
                smsReceiveRespDTO.setLogId(statusObj.getLong("seq")); // 用户序列号
                return smsReceiveRespDTO;
            }

        });
    }

    @Override
    public SmsTemplateRespDTO getSmsTemplate(String apiTemplateId) throws Throwable {
        // 1. 执行请求
        // 参考链接 https://developer.qiniu.com/sms/5969/query-a-single-template
        JSONObject response = request("GET", null, "/v1/template/" + apiTemplateId);

        // 2.2 解析请求
        SmsTemplateRespDTO smsTemplateRespDTO = new SmsTemplateRespDTO();
        smsTemplateRespDTO.setId(response.getStr("id"));
        smsTemplateRespDTO.setContent(response.getStr("template"));
        smsTemplateRespDTO.setAuditStatus(convertSmsTemplateAuditStatus(response.getStr("audit_status")));
        smsTemplateRespDTO.setAuditReason(response.getStr("reject_reason"));
        return smsTemplateRespDTO;
    }

    @VisibleForTesting
    Integer convertSmsTemplateAuditStatus(String templateStatus) {
        return switch (templateStatus) {
            case "passed" -> SmsTemplateAuditStatusEnum.SUCCESS.getStatus();
            case "reviewing" -> SmsTemplateAuditStatusEnum.CHECKING.getStatus();
            case "rejected" -> SmsTemplateAuditStatusEnum.FAIL.getStatus();
            default -> throw new IllegalArgumentException(String.format("未知审核状态(%str)", templateStatus));
        };
    }
}
