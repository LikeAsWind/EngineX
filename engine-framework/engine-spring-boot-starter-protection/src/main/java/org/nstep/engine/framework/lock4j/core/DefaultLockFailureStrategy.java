package org.nstep.engine.framework.lock4j.core;

import com.baomidou.lock.LockFailureStrategy;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.exception.ServiceException;
import org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants;

import java.lang.reflect.Method;

/**
 * 自定义获取锁失败策略，当获取锁失败时抛出 {@link ServiceException} 异常。
 * <p>
 * 该类实现了 LockFailureStrategy 接口，用于定义获取锁失败时的处理行为。具体来说，当获取锁失败时，
 * 它会记录日志并抛出一个 ServiceException 异常，通知调用者锁获取失败。
 * <p>
 * 这种策略适用于需要在锁获取失败时立刻中断并告知调用者错误的场景。
 */
@Slf4j // 使用 Lombok 注解简化日志记录
public class DefaultLockFailureStrategy implements LockFailureStrategy {

    /**
     * 当锁获取失败时调用该方法。
     * <p>
     * 该方法会记录失败的日志，并抛出一个 ServiceException 异常，表示锁获取失败。
     *
     * @param key       锁的唯一标识符
     * @param method    发生锁失败的目标方法
     * @param arguments 方法参数
     * @throws ServiceException 抛出自定义的服务异常，表示锁获取失败
     */
    @Override
    public void onLockFailure(String key, Method method, Object[] arguments) {
        // 记录锁获取失败的日志，包含线程名、锁的 key 以及方法参数
        log.debug("[onLockFailure][线程:{} 获取锁失败，key:{} 获取失败:{} ]",
                Thread.currentThread().getName(), key, arguments);

        // 抛出 ServiceException 异常，表示锁获取失败
        throw new ServiceException(GlobalErrorCodeConstants.LOCKED);
    }
}
