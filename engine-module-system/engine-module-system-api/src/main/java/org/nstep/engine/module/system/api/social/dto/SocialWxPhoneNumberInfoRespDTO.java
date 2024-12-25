package org.nstep.engine.module.system.api.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 微信小程序的手机信息响应 DTO
 * <p>
 * 该类用于返回微信小程序获取到的用户手机信息，包括手机号、纯手机号和区号。
 */
@Schema(description = "RPC 服务 - 微信小程序的手机信息 Response DTO")
@Data
public class SocialWxPhoneNumberInfoRespDTO {

    /**
     * 用户绑定的手机号（国外手机号会有区号）
     * <p>
     * 返回的手机号可能包含区号，适用于国内外不同的手机号格式。
     */
    @Schema(description = "用户绑定的手机号（国外手机号会有区号）", requiredMode = Schema.RequiredMode.REQUIRED, example = "021-13579246810")
    private String phoneNumber;

    /**
     * 没有区号的手机号
     * <p>
     * 该字段返回去除区号后的手机号，适用于统一处理手机号格式。
     */
    @Schema(description = "没有区号的手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13579246810")
    private String purePhoneNumber;

    /**
     * 区号
     * <p>
     * 返回手机号的区号，适用于区分不同国家或地区的手机号。
     */
    @Schema(description = "区号", requiredMode = Schema.RequiredMode.REQUIRED, example = "021")
    private String countryCode;

}
