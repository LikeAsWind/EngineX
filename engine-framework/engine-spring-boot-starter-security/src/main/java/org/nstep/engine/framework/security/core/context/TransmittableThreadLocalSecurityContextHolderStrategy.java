package org.nstep.engine.framework.security.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.Assert;

/**
 * 基于 TransmittableThreadLocal 实现的 Security Context 持有者策略
 * 目的是，避免 @Async 等异步执行时，原生 ThreadLocal 的丢失问题
 * 使用 TransmittableThreadLocal 来保证安全上下文在异步线程中的传递
 */
public class TransmittableThreadLocalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {

    /**
     * 使用 TransmittableThreadLocal 作为上下文存储
     * TransmittableThreadLocal 可以确保安全上下文在异步线程中传递
     */
    private static final ThreadLocal<SecurityContext> CONTEXT_HOLDER = new TransmittableThreadLocal<>();

    /**
     * 清除当前线程中的安全上下文
     * 该方法会从 ThreadLocal 中移除安全上下文
     */
    @Override
    public void clearContext() {
        CONTEXT_HOLDER.remove(); // 移除当前线程的上下文
    }

    /**
     * 获取当前线程的安全上下文
     * 如果当前线程没有安全上下文，则创建一个空的上下文并返回
     *
     * @return 当前线程的安全上下文
     */
    @Override
    public SecurityContext getContext() {
        // 获取当前线程的安全上下文
        SecurityContext ctx = CONTEXT_HOLDER.get();
        if (ctx == null) {
            // 如果没有上下文，则创建一个新的空上下文
            ctx = createEmptyContext();
            CONTEXT_HOLDER.set(ctx); // 将空上下文设置到当前线程
        }
        return ctx; // 返回安全上下文
    }

    /**
     * 设置当前线程的安全上下文
     *
     * @param context 要设置的安全上下文
     * @throws IllegalArgumentException 如果传入的上下文为 null
     */
    @Override
    public void setContext(SecurityContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted"); // 确保上下文不为空
        CONTEXT_HOLDER.set(context); // 将安全上下文设置到当前线程
    }

    /**
     * 创建一个空的安全上下文
     *
     * @return 一个新的空安全上下文实例
     */
    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl(); // 返回一个新的空安全上下文实例
    }

}
