package org.nstep.engine.module.system.api.notify;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 站内信发送服务 API
 * <p>
 * 该接口提供了发送单条站内信给指定用户（Admin 或 Member）的功能。
 * 通过 RPC 服务，支持向 Admin 或 Member 用户发送站内信。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 站内信发送")
public interface NotifyMessageSendApi {

    String PREFIX = ApiConstants.PREFIX + "/notify/send";

    /**
     * 发送单条站内信给 Admin 用户
     * <p>
     * 该方法用于发送一条站内信给 Admin 用户，传入的请求数据中包含用户编号、模板编号和参数。
     *
     * @param reqDTO 请求数据传输对象，包含用户编号、模板编号和模板参数
     * @return 返回发送结果，包含发送的消息 ID
     */
    @PostMapping(PREFIX + "/send-single-admin")
    @Operation(summary = "发送单条站内信给 Admin 用户")
    CommonResult<Long> sendSingleMessageToAdmin(@Valid @RequestBody NotifySendSingleToUserReqDTO reqDTO);

    /**
     * 发送单条站内信给 Member 用户
     * <p>
     * 该方法用于发送一条站内信给 Member 用户，传入的请求数据中包含用户编号、模板编号和参数。
     *
     * @param reqDTO 请求数据传输对象，包含用户编号、模板编号和模板参数
     * @return 返回发送结果，包含发送的消息 ID
     */
    @PostMapping(PREFIX + "/send-single-member")
    @Operation(summary = "发送单条站内信给 Member 用户")
    CommonResult<Long> sendSingleMessageToMember(@Valid @RequestBody NotifySendSingleToUserReqDTO reqDTO);

}
