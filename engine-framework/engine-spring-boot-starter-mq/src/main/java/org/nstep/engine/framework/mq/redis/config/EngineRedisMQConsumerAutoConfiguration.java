package org.nstep.engine.framework.mq.redis.config;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.enums.DocumentEnum;
import org.nstep.engine.framework.mq.redis.core.RedisMQTemplate;
import org.nstep.engine.framework.mq.redis.core.job.RedisPendingMessageResendJob;
import org.nstep.engine.framework.mq.redis.core.pubsub.AbstractRedisChannelMessageListener;
import org.nstep.engine.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import org.nstep.engine.framework.redis.config.EngineRedisAutoConfiguration;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.Properties;

/**
 * Redis 消息队列 Consumer 配置类
 * 该配置类用于自动配置 Redis 消息队列消费者，支持 Redis 的 Pub/Sub 和 Stream 模式的消息消费。
 */
@Slf4j
@EnableScheduling // 启用定时任务，用于 RedisPendingMessageResendJob 重发消息
@AutoConfiguration(after = EngineRedisAutoConfiguration.class) // 在 EngineRedisAutoConfiguration 配置之后加载
public class EngineRedisMQConsumerAutoConfiguration {

    /**
     * 构建消费者名字，使用本地 IP + 进程编号的方式。
     * 参考自 RocketMQ clientId 的实现
     *
     * @return 消费者名字
     */
    private static String buildConsumerName() {
        // 获取本机 IP 和进程 ID，构建消费者名字
        return String.format("%s@%d", SystemUtil.getHostInfo().getAddress(), SystemUtil.getCurrentPID());
    }

    /**
     * 校验 Redis 版本号，是否满足最低的版本号要求！
     * 该方法确保 Redis 版本不低于 5.0.0
     */
    private static void checkRedisVersion(RedisTemplate<String, ?> redisTemplate) {
        // 获取 Redis 版本信息
        Properties info = redisTemplate.execute((RedisCallback<Properties>) RedisServerCommands::info);
        String version = MapUtil.getStr(info, "redis_version");
        // 校验 Redis 版本号
        int majorVersion = Integer.parseInt(StrUtil.subBefore(version, '.', false));
        if (majorVersion < 5) {
            throw new IllegalStateException(StrUtil.format("您当前的 Redis 版本为 {}，小于最低要求的 5.0.0 版本！" +
                    "请参考 {} 文档进行安装。", version, DocumentEnum.REDIS_INSTALL.getUrl()));
        }
    }

    /**
     * 创建 Redis Pub/Sub 广播消费的容器
     * 该方法创建并配置 Redis 的消息监听容器，用于处理 Pub/Sub 模式的消息消费。
     */
    @Bean
    @ConditionalOnBean(AbstractRedisChannelMessageListener.class)
    // 只有 AbstractRedisChannelMessageListener 存在时才会注册 Redis pubsub 监听
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisMQTemplate redisMQTemplate, List<AbstractRedisChannelMessageListener<?>> listeners) {
        // 创建 RedisMessageListenerContainer 对象
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // 设置 Redis 连接工厂
        container.setConnectionFactory(redisMQTemplate.getRedisTemplate().getRequiredConnectionFactory());
        // 注册所有监听器
        listeners.forEach(listener -> {
            listener.setRedisMQTemplate(redisMQTemplate);
            container.addMessageListener(listener, new ChannelTopic(listener.getChannel()));
            log.info("[redisMessageListenerContainer][注册 Channel({}) 对应的监听器({})]",
                    listener.getChannel(), listener.getClass().getName());
        });
        return container;
    }

    /**
     * 创建 Redis Stream 重新消费的任务
     * 该方法创建一个定时任务，用于重新消费 Redis Stream 中的未成功消费的消息。
     */
    @Bean
    @ConditionalOnBean(AbstractRedisStreamMessageListener.class)
    // 只有 AbstractRedisStreamMessageListener 存在时才会注册 Redis Stream 监听
    public RedisPendingMessageResendJob redisPendingMessageResendJob(List<AbstractRedisStreamMessageListener<?>> listeners,
                                                                     RedisMQTemplate redisTemplate,
                                                                     @Value("${spring.application.name}") String groupName,
                                                                     RedissonClient redissonClient) {
        // 创建并返回 RedisPendingMessageResendJob 实例
        return new RedisPendingMessageResendJob(listeners, redisTemplate, groupName, redissonClient);
    }

    /**
     * 创建 Redis Stream 集群消费的容器
     * 该方法创建并配置 Redis Stream 的消息消费容器，支持群组消费。
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnBean(AbstractRedisStreamMessageListener.class)
    // 只有 AbstractRedisStreamMessageListener 存在时才会注册 Redis Stream 监听
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> redisStreamMessageListenerContainer(
            RedisMQTemplate redisMQTemplate, List<AbstractRedisStreamMessageListener<?>> listeners) {
        RedisTemplate<String, ?> redisTemplate = redisMQTemplate.getRedisTemplate();
        checkRedisVersion(redisTemplate); // 校验 Redis 版本

        // 创建 StreamMessageListenerContainer 配置
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> containerOptions =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .batchSize(10) // 每次最多拉取 10 条消息
                        .targetType(String.class) // 目标类型为 String
                        .build();
        // 创建 StreamMessageListenerContainer
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container =
                StreamMessageListenerContainer.create(redisMQTemplate.getRedisTemplate().getRequiredConnectionFactory(), containerOptions);

        // 注册监听器，消费对应的 Redis Stream 主题
        String consumerName = buildConsumerName();
        listeners.parallelStream().forEach(listener -> {
            log.info("[redisStreamMessageListenerContainer][开始注册 StreamKey({}) 对应的监听器({})]",
                    listener.getStreamKey(), listener.getClass().getName());
            try {
                redisTemplate.opsForStream().createGroup(listener.getStreamKey(), listener.getGroup());
            } catch (Exception ignore) {
            }
            listener.setRedisMQTemplate(redisMQTemplate);
            Consumer consumer = Consumer.from(listener.getGroup(), consumerName);
            StreamOffset<String> streamOffset = StreamOffset.create(listener.getStreamKey(), ReadOffset.lastConsumed());
            StreamMessageListenerContainer.StreamReadRequestBuilder<String> builder = StreamMessageListenerContainer.StreamReadRequest
                    .builder(streamOffset).consumer(consumer)
                    .autoAcknowledge(false) // 不自动确认
                    .cancelOnError(throwable -> false); // 错误时不取消消费
            container.register(builder.build(), listener);
            log.info("[redisStreamMessageListenerContainer][完成注册 StreamKey({}) 对应的监听器({})]",
                    listener.getStreamKey(), listener.getClass().getName());
        });
        return container;
    }

}
