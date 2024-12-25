package org.nstep.engine.module.system.api.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nstep.engine.framework.common.enums.UserTypeEnum;
import org.nstep.engine.framework.common.validation.InEnum;
import org.nstep.engine.module.system.enums.social.SocialTypeEnum;

/**
 * 取消绑定社交用户的请求 DTO
 * <p>
 * 该类用于接收取消绑定社交账号时的请求数据，包括用户信息、社交平台类型、授权码等。
 */
@Schema(description = "RPC 服务 - 取消绑定社交用户 Request DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserBindReqDTO {

    /**
     * 用户编号
     * <p>
     * 用于标识用户的唯一编号。
     */
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    /**
     * 用户类型
     * <p>
     * 用于标识用户的类型，参见 `UserTypeEnum` 枚举。
     */
    @Schema(description = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @InEnum(UserTypeEnum.class)
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    /**
     * 社交平台的类型
     * <p>
     * 用于标识社交平台的类型，参见 `SocialTypeEnum` 枚举。
     */
    @Schema(description = "社交平台的类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @InEnum(SocialTypeEnum.class)
    @NotNull(message = "社交平台的类型不能为空")
    private Integer socialType;

    /**
     * 授权码
     * <p>
     * 用于社交平台的授权验证。
     */
    @Schema(description = "授权码", requiredMode = Schema.RequiredMode.REQUIRED, example = "zsw")
    @NotEmpty(message = "授权码不能为空")
    private String code;

    /**
     * state 参数
     * <p>
     * 用于防止 CSRF 攻击的状态标识。
     */
    @Schema(description = "state", requiredMode = Schema.RequiredMode.REQUIRED, example = "qtw")
    @NotEmpty(message = "state 不能为空")
    private String state;

}
