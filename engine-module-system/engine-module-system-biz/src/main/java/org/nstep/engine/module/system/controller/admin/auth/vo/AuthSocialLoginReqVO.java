package org.nstep.engine.module.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nstep.engine.framework.common.validation.InEnum;
import org.nstep.engine.module.system.enums.social.SocialTypeEnum;

/**
 * 管理后台 - 社交绑定登录请求 VO
 * <p>
 * 该类用于封装社交绑定登录请求的参数，主要包含社交平台类型、授权码和 state 参数。
 * 适用于通过社交平台登录并绑定账号的场景。
 */
@Schema(description = "管理后台 - 社交绑定登录 Request VO，使用 code 授权码 + 账号密码")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSocialLoginReqVO {

    /**
     * 社交平台类型
     * 用于标识社交平台的类型，必须是 `SocialTypeEnum` 枚举中的有效值。
     * 例如，微信、微博等平台的类型编号。
     */
    @Schema(description = "社交平台的类型，参见 UserSocialTypeEnum 枚举值", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @InEnum(SocialTypeEnum.class) // 校验社交平台类型是否有效
    @NotNull(message = "社交平台的类型不能为空")
    private Integer type;

    /**
     * 授权码
     * 由社交平台返回的授权码，用于交换用户的授权信息。
     * 必须提供有效的授权码。
     */
    @Schema(description = "授权码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "授权码不能为空")
    private String code;

    /**
     * state
     * 用于防止跨站请求伪造（CSRF）攻击，社交平台返回的一个随机值。
     * 必须与发起请求时传递的 state 值一致。
     */
    @Schema(description = "state", requiredMode = Schema.RequiredMode.REQUIRED, example = "9b2ffbc1-7425-4155-9894-9d5c08541d62")
    @NotEmpty(message = "state 不能为空")
    private String state;

}
