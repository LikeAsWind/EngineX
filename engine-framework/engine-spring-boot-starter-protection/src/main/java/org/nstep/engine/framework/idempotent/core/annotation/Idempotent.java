package org.nstep.engine.framework.idempotent.core.annotation;

import com.baomidou.lock.annotation.Lock4j;
import org.nstep.engine.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import org.nstep.engine.framework.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import org.nstep.engine.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import org.nstep.engine.framework.idempotent.core.keyresolver.impl.UserIdempotentKeyResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 幂等注解，用于标记需要实现幂等性的接口方法。
 * <p>
 * 幂等性是指无论执行多少次，结果都是相同的。该注解可以应用于方法上，
 * 以确保该方法在短时间内只会被执行一次，避免重复请求带来的问题（如重复支付、重复提交等）。
 * </p>
 */
@Target({ElementType.METHOD})  // 注解可以应用于方法
@Retention(RetentionPolicy.RUNTIME)  // 注解在运行时可用
public @interface Idempotent {

    /**
     * 幂等的超时时间，默认为 1 秒。
     * <p>
     * 超时时间是指在规定时间内，若有相同的请求，系统将不会再次执行该方法。
     * 如果执行时间超过超时时间，仍然允许新的请求进入。
     * </p>
     */
    int timeout() default 1;

    /**
     * 时间单位，默认为 SECONDS（秒）。
     * <p>
     * 可以选择时间单位（如秒、毫秒等），以便控制超时时间的粒度。
     * </p>
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 提示信息，正在执行中的提示。
     * <p>
     * 当请求被标记为重复请求时，返回给用户的提示信息。
     * </p>
     */
    String message() default "重复请求，请稍后重试";

    /**
     * 使用的 Key 解析器。
     * <p>
     * 该属性指定了幂等性操作中使用的 Key 解析器，决定了如何生成唯一的 Key 来标识请求。
     * <ul>
     *   <li>{@link DefaultIdempotentKeyResolver}：全局级别的 Key 解析器。</li>
     *   <li>{@link UserIdempotentKeyResolver}：基于用户的 Key 解析器。</li>
     *   <li>{@link ExpressionIdempotentKeyResolver}：自定义表达式，通过 {@link #keyArg()} 计算。</li>
     * </ul>
     * </p>
     */
    Class<? extends IdempotentKeyResolver> keyResolver() default DefaultIdempotentKeyResolver.class;

    /**
     * 使用的 Key 参数。
     * <p>
     * 在自定义 Key 解析器时，指定要用作 Key 的参数名。
     * 如果使用 {@link ExpressionIdempotentKeyResolver}，可以通过该参数指定表达式中使用的参数名。
     * </p>
     */
    String keyArg() default "";

    /**
     * 删除 Key，当发生异常时。
     * <p>
     * 如果请求发生异常，是否删除对应的幂等性 Key。默认值为 true。
     * <p>
     * 发生异常时，说明业务逻辑有问题，此时需要删除 Key，避免下一次请求无法正常执行。
     * </p>
     * <p>
     * 为什么不在成功时删除 Key 呢？这时候本质上是分布式锁，推荐使用 {@link Lock4j} 注解来处理成功时的锁定。
     * </p>
     */
    boolean deleteKeyWhenException() default true;
}
