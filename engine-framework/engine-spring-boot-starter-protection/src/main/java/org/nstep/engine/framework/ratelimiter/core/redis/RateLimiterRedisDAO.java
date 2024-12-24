package org.nstep.engine.framework.ratelimiter.core.redis;

import lombok.AllArgsConstructor;
import org.redisson.api.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 限流 Redis DAO
 * <p>
 * 该类负责与 Redis 进行交互，提供限流功能。使用 Redisson 提供的 RRateLimiter 来实现分布式限流。
 * 通过 Redis 存储和获取限流数据，确保分布式环境下的限流操作。
 */
@AllArgsConstructor
public class RateLimiterRedisDAO {

    /**
     * 限流操作的 Redis Key 格式
     * <p>
     * KEY 格式：rate_limiter:%s，其中 %s 由具体的限流 Key 替换（如 uuid）。
     * VALUE 格式：String
     * 过期时间：不固定
     */
    private static final String RATE_LIMITER = "rate_limiter:%s";

    private final RedissonClient redissonClient;

    /**
     * 格式化 Redis Key
     * <p>
     * 通过给定的 key，返回符合 Redis 限流格式的完整 Key 字符串。
     *
     * @param key 限流 Key
     * @return 格式化后的 Redis Key
     */
    private static String formatKey(String key) {
        return String.format(RATE_LIMITER, key);
    }

    /**
     * 尝试获取一个令牌
     * <p>
     * 尝试从限流器中获取一个令牌，若成功返回 true，否则返回 false。
     * 该方法会根据传入的速率、时间和时间单位配置限流器，并尝试获取令牌。
     *
     * @param key      限流的 Key
     * @param count    每单位时间的请求数
     * @param time     限流的时间长度
     * @param timeUnit 时间单位
     * @return 是否成功获取到令牌
     */
    public Boolean tryAcquire(String key, int count, int time, TimeUnit timeUnit) {
        // 1. 获得 RRateLimiter，并设置 rate 速率
        RRateLimiter rateLimiter = getRRateLimiter(key, count, time, timeUnit);
        // 2. 尝试获取 1 个令牌
        return rateLimiter.tryAcquire();
    }

    /**
     * 获取或创建一个限流器
     * <p>
     * 根据给定的 key、速率、时间和时间单位，获取或创建一个 RRateLimiter。
     * 如果限流器不存在，则创建并设置限流速率；如果已存在且配置一致，则直接返回。
     *
     * @param key      限流的 Key
     * @param count    每单位时间的请求数
     * @param time     限流的时间长度
     * @param timeUnit 时间单位
     * @return 限流器对象
     */
    private RRateLimiter getRRateLimiter(String key, long count, int time, TimeUnit timeUnit) {
        String redisKey = formatKey(key);
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(redisKey);
        long rateInterval = timeUnit.toSeconds(time);

        // 1. 如果不存在，设置 rate 速率
        RateLimiterConfig config = rateLimiter.getConfig();
        if (config == null) {
            rateLimiter.trySetRate(RateType.OVERALL, count, rateInterval, RateIntervalUnit.SECONDS);
            return rateLimiter;
        }

        // 2. 如果存在，并且配置相同，则直接返回
        if (config.getRateType() == RateType.OVERALL
                && Objects.equals(config.getRate(), count)
                && Objects.equals(config.getRateInterval(), TimeUnit.SECONDS.toMillis(rateInterval))) {
            return rateLimiter;
        }

        // 3. 如果存在，并且配置不同，则进行新建
        rateLimiter.setRate(RateType.OVERALL, count, rateInterval, RateIntervalUnit.SECONDS);
        return rateLimiter;
    }

}
