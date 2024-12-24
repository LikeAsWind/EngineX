package org.nstep.engine.framework.websocket.core.sender.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * Kafka 消息消费者，用于接收 {@link KafkaWebSocketMessage} 消息并将其广播到 WebSocket 客户端。
 * <p>
 * 该类通过 Kafka 消费者监听 Kafka 消息队列中的消息，并将消息转发给 WebSocket 客户端。
 */
@RequiredArgsConstructor
public class KafkaWebSocketMessageConsumer {

    /**
     * KafkaWebSocketMessageSender 用于将消息发送到 WebSocket 客户端。
     */
    private final KafkaWebSocketMessageSender kafkaWebSocketMessageSender;

    /**
     * Kafka 消费者处理方法。
     * <p>
     * 该方法通过 Kafka 监听器接收来自 Kafka 消息队列的 {@link KafkaWebSocketMessage} 消息，并将其通过 WebSocket 发送出去。
     * 消息的消费是基于 Kafka 订阅的主题和消费者组来进行的。
     *
     * @param message Kafka 消息体，包含 WebSocket 消息的所有必要信息
     */
    @KafkaListener(
            topics = "${engine.websocket.sender-kafka.topic}", // 订阅的 Kafka 主题
            groupId = "${engine.websocket.sender-kafka.consumer-group}" + "-" + "#{T(java.util.UUID).randomUUID()}")
    // 使用 UUID 确保每个消费者有独特的消费者组
    @RabbitHandler
    public void onMessage(KafkaWebSocketMessage message) {
        // 调用 KafkaWebSocketMessageSender 将消息通过 WebSocket 发送给客户端
        kafkaWebSocketMessageSender.send(
                message.getSessionId(), // 会话 ID
                message.getUserType(),  // 用户类型
                message.getUserId(),    // 用户 ID
                message.getMessageType(), // 消息类型
                message.getMessageContent() // 消息内容
        );
    }
}
