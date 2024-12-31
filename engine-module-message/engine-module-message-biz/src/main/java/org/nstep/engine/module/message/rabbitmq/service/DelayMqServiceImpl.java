package org.nstep.engine.module.message.rabbitmq.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * 发送消息到延迟交换机实现类
 * <p>
 * 本类实现了 `DelayMqService` 接口，用于将消息发送到 RabbitMQ 的延迟交换机。
 * 消息在延迟交换机上会被延迟指定时间后再被消费。延迟队列常用于超时处理、定时任务等场景。
 */
@Service
@Slf4j
public class DelayMqServiceImpl implements DelayMqService {

    /**
     * 延迟队列的交换机名称
     * 该值从配置文件中读取，指定消息将发送到哪个交换机。
     */
    @Value("${engine.rabbitmq.delayedExchange.name}")
    private String delayedExchange;

    /**
     * 延迟队列的路由键
     * 该值从配置文件中读取，指定消息的路由路径。
     */
    @Value("${engine.rabbitmq.delayedRouting.key}")
    private String delayedRoutingKey;

    /**
     * RabbitMQ 的模板，负责消息的发送操作
     * 通过 `AmqpTemplate` 可以向 RabbitMQ 发送消息，支持高级功能如延迟等。
     */
    @Resource
    private AmqpTemplate rabbitTemplate;

    /**
     * 发送消息到延迟交换机
     * <p>
     * 该方法将接收到的消息发送到 RabbitMQ 的延迟交换机。消息会根据传入的 `expTime` 设置延迟时间，
     * 使得消息在队列中等待指定时间后才被消费。
     *
     * @param json    需要发送的消息内容，通常是一个 JSON 字符串
     * @param expTime 延迟时间，单位通常是毫秒。指定消息在队列中的等待时间。
     */
    @Override
    public void send(String json, String expTime) {

        // 使用 AmqpTemplate 发送消息到延迟交换机
        rabbitTemplate.convertAndSend(delayedExchange, delayedRoutingKey, json,
                message -> {
                    // 设置消息为持久化，确保消息不会因 RabbitMQ 重启而丢失
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                    // 设置消息的延迟时间（单位为毫秒），消息在队列中等待的时间
                    message.getMessageProperties().setHeader("x-delay", expTime);

                    // 返回构造好的消息
                    return message;
                });
    }
}
