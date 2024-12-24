package org.nstep.engine.framework.ratelimiter.core.annotation;

import org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.nstep.engine.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import org.nstep.engine.framework.ratelimiter.core.keyresolver.impl.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解，用于方法上标记需要进行限流的操作。
 * <p>
 * 该注解可以应用于方法上，表示该方法需要进行限流控制。通过配置限流的时间窗口、次数等参数来限制请求频率。
 * 适用于高并发场景中，防止某些操作被过度频繁调用。
 */
@Target({ElementType.METHOD})  // 注解应用于方法上
@Retention(RetentionPolicy.RUNTIME)  // 注解在运行时可用
public @interface RateLimiter {

    /**
     * 限流的时间窗口，单位为秒，默认为 1 秒。
     * <p>
     * 表示在一定时间内（例如 1 秒内）允许的最大请求次数。
     *
     * @return 限流时间窗口的大小
     */
    int time() default 1;

    /**
     * 时间单位，默认为秒（SECONDS）。
     * <p>
     * 限流时间窗口的单位，支持秒、毫秒等时间单位。
     *
     * @return 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流次数，默认为 100 次。
     * <p>
     * 表示在指定的时间窗口内允许的最大请求次数。
     *
     * @return 限流的最大次数
     */
    int count() default 100;

    /**
     * 请求过快时的提示信息，默认为空字符串，使用默认的错误提示 {@link GlobalErrorCodeConstants#TOO_MANY_REQUESTS}。
     * <p>
     * 如果为空，系统会使用默认的错误提示，表示请求频率过高。
     *
     * @return 请求过快时的提示信息
     */
    String message() default ""; // 为空时，使用 TOO_MANY_REQUESTS 错误提示

    /**
     * 使用的 Key 解析器，用于生成限流的唯一 Key。
     * <p>
     * 限流是基于某些唯一标识来进行的，比如用户 ID、IP 地址等。通过配置不同的解析器，可以灵活选择限流策略。
     * 默认使用全局级别的 {@link DefaultRateLimiterKeyResolver}。
     *
     * @return 限流 Key 解析器
     * @see DefaultRateLimiterKeyResolver 全局级别
     * @see UserRateLimiterKeyResolver 用户 ID 级别
     * @see ClientIpRateLimiterKeyResolver 用户 IP 级别
     * @see ServerNodeRateLimiterKeyResolver 服务器 Node 级别
     * @see ExpressionRateLimiterKeyResolver 自定义表达式
     */
    Class<? extends RateLimiterKeyResolver> keyResolver() default DefaultRateLimiterKeyResolver.class;

    /**
     * 使用的 Key 参数，用于自定义 Key 解析时指定参数。
     * <p>
     * 该字段用于指定限流 Key 的计算依据，通常与 {@link #keyResolver()} 配合使用。
     * 如果使用了 {@link ExpressionRateLimiterKeyResolver}，可以通过此参数指定表达式计算限流 Key。
     *
     * @return Key 参数
     */
    String keyArg() default "";
}
