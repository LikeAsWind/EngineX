package org.nstep.engine.framework.xxljob.enums;


/**
 * 路由策略枚举
 * <p>
 * 该枚举定义了不同的路由策略，用于指定任务调度时执行器的选择方式。每个策略决定了如何选择执行器来执行任务。
 * <p>
 * 路由策略包括：
 * - FIRST: 选择第一个执行器
 * - LAST: 选择最后一个执行器
 * - ROUND: 轮询策略
 * - RANDOM: 随机选择执行器
 * - CONSISTENT_HASH: 一致性哈希策略
 * - LEAST_FREQUENTLY_USED: 最少使用策略
 * - LEAST_RECENTLY_USED: 最久未使用策略
 * - FAILOVER: 故障转移策略
 * - BUSYOVER: 忙碌转移策略
 * - SHARDING_BROADCAST: 分片广播策略
 * </p>
 * <p>
 * 每个策略都适用于不同的业务场景，具体选择哪种策略取决于任务调度的需求。
 * </p>
 *
 * @author hanabi
 */
public enum ExecutorRouteStrategyEnum {

    /**
     * FIRST: 选择第一个执行器
     */
    FIRST,

    /**
     * LAST: 选择最后一个执行器
     */
    LAST,

    /**
     * ROUND: 轮询策略
     */
    ROUND,

    /**
     * RANDOM: 随机选择执行器
     */
    RANDOM,

    /**
     * CONSISTENT_HASH: 一致性哈希策略
     */
    CONSISTENT_HASH,

    /**
     * LEAST_FREQUENTLY_USED: 最少使用策略
     */
    LEAST_FREQUENTLY_USED,

    /**
     * LEAST_RECENTLY_USED: 最久未使用策略
     */
    LEAST_RECENTLY_USED,

    /**
     * FAILOVER: 故障转移策略
     */
    FAILOVER,

    /**
     * BUSYOVER: 忙碌转移策略
     */
    BUSYOVER,

    /**
     * SHARDING_BROADCAST: 分片广播策略
     */
    SHARDING_BROADCAST;

    // 枚举构造函数，默认无参
    ExecutorRouteStrategyEnum() {
    }
}
