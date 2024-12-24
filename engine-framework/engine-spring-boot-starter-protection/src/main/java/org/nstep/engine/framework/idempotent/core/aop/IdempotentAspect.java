package org.nstep.engine.framework.idempotent.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nstep.engine.framework.common.exception.ServiceException;
import org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.nstep.engine.framework.common.util.collection.CollectionUtils;
import org.nstep.engine.framework.idempotent.core.annotation.Idempotent;
import org.nstep.engine.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import org.nstep.engine.framework.idempotent.core.redis.IdempotentRedisDAO;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * 拦截声明了 {@link Idempotent} 注解的方法，实现幂等操作。
 * <p>
 * 该切面类用于拦截带有 {@link Idempotent} 注解的方法，确保方法在短时间内只会被执行一次。
 * 它通过 Redis 锁机制来防止重复请求，确保幂等性。
 * </p>
 */
@Aspect
@Slf4j  // 使用 Lombok 提供的日志功能
public class IdempotentAspect {

    /**
     * IdempotentKeyResolver 集合，存储不同的 Key 解析器。
     * <p>
     * 用于根据不同的 Key 解析器来生成唯一的请求标识符。
     * </p>
     */
    private final Map<Class<? extends IdempotentKeyResolver>, IdempotentKeyResolver> keyResolvers;

    /**
     * IdempotentRedisDAO，用于操作 Redis 存储的幂等性 Key。
     * <p>
     * 主要用于在 Redis 中设置、删除幂等性锁。
     * </p>
     */
    private final IdempotentRedisDAO idempotentRedisDAO;

    /**
     * 构造方法，初始化 keyResolvers 和 idempotentRedisDAO。
     *
     * @param keyResolvers       List 类型的 IdempotentKeyResolver 实现类，负责解析幂等性 Key。
     * @param idempotentRedisDAO 操作 Redis 的 DAO 类，用于存储和删除幂等性 Key。
     */
    public IdempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        // 将 keyResolvers 转换为 Map，方便通过解析器类型快速获取对应的实例
        this.keyResolvers = CollectionUtils.convertMap(keyResolvers, IdempotentKeyResolver::getClass);
        this.idempotentRedisDAO = idempotentRedisDAO;
    }

    /**
     * 环绕通知，拦截标记了 {@link Idempotent} 注解的方法，执行幂等性控制。
     * <p>
     * 主要步骤：
     * 1. 获取幂等性 Key 解析器；
     * 2. 使用解析器生成唯一的请求 Key；
     * 3. 尝试在 Redis 中设置幂等性锁；
     * 4. 如果锁定成功，继续执行方法；如果锁定失败，抛出重复请求异常；
     * 5. 如果方法执行过程中抛出异常，删除 Redis 中的幂等性 Key（如果配置了）。
     * </p>
     *
     * @param joinPoint  连接点，表示被拦截的方法
     * @param idempotent {@link Idempotent} 注解，包含了幂等性的相关配置
     * @return 方法执行的返回值
     * @throws Throwable 方法执行过程中可能抛出的异常
     */
    @Around(value = "@annotation(idempotent)")  // 拦截带有 @Idempotent 注解的方法
    public Object aroundPointCut(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        // 1. 获得对应的 IdempotentKeyResolver
        IdempotentKeyResolver keyResolver = keyResolvers.get(idempotent.keyResolver());
        Assert.notNull(keyResolver, "找不到对应的 IdempotentKeyResolver");

        // 2. 解析请求的 Key
        String key = keyResolver.resolver(joinPoint, idempotent);

        // 3. 锁定 Key
        boolean success = idempotentRedisDAO.setIfAbsent(key, idempotent.timeout(), idempotent.timeUnit());
        // 锁定失败，表示请求重复，抛出异常
        if (!success) {
            log.info("[aroundPointCut][方法({}) 参数({}) 存在重复请求]", joinPoint.getSignature().toString(), joinPoint.getArgs());
            throw new ServiceException(GlobalErrorCodeConstants.REPEATED_REQUESTS.getCode(), idempotent.message());
        }

        // 4. 执行方法逻辑
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            // 5. 如果发生异常，删除 Redis 中的 Key
            // 参考美团 GTIS 思路：https://tech.meituan.com/2016/09/29/distributed-system-mutually-exclusive-idempotence-cerberus-gtis.html
            if (idempotent.deleteKeyWhenException()) {
                idempotentRedisDAO.delete(key);
            }
            throw throwable;
        }
    }
}
