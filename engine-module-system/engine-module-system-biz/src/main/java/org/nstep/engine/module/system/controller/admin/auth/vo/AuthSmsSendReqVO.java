package org.nstep.engine.module.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nstep.engine.framework.common.validation.InEnum;
import org.nstep.engine.framework.common.validation.Mobile;
import org.nstep.engine.module.system.enums.sms.SmsSceneEnum;

/**
 * 管理后台 - 发送手机验证码请求 VO
 * <p>
 * 该类用于封装发送手机验证码请求时的参数，包括手机号和短信场景。
 * 主要用于后台系统的发送验证码接口。
 */
@Schema(description = "管理后台 - 发送手机验证码 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSmsSendReqVO {

    /**
     * 手机号
     * 用户的手机号，用于接收短信验证码。需要满足手机号格式。
     */
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    @NotEmpty(message = "手机号不能为空")
    @Mobile // 自定义验证手机号格式
    private String mobile;

    /**
     * 短信场景
     * 用于区分不同的短信验证码使用场景，例如登录、注册等。
     * 必须是 `SmsSceneEnum` 枚举中的有效值。
     */
    @Schema(description = "短信场景", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "发送场景不能为空")
    @InEnum(SmsSceneEnum.class) // 校验场景是否属于 SmsSceneEnum 枚举值
    private Integer scene;

}
