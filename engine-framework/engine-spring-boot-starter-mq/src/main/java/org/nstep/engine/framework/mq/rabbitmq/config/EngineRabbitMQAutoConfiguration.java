package org.nstep.engine.framework.mq.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * RabbitMQ 消息队列配置类
 * 该配置类用于自动配置 RabbitMQ 消息队列的相关组件，确保应用能够与 RabbitMQ 正常交互。
 */
@AutoConfiguration
@Slf4j
@ConditionalOnClass(name = "org.springframework.amqp.rabbit.core.RabbitTemplate") // 只有在存在 RabbitTemplate 类时才会生效
public class EngineRabbitMQAutoConfiguration {

    /**
     * Jackson2JsonMessageConverter Bean：使用 Jackson 序列化和反序列化消息
     *
     * @return 配置好的消息转换器
     */
    @Bean
    public MessageConverter createMessageConverter() {
        // 创建并返回 Jackson2JsonMessageConverter 实例
        // 该转换器使用 Jackson 序列化和反序列化消息体
        return new Jackson2JsonMessageConverter();
    }

}
