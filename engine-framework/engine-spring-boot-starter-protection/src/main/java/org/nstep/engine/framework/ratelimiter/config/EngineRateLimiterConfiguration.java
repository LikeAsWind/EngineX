package org.nstep.engine.framework.ratelimiter.config;

import org.nstep.engine.framework.ratelimiter.core.aop.RateLimiterAspect;
import org.nstep.engine.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import org.nstep.engine.framework.ratelimiter.core.keyresolver.impl.*;
import org.nstep.engine.framework.ratelimiter.core.redis.RateLimiterRedisDAO;
import org.nstep.engine.framework.redis.config.EngineRedisAutoConfiguration;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * RateLimiter 配置类，自动配置 RateLimiter 相关的 Bean。
 * <p>
 * 该类提供了 RateLimiter 相关的配置，主要包括 RateLimiter 的切面（Aspect）和 Redis DAO 的 Bean 配置。
 * 它还为各种 RateLimiterKeyResolver 提供了默认的实现，以支持不同的限流策略。
 * <p>
 * 该配置类依赖于 Redis 和 RedissonClient 来实现分布式限流。
 */
@AutoConfiguration(after = EngineRedisAutoConfiguration.class)
public class EngineRateLimiterConfiguration {

    /**
     * 创建 RateLimiterAspect Bean，负责切面操作
     * <p>
     * 该 Bean 用于处理限流的切面逻辑。它会根据不同的 RateLimiterKeyResolver 和 RateLimiterRedisDAO 进行限流控制。
     *
     * @param keyResolvers        用于解析限流 Key 的解析器列表
     * @param rateLimiterRedisDAO 用于与 Redis 交互的限流 DAO
     * @return 返回一个新的 RateLimiterAspect 实例
     */
    @Bean
    public RateLimiterAspect rateLimiterAspect(List<RateLimiterKeyResolver> keyResolvers, RateLimiterRedisDAO rateLimiterRedisDAO) {
        return new RateLimiterAspect(keyResolvers, rateLimiterRedisDAO);
    }

    /**
     * 创建 RateLimiterRedisDAO Bean，负责与 Redis 交互进行限流控制
     * <p>
     * 该 Bean 使用 RedissonClient 进行 Redis 操作，提供限流的 Redis 存储和操作支持。
     *
     * @param redissonClient Redisson 客户端实例，用于与 Redis 交互
     * @return 返回一个新的 RateLimiterRedisDAO 实例
     */
    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RateLimiterRedisDAO rateLimiterRedisDAO(RedissonClient redissonClient) {
        return new RateLimiterRedisDAO(redissonClient);
    }

    // ========== 各种 RateLimiterKeyResolver Bean 配置 ==========

    /**
     * 创建 DefaultRateLimiterKeyResolver Bean，使用默认的限流 Key 解析策略
     * <p>
     * 该 Bean 使用默认的策略，通过方法名和参数来生成限流的 Key。
     *
     * @return 返回一个新的 DefaultRateLimiterKeyResolver 实例
     */
    @Bean
    public DefaultRateLimiterKeyResolver defaultRateLimiterKeyResolver() {
        return new DefaultRateLimiterKeyResolver();
    }

    /**
     * 创建 UserRateLimiterKeyResolver Bean，使用用户级别的限流 Key 解析策略
     * <p>
     * 该 Bean 通过用户信息（如 userId）来生成限流的 Key，适用于需要根据用户进行限流的场景。
     *
     * @return 返回一个新的 UserRateLimiterKeyResolver 实例
     */
    @Bean
    public UserRateLimiterKeyResolver userRateLimiterKeyResolver() {
        return new UserRateLimiterKeyResolver();
    }

    /**
     * 创建 ClientIpRateLimiterKeyResolver Bean，使用客户端 IP 地址作为限流 Key
     * <p>
     * 该 Bean 通过客户端的 IP 地址来生成限流的 Key，适用于基于客户端 IP 进行限流的场景。
     *
     * @return 返回一个新的 ClientIpRateLimiterKeyResolver 实例
     */
    @Bean
    public ClientIpRateLimiterKeyResolver clientIpRateLimiterKeyResolver() {
        return new ClientIpRateLimiterKeyResolver();
    }

    /**
     * 创建 ServerNodeRateLimiterKeyResolver Bean，使用服务器节点作为限流 Key
     * <p>
     * 该 Bean 通过服务器节点的标识来生成限流的 Key，适用于分布式环境下根据节点进行限流的场景。
     *
     * @return 返回一个新的 ServerNodeRateLimiterKeyResolver 实例
     */
    @Bean
    public ServerNodeRateLimiterKeyResolver serverNodeRateLimiterKeyResolver() {
        return new ServerNodeRateLimiterKeyResolver();
    }

    /**
     * 创建 ExpressionRateLimiterKeyResolver Bean，使用 Spring EL 表达式作为限流 Key
     * <p>
     * 该 Bean 通过解析 Spring EL 表达式来动态生成限流的 Key，适用于需要灵活配置的场景。
     *
     * @return 返回一个新的 ExpressionRateLimiterKeyResolver 实例
     */
    @Bean
    public ExpressionRateLimiterKeyResolver expressionRateLimiterKeyResolver() {
        return new ExpressionRateLimiterKeyResolver();
    }

}
