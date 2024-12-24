package org.nstep.engine.framework.websocket.core.sender.rabbitmq;

import lombok.Data;

import java.io.Serializable;

/**
 * RabbitMQ 广播 WebSocket 的消息
 * <p>
 * 用于通过 RabbitMQ 广播 WebSocket 消息。
 */
@Data
public class RabbitMQWebSocketMessage implements Serializable {

    /**
     * Session 编号
     * <p>
     * 用于标识 WebSocket 会话的唯一 ID。
     */
    private String sessionId;

    /**
     * 用户类型
     * <p>
     * 用于标识用户类型（例如管理员、普通用户等）。
     */
    private Integer userType;

    /**
     * 用户编号
     * <p>
     * 用于标识用户的唯一 ID。
     */
    private Long userId;

    /**
     * 消息类型
     * <p>
     * 用于标识消息的类型，例如通知、警告等。
     */
    private String messageType;

    /**
     * 消息内容
     * <p>
     * 消息的实际内容，通常为字符串或 JSON 格式的数据。
     */
    private String messageContent;

}
