package org.nstep.engine.module.system.api.sms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import org.nstep.engine.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import org.nstep.engine.module.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 短信验证码")
public interface SmsCodeApi {

    String PREFIX = ApiConstants.PREFIX + "/oauth2/sms/code";

    @PostMapping(PREFIX + "/send")
    @Operation(summary = "创建短信验证码，并进行发送")
    CommonResult<Boolean> sendSmsCode(@Valid @RequestBody SmsCodeSendReqDTO reqDTO);

    @PutMapping(PREFIX + "/use")
    @Operation(summary = "验证短信验证码，并进行使用")
    CommonResult<Boolean> useSmsCode(@Valid @RequestBody SmsCodeUseReqDTO reqDTO);

    @GetMapping(PREFIX + "/validate")
    @Operation(summary = "检查验证码是否有效")
    CommonResult<Boolean> validateSmsCode(@Valid @RequestBody SmsCodeValidateReqDTO reqDTO);

}
