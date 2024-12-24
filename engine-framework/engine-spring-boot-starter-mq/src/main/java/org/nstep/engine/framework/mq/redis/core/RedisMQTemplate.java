package org.nstep.engine.framework.mq.redis.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.framework.mq.redis.core.interceptor.RedisMessageInterceptor;
import org.nstep.engine.framework.mq.redis.core.message.AbstractRedisMessage;
import org.nstep.engine.framework.mq.redis.core.pubsub.AbstractRedisChannelMessage;
import org.nstep.engine.framework.mq.redis.core.stream.AbstractRedisStreamMessage;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis MQ 操作模板类
 * <p>
 * 该类封装了 Redis 消息队列的操作，提供了发送消息的方法，并支持通过拦截器机制进行消息的处理。
 */
@Getter
@AllArgsConstructor
public class RedisMQTemplate {

    /**
     * RedisTemplate 实例，用于执行 Redis 操作
     */
    private final RedisTemplate<String, ?> redisTemplate;

    /**
     * 拦截器列表，存储所有的消息拦截器
     */
    private final List<RedisMessageInterceptor> interceptors = new ArrayList<>();

    /**
     * 发送 Redis 消息，基于 Redis pub/sub 实现
     *
     * @param message 消息对象
     * @param <T>     消息类型，必须是 AbstractRedisChannelMessage 的子类
     */
    public <T extends AbstractRedisChannelMessage> void send(T message) {
        try {
            // 在发送消息之前执行拦截器的 sendMessageBefore 方法
            sendMessageBefore(message);
            // 发送消息到 Redis Channel
            redisTemplate.convertAndSend(message.getChannel(), JsonUtils.toJsonString(message));
        } finally {
            // 在发送消息之后执行拦截器的 sendMessageAfter 方法
            sendMessageAfter(message);
        }
    }

    /**
     * 发送 Redis 消息，基于 Redis Stream 实现
     *
     * @param message 消息对象
     * @param <T>     消息类型，必须是 AbstractRedisStreamMessage 的子类
     * @return 返回消息记录的编号对象
     */
    public <T extends AbstractRedisStreamMessage> RecordId send(T message) {
        try {
            // 在发送消息之前执行拦截器的 sendMessageBefore 方法
            sendMessageBefore(message);
            // 发送消息到 Redis Stream
            return redisTemplate.opsForStream().add(StreamRecords.newRecord()
                    .ofObject(JsonUtils.toJsonString(message)) // 设置消息内容
                    .withStreamKey(message.getStreamKey())); // 设置消息的 stream key
        } finally {
            // 在发送消息之后执行拦截器的 sendMessageAfter 方法
            sendMessageAfter(message);
        }
    }

    /**
     * 添加消息拦截器
     *
     * @param interceptor 消息拦截器
     */
    public void addInterceptor(RedisMessageInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * 在发送消息之前执行所有拦截器的 sendMessageBefore 方法
     */
    private void sendMessageBefore(AbstractRedisMessage message) {
        // 正序执行拦截器的 sendMessageBefore 方法
        interceptors.forEach(interceptor -> interceptor.sendMessageBefore(message));
    }

    /**
     * 在发送消息之后执行所有拦截器的 sendMessageAfter 方法
     */
    private void sendMessageAfter(AbstractRedisMessage message) {
        // 倒序执行拦截器的 sendMessageAfter 方法
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).sendMessageAfter(message);
        }
    }

}
