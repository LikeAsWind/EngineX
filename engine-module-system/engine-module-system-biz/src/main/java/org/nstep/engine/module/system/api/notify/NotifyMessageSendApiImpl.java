package org.nstep.engine.module.system.api.notify;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import org.nstep.engine.module.system.service.notify.NotifySendService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 通知消息发送 API 实现类
 * <p>
 * 该类提供了发送通知消息的 RESTful API 接口，供 Feign 调用。
 * 主要功能包括向管理员和会员发送单条通知消息。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class NotifyMessageSendApiImpl implements NotifyMessageSendApi {

    @Resource
    private NotifySendService notifySendService; // 通知消息发送服务，处理通知消息发送的业务逻辑

    /**
     * 向管理员发送单条通知消息
     *
     * @param reqDTO 通知发送请求 DTO，包含用户 ID、模板编码及模板参数
     * @return 发送通知消息的任务 ID
     */
    @Override
    public CommonResult<Long> sendSingleMessageToAdmin(NotifySendSingleToUserReqDTO reqDTO) {
        // 调用通知发送服务向管理员发送通知消息，并返回任务 ID
        return success(notifySendService.sendSingleNotifyToAdmin(reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams()));
    }

    /**
     * 向会员发送单条通知消息
     *
     * @param reqDTO 通知发送请求 DTO，包含用户 ID、模板编码及模板参数
     * @return 发送通知消息的任务 ID
     */
    @Override
    public CommonResult<Long> sendSingleMessageToMember(NotifySendSingleToUserReqDTO reqDTO) {
        // 调用通知发送服务向会员发送通知消息，并返回任务 ID
        return success(notifySendService.sendSingleNotifyToMember(reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams()));
    }

}
