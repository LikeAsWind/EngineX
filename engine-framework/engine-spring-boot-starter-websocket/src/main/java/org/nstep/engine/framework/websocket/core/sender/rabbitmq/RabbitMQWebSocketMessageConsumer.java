package org.nstep.engine.framework.websocket.core.sender.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;

/**
 * {@link RabbitMQWebSocketMessage} 广播消息的消费者，真正把消息发送出去
 * <p>
 * 该类作为 RabbitMQ 消息消费者，监听从 RabbitMQ 队列中接收到的 WebSocket 消息，并通过 {@link RabbitMQWebSocketMessageSender}
 * 将消息发送到对应的 WebSocket 会话中。通过该消费者，消息可以广播到多个 WebSocket 客户端。
 */
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        // 在 Queue 的名字上，使用 UUID 生成其后缀。这样，启动的 Consumer 的 Queue 不同，以达到广播消费的目的
                        name = "${engine.websocket.sender-rabbitmq.queue}" + "-" + "#{T(java.util.UUID).randomUUID()}",
                        // Consumer 关闭时，该队列就可以被自动删除了
                        autoDelete = "true"
                ),
                exchange = @Exchange(
                        name = "${engine.websocket.sender-rabbitmq.exchange}",
                        type = ExchangeTypes.TOPIC,
                        declare = "false"
                )
        )
)
@RequiredArgsConstructor
public class RabbitMQWebSocketMessageConsumer {

    /**
     * 用于将消息发送到 WebSocket 会话的 {@link RabbitMQWebSocketMessageSender} 实例
     */
    private final RabbitMQWebSocketMessageSender rabbitMQWebSocketMessageSender;

    /**
     * 处理从 RabbitMQ 队列中接收到的消息，并将其转发到 WebSocket 会话。
     * <p>
     * 该方法会被 {@link RabbitListener} 自动调用，用于消费队列中的消息，并通过 {@link RabbitMQWebSocketMessageSender}
     * 将消息推送到 WebSocket 客户端。
     *
     * @param message 从 RabbitMQ 队列中接收到的 {@link RabbitMQWebSocketMessage} 消息
     */
    @RabbitHandler
    public void onMessage(RabbitMQWebSocketMessage message) {
        // 使用 {@link RabbitMQWebSocketMessageSender} 将接收到的消息发送到 WebSocket 会话
        rabbitMQWebSocketMessageSender.send(message.getSessionId(),
                message.getUserType(), message.getUserId(),
                message.getMessageType(), message.getMessageContent());
    }
}
