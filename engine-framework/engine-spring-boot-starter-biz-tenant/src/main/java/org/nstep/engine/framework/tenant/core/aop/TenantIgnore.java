package org.nstep.engine.framework.tenant.core.aop;

import java.lang.annotation.*;

/**
 * 忽略租户，标记指定方法不进行租户的自动过滤
 * <p>
 * 使用该注解可以标记某些方法在执行时不进行多租户的自动过滤，通常用于不需要租户信息的特殊场景。
 * <p>
 * 注意，只有数据库（DB）场景会进行租户过滤，其它场景暂时不会：
 * 1. Redis 场景：因为 Redis 是基于 Key 实现多租户的能力，而不是基于数据库的列（column）。所以在 Redis 场景下，忽略多租户过滤没有意义。
 * 2. MQ 场景：目前可以通过消费者（Consumer）手动在消费的方法上，添加 @TenantIgnore 进行忽略，但由于 MQ 场景的复杂性，这种方式并不自动应用。
 */
@Target({ElementType.METHOD}) // 该注解仅可用于方法上
@Retention(RetentionPolicy.RUNTIME) // 在运行时保留该注解
@Inherited // 该注解会被子类继承
public @interface TenantIgnore {
}
