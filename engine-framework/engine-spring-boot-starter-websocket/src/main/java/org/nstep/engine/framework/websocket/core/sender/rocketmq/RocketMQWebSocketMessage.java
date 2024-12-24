package org.nstep.engine.framework.websocket.core.sender.rocketmq;

import lombok.Data;

/**
 * RocketMQ 广播 WebSocket 的消息
 * <p>
 * 该类用于封装通过 RocketMQ 广播的 WebSocket 消息。它包含了 WebSocket 会话编号、用户信息、消息类型和消息内容等字段。
 */
@Data
public class RocketMQWebSocketMessage {

    /**
     * Session 编号
     * <p>
     * 该字段用于标识 WebSocket 会话的唯一标识符。
     */
    private String sessionId;

    /**
     * 用户类型
     * <p>
     * 该字段表示用户的类型，可以是不同角色的标识，例如管理员、普通用户等。
     */
    private Integer userType;

    /**
     * 用户编号
     * <p>
     * 该字段表示用户的唯一编号，用于标识发送消息的用户。
     */
    private Long userId;

    /**
     * 消息类型
     * <p>
     * 该字段表示消息的类型，用于区分不同的消息类别（例如文本消息、通知等）。
     */
    private String messageType;

    /**
     * 消息内容
     * <p>
     * 该字段表示消息的具体内容，通常是 JSON 格式或文本内容。
     */
    private String messageContent;

}
