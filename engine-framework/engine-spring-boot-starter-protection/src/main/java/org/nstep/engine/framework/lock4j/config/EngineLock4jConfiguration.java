package org.nstep.engine.framework.lock4j.config;

import com.baomidou.lock.spring.boot.autoconfigure.LockAutoConfiguration;
import org.nstep.engine.framework.lock4j.core.DefaultLockFailureStrategy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * EngineLock4jConfiguration 自动配置类，用于配置 Lock4j 锁机制的失败策略。
 * <p>
 * 该类会在项目中包含了 Lock4j 库时自动加载，并为 Spring 容器提供一个锁失败策略的 Bean。
 * 它确保在 Lock4j 锁操作失败时可以采取默认的失败策略。
 * <p>
 * 该配置会在 LockAutoConfiguration 配置类之前加载。
 */
@AutoConfiguration(before = LockAutoConfiguration.class)
@ConditionalOnClass(name = "com.baomidou.lock.annotation.Lock4j") // 只有在 Lock4j 类存在时，才会生效
public class EngineLock4jConfiguration {

    /**
     * 创建并返回一个 DefaultLockFailureStrategy 实例，定义锁失败时的处理策略。
     * <p>
     * 默认的锁失败策略通常包括重试、抛出异常等，具体行为依赖于 Lock4j 的实现。
     * 该 Bean 会被 Spring 管理，并且在需要时注入到其他组件中。
     *
     * @return DefaultLockFailureStrategy 实例，用于处理锁失败的策略
     */
    @Bean
    public DefaultLockFailureStrategy lockFailureStrategy() {
        // 创建并返回一个 DefaultLockFailureStrategy 实例
        return new DefaultLockFailureStrategy();
    }

}
