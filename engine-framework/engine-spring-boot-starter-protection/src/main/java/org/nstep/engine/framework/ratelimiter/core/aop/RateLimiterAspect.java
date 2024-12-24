package org.nstep.engine.framework.ratelimiter.core.aop;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.nstep.engine.framework.common.exception.ServiceException;
import org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.nstep.engine.framework.common.util.collection.CollectionUtils;
import org.nstep.engine.framework.ratelimiter.core.annotation.RateLimiter;
import org.nstep.engine.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import org.nstep.engine.framework.ratelimiter.core.redis.RateLimiterRedisDAO;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * 拦截声明了 {@link RateLimiter} 注解的方法，实现限流操作。
 * <p>
 * 该类通过 AOP 切面拦截被 {@link RateLimiter} 注解标记的方法，在方法执行前进行限流判断。
 * 如果超过设定的限流次数，则抛出异常，防止请求过于频繁。
 */
@Aspect  // 声明这是一个切面类
@Slf4j  // 使用 Lombok 提供的日志功能
public class RateLimiterAspect {

    /**
     * RateLimiterKeyResolver 集合，用于存储不同类型的 Key 解析器。
     * <p>
     * 根据不同的解析器策略（如全局级别、用户级别、IP级别等）来解析限流的 Key。
     */
    private final Map<Class<? extends RateLimiterKeyResolver>, RateLimiterKeyResolver> keyResolvers;

    private final RateLimiterRedisDAO rateLimiterRedisDAO;

    /**
     * 构造函数，初始化 RateLimiterKeyResolver 集合和 RateLimiterRedisDAO。
     *
     * @param keyResolvers        RateLimiterKeyResolver 列表，包含不同的 Key 解析器
     * @param rateLimiterRedisDAO 用于 Redis 操作的限流 DAO
     */
    public RateLimiterAspect(List<RateLimiterKeyResolver> keyResolvers, RateLimiterRedisDAO rateLimiterRedisDAO) {
        this.keyResolvers = CollectionUtils.convertMap(keyResolvers, RateLimiterKeyResolver::getClass);
        this.rateLimiterRedisDAO = rateLimiterRedisDAO;
    }

    /**
     * 切面方法，拦截带有 {@link RateLimiter} 注解的方法，执行限流逻辑。
     * <p>
     * 在方法执行前，检查当前请求是否超过了限流次数，如果超过则抛出 {@link ServiceException} 异常。
     *
     * @param joinPoint   连接点，提供了方法的信息
     * @param rateLimiter 限流注解对象，包含限流的配置信息
     */
    @Before("@annotation(rateLimiter)")  // 在方法执行前触发
    public void beforePointCut(JoinPoint joinPoint, RateLimiter rateLimiter) {
        // 获取对应的 RateLimiterKeyResolver 实现
        RateLimiterKeyResolver keyResolver = keyResolvers.get(rateLimiter.keyResolver());
        Assert.notNull(keyResolver, "找不到对应的 RateLimiterKeyResolver");

        // 解析出限流的 Key
        String key = keyResolver.resolver(joinPoint, rateLimiter);

        // 获取一次限流判断，尝试获取令牌
        boolean success = rateLimiterRedisDAO.tryAcquire(key,
                rateLimiter.count(), rateLimiter.time(), rateLimiter.timeUnit());

        // 如果获取失败，说明请求过于频繁，抛出异常
        if (!success) {
            log.info("[beforePointCut][方法({}) 参数({}) 请求过于频繁]", joinPoint.getSignature().toString(), joinPoint.getArgs());
            String message = StrUtil.blankToDefault(rateLimiter.message(),
                    GlobalErrorCodeConstants.TOO_MANY_REQUESTS.getMsg());
            throw new ServiceException(GlobalErrorCodeConstants.TOO_MANY_REQUESTS.getCode(), message);
        }
    }
}
