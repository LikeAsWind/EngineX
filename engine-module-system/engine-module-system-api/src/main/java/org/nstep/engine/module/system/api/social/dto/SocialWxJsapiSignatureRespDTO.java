package org.nstep.engine.module.system.api.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 微信公众号 JSAPI 签名响应 DTO
 * <p>
 * 该类用于返回微信公众号 JSAPI 签名所需的参数，包括 appId、nonceStr、timestamp、url 和 signature。
 */
@Schema(description = "RPC 服务 - 微信公众号 JSAPI 签名 Response DTO")
@Data
public class SocialWxJsapiSignatureRespDTO {

    /**
     * 微信公众号的 appId
     * <p>
     * 用于标识微信公众号的唯一标识符。
     */
    @Schema(description = "微信公众号的 appId", requiredMode = Schema.RequiredMode.REQUIRED, example = "wx123456")
    private String appId;

    /**
     * 匿名串
     * <p>
     * 用于生成签名的随机字符串，保证签名的唯一性。
     */
    @Schema(description = "匿名串", requiredMode = Schema.RequiredMode.REQUIRED, example = "zsw")
    private String nonceStr;

    /**
     * 时间戳
     * <p>
     * 用于生成签名的时间戳，确保签名的时效性。
     */
    @Schema(description = "时间戳", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456789")
    private Long timestamp;

    /**
     * URL
     * <p>
     * 用于生成签名的 URL 地址，通常为网页的链接。
     */
    @Schema(description = "URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.nstep.cn")
    private String url;

    /**
     * 签名
     * <p>
     * 微信公众号 JSAPI 的签名，用于验证请求的合法性。
     */
    @Schema(description = "签名", requiredMode = Schema.RequiredMode.REQUIRED, example = "zsw")
    private String signature;

}
