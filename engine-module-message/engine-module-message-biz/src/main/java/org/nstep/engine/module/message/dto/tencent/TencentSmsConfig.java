package org.nstep.engine.module.message.dto.tencent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 腾讯云短信配置类
 * 该类用于封装腾讯云短信服务的配置，包括接入地域、认证信息、短信应用ID等。
 */
@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class TencentSmsConfig {

    /**
     * 指定接入地域域名
     * 默认接入域名为 sms.tencentcloudapi.com，支持指定地域域名访问，例如广州地域的域名为 sms.ap-guangzhou.tencentcloudapi.com。
     * 可选配置项。
     */
    private String endpoint;

    /**
     * 地域信息
     * 可以直接填写字符串如 "ap-guangzhou" 来指定腾讯云短信服务的地域。
     * 支持的地域列表可以参考 <a href=https://cloud.tencent.com/document/api/382/52071#.E5.9C.B0.E5.9F.9F.E5.88.97.E8.A1.A8。></a>
     * 必选配置项。
     */
    private String region;

    /**
     * SecretId
     * 用于身份认证的密钥ID，查询方式请参考 <a href=https://console.cloud.tencent.com/cam/capi。></a>
     * 必选配置项。
     */
    private String secretId;

    /**
     * SecretKey
     * 用于身份认证的密钥，查询方式请参考 <a href=https://console.cloud.tencent.com/cam/capi。></a>
     * 必选配置项。
     */
    private String secretKey;

    /**
     * 短信应用ID
     * 短信SdkAppId是在 [短信控制台] 添加应用后生成的实际SdkAppId。
     * 必选配置项。
     */
    private String smsSdkAppId;

    /**
     * 模板参数
     * 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数，则设置为空。
     * 可选配置项。
     */
    private String templateId;

    /**
     * 短信签名内容
     * 使用 UTF-8 编码，必须填写已审核通过的短信签名。
     * 必选配置项。
     */
    private String signName;

    /**
     * 第三方服务名称
     * 可选配置项，用于标识使用的第三方服务名称。
     */
    private String serviceName;
}
