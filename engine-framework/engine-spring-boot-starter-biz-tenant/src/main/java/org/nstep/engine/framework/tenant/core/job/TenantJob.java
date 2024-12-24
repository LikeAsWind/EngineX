package org.nstep.engine.framework.tenant.core.job;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多租户 Job 注解
 * <p>
 * 该注解用于标记需要支持多租户的定时任务方法。
 * 例如，可以将这个注解应用于某些需要根据租户进行数据处理的定时任务，
 * 通过该注解标记的方法将会在执行时考虑租户信息。
 */
@Target({ElementType.METHOD}) // 该注解可以应用于方法上
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可用
public @interface TenantJob {
}
