package org.nstep.engine.module.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 管理后台 - 注册请求 VO
 * <p>
 * 该类用于封装用户注册请求时的参数，包括用户账号、昵称、密码以及验证码等信息。
 * 主要用于后台系统的用户注册接口。
 */
@Schema(description = "管理后台 - Register Request VO")
@Data
public class AuthRegisterReqVO {

    /**
     * 用户账号
     * 用户在系统中的唯一标识符，要求由数字和字母组成，长度为 4 到 30 个字符
     */
    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    @NotBlank(message = "用户账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,30}$", message = "用户账号由 数字、字母 组成")
    @Size(min = 4, max = 30, message = "用户账号长度为 4-30 个字符")
    private String username;

    /**
     * 用户昵称
     * 用户的显示名称，不能超过 30 个字符
     */
    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    @NotBlank(message = "用户昵称不能为空")
    @Size(max = 30, message = "用户昵称长度不能超过 30 个字符")
    private String nickname;

    /**
     * 密码
     * 用户的登录密码，长度为 4 到 16 位
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "密码不能为空")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String password;

    // ========== 图片验证码相关 ==========

    /**
     * 验证码
     * 如果验证码开启，用户需要传递验证码值。该字段不能为空。
     */
    @Schema(description = "验证码，验证码开启时，需要传递", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "PfcH6mgr8tpXuMWFjvW6YVaqrswIuwmWI5dsVZSg7sGpWtDCUbHuDEXl3cFB1+VvCC/rAkSwK8Fad52FSuncVg==")
    @NotEmpty(message = "验证码不能为空", groups = AuthLoginReqVO.CodeEnableGroup.class)
    private String captchaVerification;

}
