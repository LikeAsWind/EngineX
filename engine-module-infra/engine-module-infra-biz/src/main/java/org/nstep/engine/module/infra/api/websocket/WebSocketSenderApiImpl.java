package org.nstep.engine.module.infra.api.websocket;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.websocket.core.sender.WebSocketMessageSender;
import org.nstep.engine.module.infra.api.websocket.dto.WebSocketSendReqDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * WebSocket 消息发送 API 实现类
 * <p>
 * 该类实现了 WebSocketSenderApi 接口，提供了 WebSocket 消息发送相关的 RESTful API 接口。
 * 通过 Feign 调用该接口时，可以向指定用户或 Session 发送 WebSocket 消息。
 * </p>
 */
@RestController // 提供 RESTful API 接口，供 Feign 调用
@Validated // 开启 Spring 的参数校验功能
public class WebSocketSenderApiImpl implements WebSocketSenderApi {

    @Resource
    private WebSocketMessageSender webSocketMessageSender; // 注入 WebSocketMessageSender，用于发送 WebSocket 消息

    /**
     * 发送 WebSocket 消息
     * <p>
     * 根据传入的消息内容，判断消息的目标，发送到指定的 Session 或用户。
     * </p>
     *
     * @param message WebSocket 消息发送请求 DTO，包含消息的详细信息
     * @return 返回操作结果，成功则返回 true
     */
    @Override
    public CommonResult<Boolean> send(WebSocketSendReqDTO message) {
        // 根据消息的 sessionId 判断发送目标
        if (StrUtil.isNotEmpty(message.getSessionId())) {
            // 通过 Session ID 发送消息
            webSocketMessageSender.send(message.getSessionId(),
                    message.getMessageType(), message.getMessageContent());
        } else if (message.getUserType() != null && message.getUserId() != null) {
            // 通过 用户类型和用户 ID 发送消息
            webSocketMessageSender.send(message.getUserType(), message.getUserId(),
                    message.getMessageType(), message.getMessageContent());
        } else if (message.getUserType() != null) {
            // 通过 用户类型 发送消息
            webSocketMessageSender.send(message.getUserType(),
                    message.getMessageType(), message.getMessageContent());
        }
        // 返回成功结果
        return success(true);
    }

}
