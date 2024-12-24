package org.nstep.engine.framework.mq.redis.core.pubsub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nstep.engine.framework.mq.redis.core.message.AbstractRedisMessage;

/**
 * Redis Channel 消息抽象类
 * <p>
 * 该类继承自 AbstractRedisMessage，作为 Redis 消息的基类，提供了获取 Redis Channel 的功能。
 * 子类可以继承该类，并指定不同的 Channel 来实现特定的消息传递逻辑。
 */
public abstract class AbstractRedisChannelMessage extends AbstractRedisMessage {

    /**
     * 获取 Redis Channel，默认使用类名作为 Channel 名
     * <p>
     * Redis 中的 Channel 是消息发布和订阅的标识。此方法默认使用当前类的简单类名作为 Channel 名。
     * 子类可以根据需要重写此方法，返回不同的 Channel 名。
     *
     * @return Channel 名称
     */
    @JsonIgnore // 避免序列化。原因是，Redis 发布 Channel 消息时，已经会指定 Channel。
    public String getChannel() {
        return getClass().getSimpleName(); // 默认使用类名作为 Channel 名
    }

}
