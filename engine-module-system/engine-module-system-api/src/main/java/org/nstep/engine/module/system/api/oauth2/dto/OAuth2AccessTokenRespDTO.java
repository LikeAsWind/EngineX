package org.nstep.engine.module.system.api.oauth2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * OAuth2 访问令牌响应 DTO
 * <p>
 * 该类用于表示 OAuth2 访问令牌的响应数据。包含访问令牌、刷新令牌、用户信息以及过期时间等。
 * 在 OAuth2 认证过程中，通过该 DTO 返回访问令牌的相关信息。
 */
@Schema(description = "RPC 服务 - OAuth2 访问令牌的信息 Response DTO")
@Data
@Accessors(chain = true)
public class OAuth2AccessTokenRespDTO implements Serializable {

    /**
     * 访问令牌
     * <p>
     * 表示用于授权访问资源的访问令牌。
     */
    @Schema(description = "访问令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "tudou")
    private String accessToken;

    /**
     * 刷新令牌
     * <p>
     * 用于在访问令牌过期后重新获取新的访问令牌。
     */
    @Schema(description = "刷新令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "haha")
    private String refreshToken;

    /**
     * 用户编号
     * <p>
     * 表示访问令牌对应的用户的唯一标识符。
     */
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long userId;

    /**
     * 用户类型
     * <p>
     * 参见 `UserTypeEnum` 枚举，表示用户的类型，如普通用户、管理员等。
     */
    @Schema(description = "用户类型，参见 UserTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer userType;

    /**
     * 过期时间
     * <p>
     * 表示访问令牌的过期时间，过期后访问令牌将不再有效。
     */
    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresTime;

}
