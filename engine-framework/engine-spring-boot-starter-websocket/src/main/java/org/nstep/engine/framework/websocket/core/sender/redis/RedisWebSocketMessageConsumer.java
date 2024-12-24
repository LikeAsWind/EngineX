package org.nstep.engine.framework.websocket.core.sender.redis;

import lombok.RequiredArgsConstructor;
import org.nstep.engine.framework.mq.redis.core.pubsub.AbstractRedisChannelMessageListener;

/**
 * {@link RedisWebSocketMessage} 广播消息的消费者，真正把消息发送出去
 * <p>
 * 该类是 Redis 广播消息的消费者，负责接收通过 Redis 广播的 {@link RedisWebSocketMessage} 消息。
 * 它继承自 {@link AbstractRedisChannelMessageListener}，并通过调用 {@link RedisWebSocketMessageSender} 将接收到的消息发送到 WebSocket 客户端。
 */
@RequiredArgsConstructor
public class RedisWebSocketMessageConsumer extends AbstractRedisChannelMessageListener<RedisWebSocketMessage> {

    /**
     * RedisWebSocketMessageSender 用于将消息发送到 WebSocket 客户端
     */
    private final RedisWebSocketMessageSender redisWebSocketMessageSender;

    /**
     * 处理接收到的 Redis 消息
     * <p>
     * 当 Redis 广播消息到达时，调用此方法将消息发送到 WebSocket 客户端。
     *
     * @param message 接收到的 RedisWebSocketMessage 消息
     */
    @Override
    public void onMessage(RedisWebSocketMessage message) {
        // 将消息发送到 WebSocket 客户端
        redisWebSocketMessageSender.send(message.getSessionId(),
                message.getUserType(), message.getUserId(),
                message.getMessageType(), message.getMessageContent());
    }

}
