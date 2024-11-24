package org.nstep.engine.module.system.api.sms.dto.code;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.nstep.engine.framework.common.validation.InEnum;
import org.nstep.engine.framework.common.validation.Mobile;
import org.nstep.engine.module.system.enums.sms.SmsSceneEnum;

@Schema(description = "RPC 服务 - 短信验证码的校验 Request DTO")
@Data
public class SmsCodeValidateReqDTO {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @Mobile
    @NotEmpty(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "发送场景", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "发送场景不能为空")
    @InEnum(SmsSceneEnum.class)
    private Integer scene;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "验证码")
    private String code;

}
