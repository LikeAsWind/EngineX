package org.nstep.engine.module.system.api.mail;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.mail.dto.MailSendSingleToUserReqDTO;
import org.nstep.engine.module.system.service.mail.MailSendService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 邮件发送 API 实现类
 * <p>
 * 该类提供了发送邮件的 RESTful API 接口，供 Feign 调用。
 * 主要功能包括向管理员和会员发送单封邮件。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class MailSendApiImpl implements MailSendApi {

    @Resource
    private MailSendService mailSendService; // 邮件发送服务，处理邮件发送的业务逻辑

    /**
     * 向管理员发送单封邮件
     *
     * @param reqDTO 邮件发送请求 DTO，包含邮件地址、用户 ID、模板编码及模板参数
     * @return 发送邮件的任务 ID
     */
    @Override
    public CommonResult<Long> sendSingleMailToAdmin(MailSendSingleToUserReqDTO reqDTO) {
        // 调用邮件发送服务向管理员发送邮件，并返回任务 ID
        return success(mailSendService.sendSingleMailToAdmin(reqDTO.getMail(), reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams()));
    }

    /**
     * 向会员发送单封邮件
     *
     * @param reqDTO 邮件发送请求 DTO，包含邮件地址、用户 ID、模板编码及模板参数
     * @return 发送邮件的任务 ID
     */
    @Override
    public CommonResult<Long> sendSingleMailToMember(MailSendSingleToUserReqDTO reqDTO) {
        // 调用邮件发送服务向会员发送邮件，并返回任务 ID
        return success(mailSendService.sendSingleMailToMember(reqDTO.getMail(), reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams()));
    }

}
