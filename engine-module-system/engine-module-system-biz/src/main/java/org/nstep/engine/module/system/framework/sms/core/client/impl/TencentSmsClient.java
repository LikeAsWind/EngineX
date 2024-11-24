package org.nstep.engine.module.system.framework.sms.core.client.impl;

import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.annotations.VisibleForTesting;
import org.nstep.engine.framework.common.core.KeyValue;
import org.nstep.engine.framework.common.util.collection.ArrayUtils;
import org.nstep.engine.framework.common.util.http.HttpUtils;
import org.nstep.engine.module.system.framework.sms.core.client.dto.SmsReceiveRespDTO;
import org.nstep.engine.module.system.framework.sms.core.client.dto.SmsSendRespDTO;
import org.nstep.engine.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import org.nstep.engine.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import org.nstep.engine.module.system.framework.sms.core.property.SmsChannelProperties;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static cn.hutool.crypto.digest.DigestUtil.sha256Hex;
import static org.nstep.engine.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 腾讯云短信功能实现
 * <p>
 * 参见 <a href="https://cloud.tencent.com/document/product/382/52077">文档</a>
 */
public class TencentSmsClient extends AbstractSmsClient {

    /**
     * 调用成功 code
     */
    public static final String API_CODE_SUCCESS = "Ok";
    private static final String HOST = "sms.tencentcloudapi.com";
    private static final String VERSION = "2021-01-11";
    private static final String REGION = "ap-guangzhou";
    /**
     * 是否国际/港澳台短信：
     * <p>
     * 0：表示国内短信。
     * 1：表示国际/港澳台短信。
     */
    private static final long INTERNATIONAL_CHINA = 0L;

    public TencentSmsClient(SmsChannelProperties properties) {
        super(properties);
        Assert.notEmpty(properties.getApiSecret(), "apiSecret 不能为空");
        validateSdkAppId(properties);
    }

    /**
     * 参数校验腾讯云的 SDK AppId
     * <p>
     * 原因是：腾讯云发放短信的时候，需要额外的参数 sdkAppId
     * <p>
     * 解决方案：考虑到不破坏原有的 apiKey + apiSecret 的结构，所以将 secretId 拼接到 apiKey 字段中，格式为 "secretId sdkAppId"。
     *
     * @param properties 配置
     */
    private static void validateSdkAppId(SmsChannelProperties properties) {
        String combineKey = properties.getApiKey();
        Assert.notEmpty(combineKey, "apiKey 不能为空");
        String[] keys = combineKey.trim().split(" ");
        Assert.isTrue(keys.length == 2, "腾讯云短信 apiKey 配置格式错误，请配置 为[secretId sdkAppId]");
    }

    private static byte[] hmac256(byte[] key, String msg) {
        return DigestUtil.hmac(HmacAlgorithm.HmacSHA256, key).digest(msg);
    }

    private String getSdkAppId() {
        return StrUtil.subAfter(properties.getApiKey(), " ", true);
    }

    private String getApiKey() {
        return StrUtil.subBefore(properties.getApiKey(), " ", true);
    }

    @Override
    public SmsSendRespDTO sendSms(Long sendLogId, String mobile,
                                  String apiTemplateId, List<KeyValue<String, Object>> templateParams) throws Throwable {
        // 1. 执行请求
        // 参考链接 https://cloud.tencent.com/document/product/382/55981
        TreeMap<String, Object> body = new TreeMap<>();
        body.put("PhoneNumberSet", new String[]{mobile});
        body.put("SmsSdkAppId", getSdkAppId());
        body.put("SignName", properties.getSignature());
        body.put("TemplateId", apiTemplateId);
        body.put("TemplateParamSet", ArrayUtils.toArray(templateParams, param -> String.valueOf(param.getValue())));
        JSONObject response = request("SendSms", body);

        // 2. 解析请求
        JSONObject responseResult = response.getJSONObject("Response");
        JSONObject error = responseResult.getJSONObject("Error");
        SmsSendRespDTO smsSendRespDTO = new SmsSendRespDTO();
        if (error != null) {
            smsSendRespDTO.setSuccess(false);
            smsSendRespDTO.setApiRequestId(responseResult.getStr("RequestId"));
            smsSendRespDTO.setApiCode(error.getStr("Code"));
            smsSendRespDTO.setApiMsg(error.getStr("Message"));
            return smsSendRespDTO;
        }
        JSONObject sendResult = responseResult.getJSONArray("SendStatusSet").getJSONObject(0);
        smsSendRespDTO.setSuccess(Objects.equals(API_CODE_SUCCESS, sendResult.getStr("Code")));
        smsSendRespDTO.setApiRequestId(responseResult.getStr("RequestId"));
        smsSendRespDTO.setSerialNo(sendResult.getStr("SerialNo"));
        smsSendRespDTO.setApiMsg(sendResult.getStr("Message"));


        return smsSendRespDTO;
    }

