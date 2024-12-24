package org.nstep.engine.framework.mq.redis.core.stream;

import cn.hutool.core.util.TypeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.framework.mq.redis.core.RedisMQTemplate;
import org.nstep.engine.framework.mq.redis.core.interceptor.RedisMessageInterceptor;
import org.nstep.engine.framework.mq.redis.core.message.AbstractRedisMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Redis Stream 监听器抽象类，用于实现集群消费
 * <p>
 * 该类是 Redis Stream 消息的消费者监听器的抽象基类，所有使用 Redis Stream 进行集群消费的类应继承此类。
 *
 * @param <T> 消息类型。必须指定消息类型，否则会报错。
 */
public abstract class AbstractRedisStreamMessageListener<T extends AbstractRedisStreamMessage>
        implements StreamListener<String, ObjectRecord<String, String>> {

    /**
     * 消息类型
     */
    private final Class<T> messageType;
    /**
     * Redis Stream Key
     */
    @Getter
    private final String streamKey;

    /**
     * Redis 消费者分组，默认使用 spring.application.name 配置的名称
     */
    @Value("${spring.application.name}")
    @Getter
    private String group;

    /**
     * RedisMQTemplate 用于操作 Redis
     */
    @Setter
    private RedisMQTemplate redisMQTemplate;

    /**
     * 构造函数，初始化消息类型和 Stream Key
     */
    @SneakyThrows
    protected AbstractRedisStreamMessageListener() {
        this.messageType = getMessageClass(); // 获取消息类型
        this.streamKey = messageType.getDeclaredConstructor().newInstance().getStreamKey(); // 获取 Stream Key
    }

    /**
     * 处理接收到的消息
     *
     * @param message Redis Stream 消息
     */
    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        // 将消息体解析为指定类型的消息对象
        T messageObj = JsonUtils.parseObject(message.getValue(), messageType);
        try {
            consumeMessageBefore(messageObj); // 消费前处理
            // 消费消息
            this.onMessage(messageObj);
            // ack 消息，表示消息已成功消费
            redisMQTemplate.getRedisTemplate().opsForStream().acknowledge(group, message);
        } finally {
            consumeMessageAfter(messageObj); // 消费后处理
        }
    }

    /**
     * 消费消息的具体实现，由子类实现
     *
     * @param message 消息对象
     */
    public abstract void onMessage(T message);

    /**
     * 通过解析类上的泛型，获得消息类型
     *
     * @return 消息类型
     */
    @SuppressWarnings("unchecked")
    private Class<T> getMessageClass() {
        Type type = TypeUtil.getTypeArgument(getClass(), 0);
        if (type == null) {
            throw new IllegalStateException(String.format("类型(%s) 需要设置消息类型", getClass().getName()));
        }
        return (Class<T>) type;
    }

    /**
     * 消费前的处理，执行所有拦截器的前置操作
     */
    private void consumeMessageBefore(AbstractRedisMessage message) {
        assert redisMQTemplate != null;
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();
        // 正序执行拦截器的前置操作
        interceptors.forEach(interceptor -> interceptor.consumeMessageBefore(message));
    }

    /**
     * 消费后的处理，执行所有拦截器的后置操作
     */
    private void consumeMessageAfter(AbstractRedisMessage message) {
        assert redisMQTemplate != null;
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();
        // 倒序执行拦截器的后置操作
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).consumeMessageAfter(message);
        }
    }

}
