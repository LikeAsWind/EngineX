package org.nstep.engine.module.system.api.oauth2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 访问令牌的校验响应 DTO
 * <p>
 * 该类用于表示 OAuth2 访问令牌校验的响应数据。包含用户信息、授权范围、租户信息及令牌的过期时间等。
 * 在校验令牌时，返回该 DTO 以提供相应的校验结果。
 */
@Schema(description = "RPC 服务 - OAuth2 访问令牌的校验 Response DTO")
@Data
public class OAuth2AccessTokenCheckRespDTO implements Serializable {

    /**
     * 用户编号
     * <p>
     * 表示经过授权的用户的唯一标识符。
     */
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long userId;

    /**
     * 用户类型
     * <p>
     * 参见 UserTypeEnum 枚举，表示用户的类型，如普通用户、管理员等。
     */
    @Schema(description = "用户类型，参见 UserTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer userType;

    /**
     * 用户信息
     * <p>
     * 包含用户的附加信息，如昵称、邮箱等。以键值对的形式存储。
     */
    @Schema(description = "用户信息", example = "{\"nickname\": \"engine\"}")
    private Map<String, String> userInfo;

    /**
     * 租户编号
     * <p>
     * 表示该用户所属的租户的唯一标识符。
     */
    @Schema(description = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long tenantId;

    /**
     * 授权范围
     * <p>
     * 表示该访问令牌授权的范围。通常是一些权限或访问的资源列表。
     */
    @Schema(description = "授权范围的数组", example = "user_info")
    private List<String> scopes;

    /**
     * 令牌过期时间
     * <p>
     * 表示该 OAuth2 访问令牌的过期时间。
     */
    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresTime;

}
