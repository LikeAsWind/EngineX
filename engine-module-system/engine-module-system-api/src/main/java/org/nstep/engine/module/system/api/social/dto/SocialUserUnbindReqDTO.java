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
 * 该类用于接收取消绑定社交平台用户的请求，包括用户编号、用户类型、社交平台类型和社交平台的 openid。
 */
@Schema(description = "RPC 服务 - 取消绑定社交用户 Request DTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserUnbindReqDTO {

    /**
     * 用户编号
     * <p>
     * 用于标识需要取消绑定社交平台的系统用户。
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
     * 社交平台的 openid
     * <p>
     * 用于标识社交平台上用户的唯一 ID。
     */
    @Schema(description = "社交平台的 openid", requiredMode = Schema.RequiredMode.REQUIRED, example = "zsw")
    @NotEmpty(message = "社交平台的 openid 不能为空")
    private String openid;

}
