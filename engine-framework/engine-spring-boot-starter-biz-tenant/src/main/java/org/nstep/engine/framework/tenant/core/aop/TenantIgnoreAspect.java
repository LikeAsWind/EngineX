package org.nstep.engine.framework.tenant.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;
import org.nstep.engine.framework.tenant.core.util.TenantUtils;

/**
 * 忽略多租户的 Aspect，基于 {@link TenantIgnore} 注解实现，用于一些全局的逻辑。
 * 例如说，一个定时任务，读取所有数据，进行处理。
 * 又例如说，读取所有数据，进行缓存。
 * <p>
 * 该类实现了一个切面（Aspect），用于在执行被 {@link TenantIgnore} 注解标记的方法时，
 * 忽略多租户的上下文处理。常见场景包括需要操作所有租户数据的任务（如定时任务或缓存更新）。
 * <p>
 * 整体逻辑的实现，和 {@link TenantUtils#executeIgnore(Runnable)} 需要保持一致，即
 * 在方法执行前设置忽略多租户上下文，执行方法后恢复原有的多租户上下文。
 * 表示这是一个切面类
 */
@Aspect
@Slf4j
public class TenantIgnoreAspect {

    /**
     * 环绕通知，拦截所有带有 {@link TenantIgnore} 注解的方法。
     * <p>
     * 在方法执行前，设置 TenantContextHolder 为忽略多租户上下文，
     * 执行完毕后，恢复原来的多租户上下文。
     *
     * @param joinPoint 当前执行的方法的连接点
     * @return 方法执行的结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("@annotation(TenantIgnore)") // 拦截所有带有 @TenantIgnore 注解的方法
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 保存当前的多租户上下文状态
        Boolean oldIgnore = TenantContextHolder.isIgnore();
        try {
            // 设置为忽略多租户上下文
            TenantContextHolder.setIgnore(true);
            // 执行方法逻辑
            return joinPoint.proceed();
        } finally {
            // 恢复原来的多租户上下文状态
            TenantContextHolder.setIgnore(oldIgnore);
        }
    }

}