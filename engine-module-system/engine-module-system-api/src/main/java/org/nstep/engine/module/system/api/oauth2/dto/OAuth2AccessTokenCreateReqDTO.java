package org.nstep.engine.module.system.api.oauth2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.nstep.engine.framework.common.enums.UserTypeEnum;
import org.nstep.engine.framework.common.validation.InEnum;

import java.io.Serializable;
import java.util.List;

/**
 * OAuth2 访问令牌创建请求 DTO
 * <p>
 * 该类用于表示 OAuth2 访问令牌创建请求的数据传输对象。包含用户信息、客户端信息以及授权范围等。
 * 在创建访问令牌时，通过该 DTO 提供必要的参数。
 */
@Schema(description = "RPC 服务 - OAuth2 访问令牌创建 Request DTO")
@Data
public class OAuth2AccessTokenCreateReqDTO implements Serializable {

    /**
     * 用户编号
     * <p>
     * 表示请求访问令牌的用户的唯一标识符。
     */
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    /**
     * 用户类型
     * <p>
     * 参见 `UserTypeEnum` 枚举，表示用户的类型，如普通用户、管理员等。
     */
    @Schema(description = "用户类型，参见 UserTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户类型不能为空")
    @InEnum(value = UserTypeEnum.class, message = "用户类型必须是 {value}")
    private Integer userType;

    /**
     * 客户端编号
     * <p>
     * 表示请求访问令牌的客户端的唯一标识符。
     */
    @Schema(description = "客户端编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    @NotNull(message = "客户端编号不能为空")
    private String clientId;

    /**
     * 授权范围
     * <p>
     * 表示该访问令牌授权的范围，通常是一些权限或访问的资源列表。
     */
    @Schema(description = "授权范围的数组", example = "user_info")
    private List<String> scopes;

}
