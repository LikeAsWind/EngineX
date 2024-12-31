package org.nstep.engine.module.message.constant;

import static org.nstep.engine.module.message.constant.MessageDataConstants.APPLICATION_NAME;

/**
 * Redisson 常量类
 * <p>
 * 该类定义了一些常量，这些常量主要用于在 Redisson 中操作分布式锁，
 * 用于保证在分布式环境下对资源的访问是线程安全的。常用于控制任务的并发性，
 * 如对发送任务、用户总发送人数以及模板总发送人数等的分布式锁控制。
 */
public class RedissonConstants {

    /**
     * 分布式锁：用于锁定同一发送任务
     * <p>
     * 锁的粒度为同一发送任务。这个锁使用发送任务的 `sendContentId` 来确保
     * 同一时刻只有一个线程能够处理特定的发送任务。锁的名字格式为：
     * "engineX:msgLock:sendContentId"
     * <p>
     * 锁的命名约定是基于应用名和发送任务 ID 来动态生成唯一的锁标识符。
     */
    public static final String SEND_CONTENT_LOCK = APPLICATION_NAME + "msgLock:";

    /**
     * 获取锁的最大等待时间
     * <p>
     * 当一个线程尝试获取锁时，如果锁被其他线程持有，它将会等待最多
     * `TRY_LOCK_WAIT_TIME` 秒。如果在此时间内无法获得锁，则会放弃请求。
     * <p>
     * 该常量设置了等待锁的最大时间，单位为秒。
     */
    public static final Long TRY_LOCK_WAIT_TIME = 10L;

    /**
     * 锁的最大持有时间
     * <p>
     * 该常量定义了获取锁后，锁被保持的最大时间，超过这个时间后，
     * 锁会被自动释放，防止死锁情况发生。
     * <p>
     * 该常量设置了锁的最大持有时间，单位为秒。
     */
    public static final Long LOCK_TIME = 10L;

    /**
     * 用户总发送人数的分布式锁 key
     * <p>
     * 该锁用于控制同一用户（发送方）的并发访问，锁的粒度为用户级别。它的格式是：
     * "engineX:userTotalLock:userId"。通过该锁可以防止多个线程同时处理
     * 同一用户的发送任务，保证每个用户的发送请求按顺序进行。
     * <p>
     * 锁的名称动态生成，通过应用名和用户 ID 来生成唯一的锁标识符。
     */
    public static final String USER_TOTAL_LOCK = APPLICATION_NAME + "userTotalLock:";

    /**
     * 模板总发送人数的分布式锁 key
     * <p>
     * 该锁用于控制同一模板的发送任务的并发访问，锁的粒度为模板级别。它的格式是：
     * "engineX:templateTotalLock:userId"。通过该锁可以防止多个线程同时处理
     * 同一模板的发送请求，保证每个模板的发送任务按顺序进行。
     * <p>
     * 锁的名称动态生成，通过应用名和模板 ID 来生成唯一的锁标识符。
     */
    public static final String TEMPLATE_TOTAL_LOCK = APPLICATION_NAME + "templateTotalLock:";
}
