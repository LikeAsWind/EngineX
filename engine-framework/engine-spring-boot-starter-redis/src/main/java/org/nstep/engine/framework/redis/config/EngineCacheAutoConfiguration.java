package org.nstep.engine.framework.redis.config;

import cn.hutool.core.util.StrUtil;
import org.nstep.engine.framework.redis.core.TimeoutRedisCacheManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static org.nstep.engine.framework.redis.config.EngineRedisAutoConfiguration.buildRedisSerializer;

/**
 * Cache 配置类，基于 Redis 实现
 * <p>
 * 该类配置了基于 Redis 的缓存管理，使用 Spring Boot 的自动配置功能来设置 Redis 缓存的相关参数。
 * 它包括缓存配置的自定义设置，如缓存前缀、序列化方式、TTL（过期时间）等。
 */
@AutoConfiguration
@EnableConfigurationProperties({CacheProperties.class, EngineCacheProperties.class})
@EnableCaching
public class EngineCacheAutoConfiguration {

    /**
     * RedisCacheConfiguration Bean
     * <p>
     * 配置 Redis 缓存的相关属性，包括缓存前缀、序列化方式、过期时间等。
     * 参考 org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration 的 createConfiguration 方法。
     *
     * @param cacheProperties 缓存相关的配置属性
     * @return 配置好的 RedisCacheConfiguration
     */
    @Bean
    @Primary
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        // 获取默认的缓存配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        // 设置缓存键的前缀
        config = config.computePrefixWith(cacheName -> {
            String keyPrefix = cacheProperties.getRedis().getKeyPrefix();
            if (StringUtils.hasText(keyPrefix)) {
                keyPrefix = keyPrefix.lastIndexOf(StrUtil.COLON) == -1 ? keyPrefix + StrUtil.COLON : keyPrefix;
                return keyPrefix + cacheName + StrUtil.COLON;
            }
            return cacheName + StrUtil.COLON;
        });

        // 设置缓存值的序列化方式为 JSON
        config = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(buildRedisSerializer()));

        // 设置缓存的过期时间（TTL）
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }

        // 禁用缓存 null 值
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }

        // 禁用缓存键的前缀
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        return config;
    }

    /**
     * RedisCacheManager Bean
     * <p>
     * 配置 Redis 缓存管理器，负责与 Redis 交互进行缓存操作。
     *
     * @param redisTemplate           Redis 模板，用于与 Redis 进行交互
     * @param redisCacheConfiguration Redis 缓存配置
     * @param engineCacheProperties   引擎缓存相关配置
     * @return 配置好的 RedisCacheManager
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate,
                                               RedisCacheConfiguration redisCacheConfiguration,
                                               EngineCacheProperties engineCacheProperties) {
        // 创建 RedisCacheWriter 对象，用于执行缓存的读写操作
        RedisConnectionFactory connectionFactory = Objects.requireNonNull(redisTemplate.getConnectionFactory());
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory,
                BatchStrategies.scan(engineCacheProperties.getRedisScanBatchSize()));

        // 创建 TimeoutRedisCacheManager 对象，设置缓存过期时间
        return new TimeoutRedisCacheManager(cacheWriter, redisCacheConfiguration);
    }

}
