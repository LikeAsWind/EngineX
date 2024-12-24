package org.nstep.engine.framework.idempotent.core.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 幂等操作的 Redis 数据访问对象（DAO）
 * <p>
 * 该类提供了与 Redis 进行交互的操作，用于实现幂等性控制。主要操作包括设置幂等性 Key 和删除幂等性 Key。
 * </p>
 */
@AllArgsConstructor
public class IdempotentRedisDAO {

    /**
     * 幂等性操作的 Redis Key 格式
     * <p>
     * 幂等性 Key 格式化为 "idempotent:{key}"，其中 {key} 为实际的幂等性标识符（如请求的 UUID）。
     * </p>
     */
    private static final String IDEMPOTENT = "idempotent:%s";

    // Redis 操作模板，用于与 Redis 进行交互
    private final StringRedisTemplate redisTemplate;

    /**
     * 格式化 Redis Key
     * <p>
     * 根据给定的 Key 格式化 Redis Key。格式为 "idempotent:{key}"，其中 {key} 为实际的幂等性标识符。
     * </p>
     *
     * @param key 幂等性标识符
     * @return 格式化后的 Redis Key
     */
    private static String formatKey(String key) {
        return String.format(IDEMPOTENT, key);
    }

    /**
     * 设置幂等性 Key，防止重复请求
     * <p>
     * 使用 Redis 的 setIfAbsent 操作设置一个幂等性 Key。如果 Key 不存在，则设置该 Key 并设置过期时间。
     * 如果 Key 已经存在，表示请求是重复的，返回 false。
     * </p>
     *
     * @param key      幂等性标识符
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return 如果设置成功返回 true，否则返回 false（表示请求重复）
     */
    public Boolean setIfAbsent(String key, long timeout, TimeUnit timeUnit) {
        // 格式化 Redis Key
        String redisKey = formatKey(key);
        // 设置 Key，若不存在则设置，并设置过期时间
        return redisTemplate.opsForValue().setIfAbsent(redisKey, "", timeout, timeUnit);
    }

    /**
     * 删除幂等性 Key
     * <p>
     * 删除指定的幂等性 Key，通常在请求处理完毕后或发生异常时调用。
     * </p>
     *
     * @param key 幂等性标识符
     */
    public void delete(String key) {
        // 格式化 Redis Key
        String redisKey = formatKey(key);
        // 删除 Redis 中的 Key
        redisTemplate.delete(redisKey);
    }

}
