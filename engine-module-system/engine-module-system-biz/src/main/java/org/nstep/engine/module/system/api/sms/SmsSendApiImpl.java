package org.nstep.engine.module.system.api.sms;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import org.nstep.engine.module.system.service.sms.SmsSendService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 短信发送 API 实现类
 * <p>
 * 该类提供了短信发送相关的 RESTful API 接口，主要用于向管理员和会员发送单条短信。
 * 提供给 Feign 调用，处理与短信发送相关的业务逻辑。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class SmsSendApiImpl implements SmsSendApi {

    @Resource
    private SmsSendService smsSendService; // 短信发送服务，处理与短信发送相关的业务逻辑

    /**
     * 向管理员发送单条短信
     *
     * @param reqDTO 请求参数，包含短信发送的相关信息
     * @return 发送短信的任务 ID
     */
    @Override
    public CommonResult<Long> sendSingleSmsToAdmin(SmsSendSingleToUserReqDTO reqDTO) {
        // 调用短信发送服务发送短信到管理员
        return success(smsSendService.sendSingleSmsToAdmin(reqDTO.getMobile(), reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams()));
    }

    /**
     * 向会员发送单条短信
     *
     * @param reqDTO 请求参数，包含短信发送的相关信息
     * @return 发送短信的任务 ID
     */
    @Override
    public CommonResult<Long> sendSingleSmsToMember(SmsSendSingleToUserReqDTO reqDTO) {
        // 调用短信发送服务发送短信到会员
        return success(smsSendService.sendSingleSmsToMember(reqDTO.getMobile(), reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams()));
    }

}
