package org.nstep.engine.module.system.api.sms;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import org.nstep.engine.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import org.nstep.engine.module.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import org.nstep.engine.module.system.service.sms.SmsCodeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 短信验证码 API 实现类
 * <p>
 * 该类提供了短信验证码相关的 RESTful API 接口，主要用于发送、使用和验证短信验证码。
 * 提供给 Feign 调用，处理与短信验证码相关的业务逻辑。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class SmsCodeApiImpl implements SmsCodeApi {

    @Resource
    private SmsCodeService smsCodeService; // 短信验证码服务，处理与短信验证码相关的业务逻辑

    /**
     * 发送短信验证码
     *
     * @param reqDTO 请求参数，包含发送验证码的相关信息
     * @return 是否发送成功
     */
    @Override
    public CommonResult<Boolean> sendSmsCode(SmsCodeSendReqDTO reqDTO) {
        // 调用短信验证码服务发送验证码
        smsCodeService.sendSmsCode(reqDTO);
        return success(true);
    }

    /**
     * 使用短信验证码
     *
     * @param reqDTO 请求参数，包含使用验证码的相关信息
     * @return 是否使用成功
     */
    @Override
    public CommonResult<Boolean> useSmsCode(SmsCodeUseReqDTO reqDTO) {
        // 调用短信验证码服务使用验证码
        smsCodeService.useSmsCode(reqDTO);
        return success(true);
    }

    /**
     * 验证短信验证码
     *
     * @param reqDTO 请求参数，包含验证验证码的相关信息
     * @return 是否验证成功
     */
    @Override
    public CommonResult<Boolean> validateSmsCode(SmsCodeValidateReqDTO reqDTO) {
        // 调用短信验证码服务验证验证码
        smsCodeService.validateSmsCode(reqDTO);
        return success(true);
    }
}
