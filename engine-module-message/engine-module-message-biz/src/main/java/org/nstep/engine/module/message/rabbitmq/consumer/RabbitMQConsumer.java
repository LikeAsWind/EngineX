package org.nstep.engine.module.message.rabbitmq.consumer;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dto.content.SendContent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 消息消费者类
 * <p>
 * 该类用于从 RabbitMQ 消费消息。它通过 `@RabbitListener` 注解监听指定的消息队列和交换机，
 * 根据消息的类型（发送或撤回）调用相应的服务方法来处理消息。
 * </p>
 */
@Component
public class RabbitMQConsumer {

    @Resource
    private ConsumerService consumerService; // 消费者服务，用于处理发送和撤回操作

    /**
     * 从 RabbitMQ 中消费消息并处理
     * <p>
     * 该方法通过 `@RabbitListener` 注解监听 RabbitMQ 消息队列。当收到消息时，方法会根据消息头部的 `messageType` 来判断消息类型：
     * - 如果是发送类型消息 (`SEND_CODE`)，则解析消息内容并调用 `consumerSend` 方法处理。
     * - 如果是撤回类型消息 (`RECALL_CODE`)，则调用 `consumerRecall` 方法进行撤回处理。
     * </p>
     *
     * @param message 消息对象，包含消息体和消息属性
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.queues}", declare = "true"), // 监听指定的消息队列
            exchange = @Exchange(value = "${engine.rabbitmq.exchange.name}"), // 监听指定的交换机
            key = "${engine.rabbitmq.topic.name}" // 监听指定的 routingKey
    ))
    public void pullMessage(Message message) {
        // 获取消息类型
        String messageType = message.getMessageProperties().getHeader("messageType");
        // 获取消息体内容
        String msgContext = new String(message.getBody());

        // 判断消息类型并进行相应处理
        if (MessageDataConstants.SEND_CODE.equals(messageType)) {
            // 如果消息类型为发送，则解析消息并调用消费者服务的发送方法
            SendContent sendContext = JSONUtil.toBean(msgContext, SendContent.class);
            consumerService.consumerSend(sendContext);
        } else if (MessageDataConstants.RECALL_CODE.equals(messageType)) {
            // 如果消息类型为撤回，则调用消费者服务的撤回方法
            consumerService.consumerRecall();
        }
    }

}
