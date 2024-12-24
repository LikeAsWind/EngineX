package org.nstep.engine.framework.redis.core;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;

/**
 * 支持自定义过期时间的 {@link RedisCacheManager} 实现类
 * <p>
 * 该类扩展了 `RedisCacheManager`，支持在缓存名称中通过格式 "key#ttl" 来指定自定义的过期时间。
 * 其中，`#` 后面的部分表示缓存的过期时间，单位可以是：
 * - `d`：天
 * - `h`：小时
 * - `m`：分钟
 * - `s`：秒
 * 如果没有指定单位，则默认单位为秒。
 * </p>
 */
public class TimeoutRedisCacheManager extends RedisCacheManager {

    private static final String SPLIT = "#"; // 用于分隔缓存名称和过期时间的符号

    /**
     * 构造方法
     *
     * @param cacheWriter               RedisCacheWriter 对象，用于写入 Redis 缓存
     * @param defaultCacheConfiguration 默认的 Redis 缓存配置
     */
    public TimeoutRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    /**
     * 创建 RedisCache 对象，支持自定义过期时间
     * <p>
     * 如果缓存名称中包含 `#` 符号并且分隔后的部分是有效的过期时间（如 "key#ttl"），
     * 则会根据该过期时间创建缓存对象。如果没有指定过期时间，则使用默认的缓存配置。
     * </p>
     *
     * @param name        缓存名称
     * @param cacheConfig 缓存配置
     * @return 创建的 RedisCache 对象
     */
    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        if (StrUtil.isEmpty(name)) {
            return super.createRedisCache(name, cacheConfig); // 如果名称为空，使用默认配置
        }

        // 如果名称中没有 # 或者 # 后面的部分不是有效的过期时间，则使用默认配置
        String[] names = StrUtil.splitToArray(name, SPLIT);
        if (names.length != 2) {
            return super.createRedisCache(name, cacheConfig);
        }

        // 核心：通过修改 cacheConfig 的过期时间，实现自定义过期时间
        if (cacheConfig != null) {
            // 获取 # 后面的部分并解析为过期时间
            String ttlStr = StrUtil.subBefore(names[1], StrUtil.COLON, false); // 获取 ttl 字符串
            names[1] = StrUtil.subAfter(names[1], ttlStr, false); // 移除 ttl 部分
            // 解析过期时间
            Duration duration = parseDuration(ttlStr);
            cacheConfig = cacheConfig.entryTtl(duration); // 设置自定义的过期时间
        }

        // 创建 RedisCache 对象，忽略 ttl 部分
        return super.createRedisCache(names[0] + names[1], cacheConfig);
    }

    /**
     * 解析过期时间字符串为 Duration 对象
     * <p>
     * 该方法根据 ttlStr 的单位（如 "d", "h", "m", "s"）解析过期时间，并返回对应的 Duration 对象。
     * </p>
     *
     * @param ttlStr 过期时间字符串
     * @return 过期时间的 Duration 对象
     */
    private Duration parseDuration(String ttlStr) {
        String timeUnit = StrUtil.subSuf(ttlStr, -1); // 获取时间单位（最后一个字符）
        return switch (timeUnit) {
            case "d" -> // 天
                    Duration.ofDays(removeDurationSuffix(ttlStr));
            case "h" -> // 小时
                    Duration.ofHours(removeDurationSuffix(ttlStr));
            case "m" -> // 分钟
                    Duration.ofMinutes(removeDurationSuffix(ttlStr));
            case "s" -> // 秒
                    Duration.ofSeconds(removeDurationSuffix(ttlStr));
            default -> // 默认单位为秒
                    Duration.ofSeconds(Long.parseLong(ttlStr));
        };
    }

    /**
     * 移除过期时间字符串中的单位后缀，返回纯数字部分
     * <p>
     * 该方法用于从过期时间字符串中去除单位（如 "d", "h", "m", "s"），并返回纯数字部分。
     * </p>
     *
     * @param ttlStr 过期时间字符串
     * @return 时间的数字部分
     */
    private Long removeDurationSuffix(String ttlStr) {
        return NumberUtil.parseLong(StrUtil.sub(ttlStr, 0, ttlStr.length() - 1)); // 移除单位后返回数字部分
    }

}
