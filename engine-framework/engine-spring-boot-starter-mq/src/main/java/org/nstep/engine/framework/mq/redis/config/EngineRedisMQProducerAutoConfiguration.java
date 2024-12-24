package org.nstep.engine.framework.mq.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.mq.redis.core.RedisMQTemplate;
import org.nstep.engine.framework.mq.redis.core.interceptor.RedisMessageInterceptor;
import org.nstep.engine.framework.redis.config.EngineRedisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * Redis 消息队列 Producer 配置类
 * 该配置类用于自动配置 Redis 消息队列的生产者，创建 RedisMQTemplate 实例，并支持消息拦截器。
 */
@Slf4j
@AutoConfiguration(after = EngineRedisAutoConfiguration.class) // 在 EngineRedisAutoConfiguration 配置之后加载
public class EngineRedisMQProducerAutoConfiguration {

    /**
     * 创建 RedisMQTemplate Bean
     * RedisMQTemplate 用于生产消息到 Redis 消息队列。
     *
     * @param redisTemplate Redis 的 StringRedisTemplate 实例，用于操作 Redis。
     * @param interceptors  Redis 消息拦截器列表，用于处理消息发送前后的拦截逻辑。
     * @return 配置好的 RedisMQTemplate 实例
     */
    @Bean
    public RedisMQTemplate redisMQTemplate(StringRedisTemplate redisTemplate,
                                           List<RedisMessageInterceptor> interceptors) {
        // 创建 RedisMQTemplate 实例
        RedisMQTemplate redisMQTemplate = new RedisMQTemplate(redisTemplate);

        // 添加拦截器，拦截器用于对消息进行处理（如修改消息内容、记录日志等）
        interceptors.forEach(redisMQTemplate::addInterceptor);

        // 返回配置好的 RedisMQTemplate 实例
        return redisMQTemplate;
    }

}
