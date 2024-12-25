package org.nstep.engine.module.system.controller.admin.auth.vo;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.nstep.engine.framework.common.validation.InEnum;
import org.nstep.engine.module.system.enums.social.SocialTypeEnum;

/**
 * 管理后台 - 账号密码登录 Request VO
 * <p>
 * 该类用于接收管理员登录请求，包含账号、密码以及验证码等信息。
 * 如果用户通过社交平台登录并绑定社交用户，还需要传递社交平台相关的参数。
 */
@Schema(description = "管理后台 - 账号密码登录 Request VO，如果登录并绑定社交用户，需要传递 social 开头的参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthLoginReqVO {

    /**
     * 登录账号
     * 账号必须为字母或数字，且长度在 4 到 16 个字符之间
     */
    @Schema(description = "账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    @NotEmpty(message = "登录账号不能为空")
    @Length(min = 4, max = 16, message = "账号长度为 4-16 位")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "账号格式为数字以及字母")
    private String username;

    /**
     * 登录密码
     * 密码长度必须在 4 到 16 个字符之间
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "buzhidao")
    @NotEmpty(message = "密码不能为空")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String password;

    // ========== 图片验证码相关 ==========

    /**
     * 图片验证码
     * 验证码开启时，必须传递验证码字段
     */
    @Schema(description = "验证码，验证码开启时，需要传递", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "PfcH6mgr8tpXuMWFjvW6YVaqrswIuwmWI5dsVZSg7sGpWtDCUbHuDEXl3cFB1+VvCC/rAkSwK8Fad52FSuncVg==")
    @NotEmpty(message = "验证码不能为空", groups = CodeEnableGroup.class)
    private String captchaVerification;

    // ========== 绑定社交登录时，需要传递如下参数 ==========

    /**
     * 社交平台的类型，参见 `SocialTypeEnum` 枚举值
     * 用于指定用户登录的社交平台类型，如微信、微博等
     */
    @Schema(description = "社交平台的类型，参见 SocialTypeEnum 枚举值", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @InEnum(SocialTypeEnum.class)
    private Integer socialType;

    /**
     * 社交平台授权码
     * 用于绑定社交平台账户时，传递授权码
     */
    @Schema(description = "授权码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private String socialCode;

    /**
     * 社交平台授权的 state 参数
     * 用于防止 CSRF 攻击，确保请求的合法性
     */
    @Schema(description = "state", requiredMode = Schema.RequiredMode.REQUIRED, example = "9b2ffbc1-7425-4155-9894-9d5c08541d62")
    private String socialState;

    /**
     * 校验授权码是否有效
     * 如果社交平台类型不为空，则授权码不能为空
     */
    @AssertTrue(message = "授权码不能为空")
    public boolean isSocialCodeValid() {
        return socialType == null || StrUtil.isNotEmpty(socialCode);
    }

    /**
     * 校验授权 state 是否有效
     * 如果社交平台类型不为空，则 state 不能为空
     */
    @AssertTrue(message = "授权 state 不能为空")
    public boolean isSocialState() {
        return socialType == null || StrUtil.isNotEmpty(socialState);
    }

    /**
     * 开启验证码的 Group，用于验证验证码字段的有效性
     */
    public interface CodeEnableGroup {
    }

}
