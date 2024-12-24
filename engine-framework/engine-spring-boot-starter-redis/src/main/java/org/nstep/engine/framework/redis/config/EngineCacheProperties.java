package org.nstep.engine.framework.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Cache 配置项
 * <p>
 * 该类用于读取和配置缓存相关的属性，尤其是 Redis 缓存的配置项。它通过 Spring Boot 的 `@ConfigurationProperties` 注解与配置文件中的属性进行绑定。
 * 该类主要用于配置 Redis 缓存扫描时一次返回的数量。
 */
@ConfigurationProperties("engine.cache")
@Data
@Validated
public class EngineCacheProperties {

    /**
     * {@link #redisScanBatchSize} 默认值
     * <p>
     * 默认的 Redis 缓存扫描批次大小为 30。
     */
    private static final Integer REDIS_SCAN_BATCH_SIZE_DEFAULT = 30;

    /**
     * redis scan 一次返回数量
     * <p>
     * 配置 Redis 扫描时一次返回的缓存项数量。该值决定了每次从 Redis 中批量获取缓存项的数量。
     * 默认值为 30，可以通过配置文件进行调整。
     */
    private Integer redisScanBatchSize = REDIS_SCAN_BATCH_SIZE_DEFAULT;

}
