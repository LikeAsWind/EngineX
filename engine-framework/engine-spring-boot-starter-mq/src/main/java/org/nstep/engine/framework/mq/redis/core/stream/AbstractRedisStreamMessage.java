package org.nstep.engine.framework.mq.redis.core.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nstep.engine.framework.mq.redis.core.message.AbstractRedisMessage;

/**
 * Redis Stream Message 抽象类
 * <p>
 * 该类是 Redis Stream 消息的抽象基类，所有 Redis Stream 消息类应继承此类。
 */
public abstract class AbstractRedisStreamMessage extends AbstractRedisMessage {

    /**
     * 获得 Redis Stream Key，默认使用类名
     * <p>
     * 该方法返回 Redis Stream 的键（Stream Key），默认为类名。
     *
     * @return Stream Key
     */
    @JsonIgnore // 避免序列化
    public String getStreamKey() {
        return getClass().getSimpleName(); // 默认使用类名作为 Stream Key
    }

}
