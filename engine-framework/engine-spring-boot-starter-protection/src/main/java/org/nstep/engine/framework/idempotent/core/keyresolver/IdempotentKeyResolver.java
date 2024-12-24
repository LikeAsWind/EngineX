package org.nstep.engine.framework.idempotent.core.keyresolver;

import org.aspectj.lang.JoinPoint;
import org.nstep.engine.framework.idempotent.core.annotation.Idempotent;

/**
 * 幂等 Key 解析器接口
 * <p>
 * 该接口用于定义幂等性 Key 的解析规则。不同的解析器可以根据不同的需求，生成唯一的幂等性 Key。
 * </p>
 */
public interface IdempotentKeyResolver {

    /**
     * 解析一个 Key
     * <p>
     * 该方法根据 AOP 切面中的方法参数以及 {@link Idempotent} 注解中的配置，生成一个唯一的幂等性 Key。
     * </p>
     *
     * @param joinPoint  AOP 切面，包含了方法签名、参数等信息
     * @param idempotent {@link Idempotent} 注解，包含了幂等性相关的配置
     * @return 生成的幂等性 Key
     */
    String resolver(JoinPoint joinPoint, Idempotent idempotent);
}
