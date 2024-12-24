package org.nstep.engine.framework.ratelimiter.core.keyresolver;

import org.aspectj.lang.JoinPoint;
import org.nstep.engine.framework.ratelimiter.core.annotation.RateLimiter;

/**
 * 限流 Key 解析器接口
 * <p>
 * 该接口用于定义如何根据不同的规则解析出限流的 Key。实现该接口的类可以根据具体的业务需求，
 * 解析出用于限流判断的唯一标识符。
 */
public interface RateLimiterKeyResolver {

    /**
     * 解析一个 Key
     * <p>
     * 该方法根据限流注解和 AOP 切面中的方法信息，生成一个唯一的 Key。这个 Key 用于在 Redis 中进行
     * 限流操作，判断当前请求是否超过设定的限流次数。
     *
     * @param rateLimiter 限流注解对象，包含限流的配置，如时间、次数等
     * @param joinPoint   AOP 切面，提供了目标方法的信息（如方法名、参数等）
     * @return Key 返回用于限流判断的 Key
     */
    String resolver(JoinPoint joinPoint, RateLimiter rateLimiter);

}
