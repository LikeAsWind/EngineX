package org.nstep.engine.module.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nstep.engine.framework.common.validation.Mobile;

/**
 * 管理后台 - 短信验证码登录请求 VO
 * <p>
 * 该类用于封装短信验证码登录时的请求参数，包括手机号和短信验证码。
 * 主要用于后台系统的短信验证码登录接口。
 */
@Schema(description = "管理后台 - 短信验证码的登录 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSmsLoginReqVO {

    /**
     * 手机号
     * 用户的手机号，用于接收短信验证码。需要满足手机号格式。
     */
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    @NotEmpty(message = "手机号不能为空")
    @Mobile // 自定义验证手机号格式
    private String mobile;

    /**
     * 短信验证码
     * 用户通过短信接收到的验证码，用于验证用户身份。
     */
    @Schema(description = "短信验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "验证码不能为空")
    private String code;

}
