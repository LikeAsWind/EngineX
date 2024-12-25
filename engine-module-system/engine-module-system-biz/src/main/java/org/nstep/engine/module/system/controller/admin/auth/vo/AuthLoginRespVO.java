package org.nstep.engine.module.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理后台 - 登录 Response VO
 * <p>
 * 该类用于封装用户登录成功后返回的响应信息，包括用户编号、访问令牌、刷新令牌和过期时间等。
 */
@Schema(description = "管理后台 - 登录 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthLoginRespVO {

    /**
     * 用户编号
     * 登录成功后返回的用户唯一标识
     */
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

    /**
     * 访问令牌
     * 用于身份验证和授权，通常在每次请求时需要携带
     */
    @Schema(description = "访问令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "happy")
    private String accessToken;

    /**
     * 刷新令牌
     * 用于刷新访问令牌，获取新的访问令牌
     */
    @Schema(description = "刷新令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "nice")
    private String refreshToken;

    /**
     * 过期时间
     * 访问令牌的过期时间，通常是一个时间戳，表示访问令牌的有效期
     */
    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresTime;

}
