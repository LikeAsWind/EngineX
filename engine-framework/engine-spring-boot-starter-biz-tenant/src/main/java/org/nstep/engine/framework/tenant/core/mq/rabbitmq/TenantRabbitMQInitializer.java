package org.nstep.engine.framework.tenant.core.mq.rabbitmq;

import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 多租户的 RabbitMQ 初始化器
 * <p>
 * 该类实现了 Spring 的 {@link BeanPostProcessor} 接口，用于在 Spring 容器初始化完成后，
 * 对 {@link RabbitTemplate} 进行增强，添加自定义的消息处理器 {@link TenantRabbitMQMessagePostProcessor}。
 * <p>
 * 目的是在发送 RabbitMQ 消息时，自动将租户信息（tenantId）添加到消息的 Header 中，
 * 从而支持多租户环境下的消息传递。
 */
public class TenantRabbitMQInitializer implements BeanPostProcessor {

    /**
     * 在 Bean 初始化后，检查是否是 {@link RabbitTemplate} 类型的 Bean，
     * 如果是，则为其添加一个自定义的消息处理器 {@link TenantRabbitMQMessagePostProcessor}。
     *
     * @param bean     被初始化的 Bean
     * @param beanName Bean 的名称
     * @return 处理后的 Bean
     * @throws BeansException 如果在处理过程中发生异常
     */
    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        // 检查是否为 RabbitTemplate 类型的 Bean
        if (bean instanceof RabbitTemplate rabbitTemplate) {
            // 为 RabbitTemplate 添加一个自定义的消息处理器
            rabbitTemplate.addBeforePublishPostProcessors(new TenantRabbitMQMessagePostProcessor());
        }
        return bean; // 返回处理后的 Bean
    }
}
