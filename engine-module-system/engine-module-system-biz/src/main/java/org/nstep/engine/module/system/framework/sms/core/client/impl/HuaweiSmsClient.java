package org.nstep.engine.module.system.framework.sms.core.client.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.core.KeyValue;
import org.nstep.engine.framework.common.util.http.HttpUtils;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.module.system.framework.sms.core.client.dto.SmsReceiveRespDTO;
import org.nstep.engine.module.system.framework.sms.core.client.dto.SmsSendRespDTO;
import org.nstep.engine.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import org.nstep.engine.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import org.nstep.engine.module.system.framework.sms.core.property.SmsChannelProperties;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static cn.hutool.crypto.digest.DigestUtil.sha256Hex;
import static org.nstep.engine.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 华为短信客户端的实现类
 *
 * @since 2024/6/02 11:55
 */
@Slf4j
public class HuaweiSmsClient extends AbstractSmsClient {

    private static final String URL = "https://smsapi.cn-north-4.myhuaweicloud.com:443/sms/batchSendSms/v1";//APP接入地址+接口访问URI
    private static final String HOST = "smsapi.cn-north-4.myhuaweicloud.com:443";
    private static final String SIGNEDHEADERS = "content-type;host;x-sdk-date";

    private static final String RESPONSE_CODE_SUCCESS = "000000";

    public HuaweiSmsClient(SmsChannelProperties properties) {
        super(properties);
        Assert.notEmpty(properties.getApiKey(), "apiKey 不能为空");
        Assert.notEmpty(properties.getApiSecret(), "apiSecret 不能为空");
        validateSender(properties);
    }

    /**
     * 参数校验华为云的 sender 通道号
     * <p>
     * 原因是：验华为云发放短信的时候，需要额外的参数 sender
     * <p>
     * 解决方案：考虑到不破坏原有的 apiKey + apiSecret 的结构，所以将 secretId 拼接到 apiKey 字段中，格式为 "secretId sdkAppId"。
     *
     * @param properties 配置
     */
    private static void validateSender(SmsChannelProperties properties) {
        String combineKey = properties.getApiKey();
        Assert.notEmpty(combineKey, "apiKey 不能为空");
        String[] keys = combineKey.trim().split(" ");
        Assert.isTrue(keys.length == 2, "华为云短信 apiKey 配置格式错误，请配置 为[accessKeyId sender]");
    }

    @SuppressWarnings("CharsetObjectCanBeUsed")
    private static void appendToBody(StringBuilder body, String key, String value) throws UnsupportedEncodingException {
        if (StrUtil.isNotEmpty(value)) {
            body.append(key).append(URLEncoder.encode(value, CharsetUtil.CHARSET_UTF_8.name()));
        }
    }

    private String getAccessKey() {
        return StrUtil.subBefore(properties.getApiKey(), " ", true);
    }

    private String getSender() {
        return StrUtil.subAfter(properties.getApiKey(), " ", true);
    }

    @Override
    public SmsSendRespDTO sendSms(Long sendLogId, String mobile, String apiTemplateId,
                                  List<KeyValue<String, Object>> templateParams) throws Throwable {
        StringBuilder requestBody = new StringBuilder();
        appendToBody(requestBody, "from=", getSender());
        appendToBody(requestBody, "&to=", mobile);
        appendToBody(requestBody, "&templateId=", apiTemplateId);
        appendToBody(requestBody, "&templateParas=", JsonUtils.toJsonString(
                convertList(templateParams, kv -> String.valueOf(kv.getValue()))));
        appendToBody(requestBody, "&statusCallback=", properties.getCallbackUrl());
        appendToBody(requestBody, "&extend=", String.valueOf(sendLogId));
        JSONObject response = request("/sms/batchSendSms/v1/", "POST", requestBody.toString());

        // 2. 解析请求
        if (!response.containsKey("result")) { // 例如说：密钥不正确
            SmsSendRespDTO smsSendRespDTO = new SmsSendRespDTO();
            smsSendRespDTO.setSuccess(false);
            smsSendRespDTO.setApiCode(response.getStr("code"));
            smsSendRespDTO.setApiMsg(response.getStr("description"));
            return smsSendRespDTO;
        }
        JSONObject sendResult = response.getJSONArray("result").getJSONObject(0);
        SmsSendRespDTO smsSendRespDTO = new SmsSendRespDTO();
        smsSendRespDTO.setSuccess(RESPONSE_CODE_SUCCESS.equals(response.getStr("code")));
        smsSendRespDTO.setSerialNo(sendResult.getStr("smsMsgId"));
        smsSendRespDTO.setApiCode(sendResult.getStr("status"));
        return smsSendRespDTO;
    }

