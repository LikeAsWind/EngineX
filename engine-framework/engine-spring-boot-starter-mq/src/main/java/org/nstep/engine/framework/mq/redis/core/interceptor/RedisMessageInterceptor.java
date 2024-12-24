package org.nstep.engine.framework.mq.redis.core.interceptor;

import org.nstep.engine.framework.mq.redis.core.message.AbstractRedisMessage;

/**
 * {@link AbstractRedisMessage} 消息拦截器
 * 通过拦截器，作为插件机制，实现拓展。
 * 例如说，多租户场景下的 MQ 消息处理
 */
public interface RedisMessageInterceptor {

    /**
     * 消息发送前的处理方法
     * 在消息发送到 Redis 之前调用，允许进行一些预处理操作。
     *
     * @param message 发送的消息对象
     */
    default void sendMessageBefore(AbstractRedisMessage message) {
    }

    /**
     * 消息发送后的处理方法
     * 在消息发送到 Redis 之后调用，允许进行一些后处理操作。
     *
     * @param message 发送的消息对象
     */
    default void sendMessageAfter(AbstractRedisMessage message) {
    }

    /**
     * 消息消费前的处理方法
     * 在消息从 Redis 消费之前调用，允许进行一些预处理操作。
     *
     * @param message 消费的消息对象
     */
    default void consumeMessageBefore(AbstractRedisMessage message) {
    }

    /**
     * 消息消费后的处理方法
     * 在消息从 Redis 消费之后调用，允许进行一些后处理操作。
     *
     * @param message 消费的消息对象
     */
    default void consumeMessageAfter(AbstractRedisMessage message) {
    }

}
