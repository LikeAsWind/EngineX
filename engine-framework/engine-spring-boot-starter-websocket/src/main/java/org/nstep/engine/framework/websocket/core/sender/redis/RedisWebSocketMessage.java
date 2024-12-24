package org.nstep.engine.framework.websocket.core.sender.redis;

import lombok.Data;
import org.nstep.engine.framework.mq.redis.core.pubsub.AbstractRedisChannelMessage;

/**
 * Redis 广播 WebSocket 的消息
 * <p>
 * 该类用于封装 WebSocket 消息，并通过 Redis 广播到各个 WebSocket 客户端。
 * 它继承自 {@link AbstractRedisChannelMessage}，并包含了与 WebSocket 消息相关的属性，如会话 ID、用户信息、消息类型和内容等。
 */
@Data
public class RedisWebSocketMessage extends AbstractRedisChannelMessage {

    /**
     * 会话编号
     * <p>
     * 唯一标识一个 WebSocket 会话的 ID，用于识别客户端连接。
     */
    private String sessionId;

    /**
     * 用户类型
     * <p>
     * 表示消息发送者的用户类型（例如，管理员、普通用户等）。
     */
    private Integer userType;

    /**
     * 用户编号
     * <p>
     * 唯一标识一个用户的 ID，用于标识消息的接收者或发送者。
     */
    private Long userId;

    /**
     * 消息类型
     * <p>
     * 表示消息的类型，用于区分不同种类的消息（例如，通知、警告、聊天消息等）。
     */
    private String messageType;

    /**
     * 消息内容
     * <p>
     * 存储消息的具体内容，通常为 JSON 格式或文本内容，包含具体的业务信息。
     */
    private String messageContent;

}
