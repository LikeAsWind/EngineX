package org.nstep.engine.module.message.rabbitmq.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ 生产者服务实现
 * <p>
 * 该类是 `RabbitMQService` 接口的实现，主要用于通过 RabbitMQ 发送消息。消息发送时会根据提供的上下文（JSON 格式）和指令（发送或撤回）将消息发布到指定的交换机和队列。
 * 实现类使用 `RabbitTemplate` 发送消息，并且在发送的消息上设置一些额外的头部信息（例如：消息类型 `sendCode`）。
 */
@Service
@Slf4j
public class RabbitMQServiceImpl implements RabbitMQService {

    /**
     * RabbitTemplate 用于消息的发送
     * RabbitTemplate 是 Spring AMQP 提供的用于与 RabbitMQ 进行交互的核心类，负责发送消息到 RabbitMQ 队列。
     */
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 交换机名称
     * 存储了从配置文件中获取的 RabbitMQ 交换机名称，用于发布消息。
     */
    @Value("${engine.rabbitmq.exchange.name}")
    private String exchangeName;

    /**
     * 路由键（routing key）
     * 存储了从配置文件中获取的 RabbitMQ 路由键，用于指定消息发送到的队列。
     */
    @Value("${engine.rabbitmq.topic.name}")
    private String topicKey;

    /**
     * 发送消息到 RabbitMQ
     * <p>
     * 该方法将任务的上下文（以 JSON 字符串格式传递）发送到 RabbitMQ，消息通过指定的交换机和路由键发送。根据 `sendCode` 参数，
     * 消息的类型可以是“发送”或“撤回”。通过在消息头中设置 `messageType` 来标识消息的类型。
     *
     * @param json     任务的上下文，以 JSON 字符串的形式传递。它包含了任务的所有详细信息。
     * @param sendCode 用于区分消息的类型（例如：发送或撤回）。通常是一个字符串（例如 "send" 或 "revoke"），它将作为消息头的一部分。
     */
    @Override
    public void send(String json, String sendCode) {
        // 设置消息头部信息，区分是发送消息还是撤回消息
        MessagePostProcessor messagePostProcessor = message -> {
            // 将消息类型（发送/撤回）作为消息头的一个字段
            message.getMessageProperties().setHeader("messageType", sendCode);
            return message;
        };

        // 将消息发送到指定的交换机和路由键
        rabbitTemplate.convertAndSend(exchangeName, topicKey, json, messagePostProcessor);
    }
}