    /**
     * 请求华为云短信
     *
     * @param uri         请求 URI
     * @param method      请求 Method
     * @param requestBody 请求 Body
     * @return 请求结果
     * @see <a href="认证鉴权">https://support.huaweicloud.com/api-msgsms/sms_05_0046.html</a>
     */
    private JSONObject request(String uri, String method, String requestBody) {
        // 1.1 请求 Header
        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        String sdkDate = FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("UTC")).format(new Date());
        headers.put("X-Sdk-Date", sdkDate);
        headers.put("host", HOST);

        // 1.2 构建签名 Header
        String canonicalQueryString = ""; // 查询参数为空
        String canonicalHeaders = "content-type:application/x-www-form-urlencoded\n"
                + "host:" + HOST + "\n" + "x-sdk-date:" + sdkDate + "\n";
        String canonicalRequest = method + "\n" + uri + "\n" + canonicalQueryString + "\n"
                + canonicalHeaders + "\n" + SIGNEDHEADERS + "\n" + sha256Hex(requestBody);
        String stringToSign = "SDK-HMAC-SHA256" + "\n" + sdkDate + "\n" + sha256Hex(canonicalRequest);
        String signature = SecureUtil.hmacSha256(properties.getApiSecret()).digestHex(stringToSign);  // 计算签名
        headers.put("Authorization", "SDK-HMAC-SHA256" + " " + "Access=" + getAccessKey()
                + ", " + "SignedHeaders=" + SIGNEDHEADERS + ", " + "Signature=" + signature);

        // 2. 发起请求
        String responseBody = HttpUtils.post(URL, headers, requestBody);
        return JSONUtil.parseObj(responseBody);
    }

    @Override
    public List<SmsReceiveRespDTO> parseSmsReceiveStatus(String requestBody) {
        Map<String, String> params = HttpUtil.decodeParamMap(requestBody, StandardCharsets.UTF_8);
        // 字段参考 https://support.huaweicloud.com/api-msgsms/sms_05_0003.html
        SmsReceiveRespDTO smsReceiveRespDTO = new SmsReceiveRespDTO();
        smsReceiveRespDTO.setSuccess("DELIVRD".equals(params.get("status"))); // 是否接收成功
        smsReceiveRespDTO.setErrorCode(params.get("status"));// 状态报告编码
        smsReceiveRespDTO.setErrorMsg(params.get("statusDesc"));
        smsReceiveRespDTO.setMobile(params.get("to"));// 手机号
        smsReceiveRespDTO.setReceiveTime(LocalDateTime.ofInstant(Instant.parse(params.get("updateTime")), ZoneId.of("UTC"))); // 状态报告时间
        smsReceiveRespDTO.setSerialNo(params.get("smsMsgId")); // 发送序列号
        smsReceiveRespDTO.setLogId(Long.valueOf(params.get("extend")));
        return ListUtil.of(smsReceiveRespDTO); // 用户序列号
    }

    @Override
    public SmsTemplateRespDTO getSmsTemplate(String apiTemplateId) throws Throwable {
        // 华为短信模板查询和发送短信，是不同的两套 key 和 secret，与阿里、腾讯的区别较大，这里模板查询校验暂不实现
        String[] strs = apiTemplateId.split(" ");
        Assert.isTrue(strs.length == 2, "格式不正确，需要满足：apiTemplateId sender");
        SmsTemplateRespDTO smsTemplateRespDTO = new SmsTemplateRespDTO();
        smsTemplateRespDTO.setId(apiTemplateId);
        smsTemplateRespDTO.setContent(null);
        smsTemplateRespDTO.setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus());
        smsTemplateRespDTO.setAuditReason(null);
        return smsTemplateRespDTO;
    }

}