package org.nstep.engine.framework.lock4j.core;

import com.baomidou.lock.DefaultLockKeyBuilder;

/**
 * Lock4j Redis Key 枚举类，用于定义 Lock4j 分布式锁在 Redis 中使用的 Key 格式。
 * <p>
 * 该接口包含了 Lock4j 分布式锁的 Redis 键值对的格式规范。它为 Redis 中的锁提供了统一的命名规则，
 * 以便于在不同的服务或模块中使用相同的锁键，确保锁的唯一性和一致性。
 * <p>
 * 锁的键遵循一定的格式，并且可以与 Redis 中的数据结构进行交互。
 */
public interface Lock4jRedisKeyConstants {

    /**
     * 分布式锁的 Redis 键格式
     * <p>
     * 键的格式为：lock4j:%s，其中 %s 会被具体的锁名称或标识符替换。该键用于标识分布式锁。
     * 锁的值使用 Redis 的 Hash 数据结构（如 Redisson 的 RLock），并且具有不固定的过期时间。
     * <p>
     * 示例：
     * 锁的键：lock4j:order:12345`，其中 `order:12345` 代表具体的锁标识符。
     * 用于构建具体的锁键
     *
     * @see DefaultLockKeyBuilder
     */
    String LOCK4J = "lock4j:%s";

}
