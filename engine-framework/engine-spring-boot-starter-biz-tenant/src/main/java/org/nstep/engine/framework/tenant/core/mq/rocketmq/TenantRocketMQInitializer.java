package org.nstep.engine.framework.tenant.core.mq.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 多租户的 RocketMQ 初始化器
 * <p>
 * 该类实现了 RocketMQ 的初始化器，用于在 RocketMQ 的消费者和生产者中注册多租户的拦截器。
 * 它确保在发送和消费消息时，租户信息会被正确地处理。
 */
public class TenantRocketMQInitializer implements BeanPostProcessor {

    /**
     * 在 Bean 初始化后，注册多租户的消息拦截器
     *
     * @param bean 当前初始化的 Bean
     * @param beanName Bean 的名称
     * @return 返回处理后的 Bean
     * @throws BeansException 如果处理过程中出现异常
     */
    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        // 处理 RocketMQ 消费者
        if (bean instanceof DefaultRocketMQListenerContainer container) {
            initTenantConsumer(container.getConsumer());
        }
        // 处理 RocketMQ 生产者
        else if (bean instanceof RocketMQTemplate template) {
            initTenantProducer(template.getProducer());
        }
        return bean;
    }

    /**
     * 初始化多租户的生产者，注册消息发送钩子
     *
     * @param producer RocketMQ 的生产者
     */
    private void initTenantProducer(DefaultMQProducer producer) {
        if (producer == null) {
            return;
        }
        DefaultMQProducerImpl producerImpl = producer.getDefaultMQProducerImpl();
        if (producerImpl != null) {
            // 注册发送消息的拦截器
            producerImpl.registerSendMessageHook(new TenantRocketMQSendMessageHook());
        }
    }

    /**
     * 初始化多租户的消费者，注册消息消费钩子
     *
     * @param consumer RocketMQ 的消费者
     */
    private void initTenantConsumer(DefaultMQPushConsumer consumer) {
        if (consumer == null) {
            return;
        }
        DefaultMQPushConsumerImpl consumerImpl = consumer.getDefaultMQPushConsumerImpl();
        if (consumerImpl != null) {
            // 注册消费消息的拦截器
            consumerImpl.registerConsumeMessageHook(new TenantRocketMQConsumeMessageHook());
        }
    }
}
