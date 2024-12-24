package org.nstep.engine.framework.quartz.config;

import com.alibaba.ttl.TtlRunnable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步任务配置类
 * <p>
 * 该配置类用于配置 Spring 异步任务相关的设置。通过启用异步任务和自定义线程池任务执行器来实现异步任务的处理。
 * 本类通过 `@EnableAsync` 注解启用 Spring 的异步支持，并通过 `BeanPostProcessor` 修改线程池任务执行器，接入 `TransmittableThreadLocal` 以支持跨线程传递线程局部变量。
 */
@AutoConfiguration
@EnableAsync // 启用 Spring 异步任务支持
public class EngineAsyncAutoConfiguration {

    /**
     * 配置自定义的线程池任务执行器
     * <p>
     * 通过 `BeanPostProcessor` 修改线程池任务执行器，确保每个提交的任务都能够接入 `TransmittableThreadLocal`，
     * 使得线程局部变量（例如：用户上下文信息）能够在异步线程中传递。
     *
     * @return 自定义的 `BeanPostProcessor`，用于修改线程池任务执行器
     */
    @Bean
    public BeanPostProcessor threadPoolTaskExecutorBeanPostProcessor() {
        return new BeanPostProcessor() {

            /**
             * 在初始化之前对线程池任务执行器进行处理
             * <p>
             * 如果 `bean` 是 `ThreadPoolTaskExecutor` 类型，则为其设置任务装饰器，接入 `TransmittableThreadLocal`。
             *
             * @param bean 要处理的 bean 对象
             * @param beanName bean 的名称
             * @return 处理后的 bean 对象
             * @throws BeansException 如果发生错误，则抛出异常
             */
            @Override
            public Object postProcessBeforeInitialization(@Nullable Object bean, @Nullable String beanName) throws BeansException {
                // 如果 bean 不是 ThreadPoolTaskExecutor 类型，则直接返回
                if (!(bean instanceof ThreadPoolTaskExecutor executor)) {
                    return bean;
                }
                // 修改任务提交方式，接入 TransmittableThreadLocal
                executor.setTaskDecorator(TtlRunnable::get);
                return executor;
            }

        };
    }

}