    @Override
    public List<SmsReceiveRespDTO> parseSmsReceiveStatus(String text) {
        JSONArray statuses = JSONUtil.parseArray(text);
        // 字段参考
        return convertList(statuses, status -> {
            JSONObject statusObj = (JSONObject) status;
            SmsReceiveRespDTO smsReceiveRespDTO = new SmsReceiveRespDTO();

            smsReceiveRespDTO.setSuccess("SUCCESS".equals(statusObj.getStr("report_status"))); // 是否接收成功
            smsReceiveRespDTO.setErrorCode(statusObj.getStr("errmsg")); // 状态报告编码
            smsReceiveRespDTO.setMobile(statusObj.getStr("mobile")); // 手机号
            smsReceiveRespDTO.setReceiveTime(statusObj.getLocalDateTime("user_receive_time", null)); // 状态报告时间
            smsReceiveRespDTO.setSerialNo(statusObj.getStr("sid")); // 发送序列号
            return smsReceiveRespDTO;
        });
    }

    @Override
    public SmsTemplateRespDTO getSmsTemplate(String apiTemplateId) throws Throwable {
        // 1. 构建请求
        // 参考链接 https://cloud.tencent.com/document/product/382/52067
        TreeMap<String, Object> body = new TreeMap<>();
        body.put("International", INTERNATIONAL_CHINA);
        body.put("TemplateIdSet", new Integer[]{Integer.valueOf(apiTemplateId)});
        JSONObject response = request("DescribeSmsTemplateList", body);

        // 2. 解析请求
        JSONObject statusResult = response.getJSONObject("Response")
                .getJSONArray("DescribeTemplateStatusSet").getJSONObject(0);

        SmsTemplateRespDTO smsTemplateRespDTO = new SmsTemplateRespDTO();
        smsTemplateRespDTO.setId(apiTemplateId);
        smsTemplateRespDTO.setContent(statusResult.get("TemplateContent").toString());
        smsTemplateRespDTO.setAuditStatus(convertSmsTemplateAuditStatus(statusResult.getInt("StatusCode")));
        smsTemplateRespDTO.setAuditReason(statusResult.get("ReviewReply").toString());
        return smsTemplateRespDTO;
    }

    @VisibleForTesting
    Integer convertSmsTemplateAuditStatus(int templateStatus) {
        switch (templateStatus) {
            case 1:
                return SmsTemplateAuditStatusEnum.CHECKING.getStatus();
            case 0:
                return SmsTemplateAuditStatusEnum.SUCCESS.getStatus();
            case -1:
                return SmsTemplateAuditStatusEnum.FAIL.getStatus();
            default:
                throw new IllegalArgumentException(String.format("未知审核状态(%d)", templateStatus));
        }
    }

    /**
     * 请求腾讯云短信
     *
     * @param action 请求的 API 名称
     * @param body   请求参数
     * @return 请求结果
     * @see <a href="https://cloud.tencent.com/document/product/382/52072">签名方法 v3</a>
     */
    private JSONObject request(String action, TreeMap<String, Object> body) {
        // 1.1 请求 Header
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Host", HOST);
        headers.put("X-TC-Action", action);
        Date now = new Date();
        String nowStr = FastDateFormat.getInstance("yyyy-MM-dd", TimeZone.getTimeZone("UTC")).format(now);
        headers.put("X-TC-Timestamp", String.valueOf(now.getTime() / 1000));
        headers.put("X-TC-Version", VERSION);
        headers.put("X-TC-Region", REGION);

        // 1.2 构建签名 Header
        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:application/json; charset=utf-8\n"
                + "host:" + HOST + "\n" + "x-tc-action:" + action.toLowerCase() + "\n";
        String signedHeaders = "content-type;host;x-tc-action";
        String canonicalRequest = "POST" + "\n" + "/" + "\n" + canonicalQueryString + "\n" + canonicalHeaders + "\n"
                + signedHeaders + "\n" + sha256Hex(JSONUtil.toJsonStr(body));
        String credentialScope = nowStr + "/" + "sms" + "/" + "tc3_request";
        String stringToSign = "TC3-HMAC-SHA256" + "\n" + now.getTime() / 1000 + "\n" + credentialScope + "\n" +
                sha256Hex(canonicalRequest);
        byte[] secretService = hmac256(hmac256(("TC3" + properties.getApiSecret()).getBytes(StandardCharsets.UTF_8), nowStr), "sms");
        String signature = HexUtil.encodeHexStr(hmac256(hmac256(secretService, "tc3_request"), stringToSign));
        headers.put("Authorization", "TC3-HMAC-SHA256" + " " + "Credential=" + getApiKey() + "/" + credentialScope + ", "
                + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature);

        // 2. 发起请求
        String responseBody = HttpUtils.post("https://" + HOST, headers, JSONUtil.toJsonStr(body));
        return JSONUtil.parseObj(responseBody);
    }

}