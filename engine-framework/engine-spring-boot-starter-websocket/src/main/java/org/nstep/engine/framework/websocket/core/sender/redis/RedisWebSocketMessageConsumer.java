package org.nstep.engine.framework.websocket.core.sender.redis;

import lombok.RequiredArgsConstructor;
import org.nstep.engine.framework.mq.redis.core.pubsub.AbstractRedisChannelMessageListener;

/**
 * {@link RedisWebSocketMessage} 广播消息的消费者，真正把消息发送出去
 */
@RequiredArgsConstructor
public class RedisWebSocketMessageConsumer extends AbstractRedisChannelMessageListener<RedisWebSocketMessage> {

    private final RedisWebSocketMessageSender redisWebSocketMessageSender;

    @Override
    public void onMessage(RedisWebSocketMessage message) {
        redisWebSocketMessageSender.send(message.getSessionId(),
                message.getUserType(), message.getUserId(),
                message.getMessageType(), message.getMessageContent());
    }

}
