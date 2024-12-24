package org.nstep.engine.framework.mq.redis.core.pubsub;

import cn.hutool.core.util.TypeUtil;
import lombok.Setter;
import lombok.SneakyThrows;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.framework.mq.redis.core.RedisMQTemplate;
import org.nstep.engine.framework.mq.redis.core.interceptor.RedisMessageInterceptor;
import org.nstep.engine.framework.mq.redis.core.message.AbstractRedisMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Redis Pub/Sub 监听器抽象类，用于实现广播消费
 * <p>
 * 该类实现了 Redis 消息的广播消费功能。通过继承此类，用户可以自定义处理不同类型的 Redis 消息。
 *
 * @param <T> 消息类型。一定要填写噢，不然会报错
 */
public abstract class AbstractRedisChannelMessageListener<T extends AbstractRedisChannelMessage> implements MessageListener {

    /**
     * 消息类型
     */
    private final Class<T> messageType;

    /**
     * Redis Channel
     */
    private final String channel;

    /**
     * RedisMQTemplate
     */
    @Setter
    private RedisMQTemplate redisMQTemplate;

    /**
     * 构造方法，初始化消息类型和 Redis Channel
     */
    @SneakyThrows
    protected AbstractRedisChannelMessageListener() {
        this.messageType = getMessageClass(); // 获取消息类型
        this.channel = messageType.getDeclaredConstructor().newInstance().getChannel(); // 获取 Redis Channel
    }

    /**
     * 获得 Sub 订阅的 Redis Channel 通道
     * <p>
     * 返回该监听器订阅的 Redis Channel 名。
     *
     * @return channel
     */
    public final String getChannel() {
        return channel;
    }

    /**
     * 消息接收处理方法
     * <p>
     * 该方法是 Redis 消息监听的回调方法，接收到消息后会调用该方法。
     *
     * @param message Redis 消息
     * @param bytes   消息字节数组
     */
    @Override
    public final void onMessage(Message message, byte[] bytes) {
        // 将接收到的消息体转换为指定的消息类型
        T messageObj = JsonUtils.parseObject(message.getBody(), messageType);
        try {
            consumeMessageBefore(messageObj); // 消费前处理
            // 消费消息
            this.onMessage(messageObj);
        } finally {
            consumeMessageAfter(messageObj); // 消费后处理
        }
    }

    /**
     * 处理消息
     * <p>
     * 这是一个抽象方法，子类需要实现该方法来处理接收到的消息。
     *
     * @param message 消息
     */
    public abstract void onMessage(T message);

    /**
     * 通过解析类上的泛型，获得消息类型
     * <p>
     * 该方法通过反射获取当前类的泛型类型，即消息类型。
     *
     * @return 消息类型
     */
    @SuppressWarnings("unchecked")
    private Class<T> getMessageClass() {
        Type type = TypeUtil.getTypeArgument(getClass(), 0); // 获取类的泛型类型
        if (type == null) {
            throw new IllegalStateException(String.format("类型(%s) 需要设置消息类型", getClass().getName()));
        }
        return (Class<T>) type;
    }

    /**
     * 消费前处理
     * <p>
     * 在处理消息之前，会遍历所有的拦截器，执行 `consumeMessageBefore` 方法。
     *
     * @param message 消息
     */
    private void consumeMessageBefore(AbstractRedisMessage message) {
        assert redisMQTemplate != null;
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();
        // 正序执行拦截器的 `consumeMessageBefore` 方法
        interceptors.forEach(interceptor -> interceptor.consumeMessageBefore(message));
    }

    /**
     * 消费后处理
     * <p>
     * 在处理完消息之后，会遍历所有的拦截器，执行 `consumeMessageAfter` 方法。
     * <p>
     * 这里是倒序执行拦截器的 `consumeMessageAfter` 方法。
     *
     * @param message 消息
     */
    private void consumeMessageAfter(AbstractRedisMessage message) {
        assert redisMQTemplate != null;
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();
        // 倒序执行拦截器的 `consumeMessageAfter` 方法
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).consumeMessageAfter(message);
        }
    }
}
