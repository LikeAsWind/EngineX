package org.nstep.engine.framework.websocket.core.sender.rocketmq;

import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

/**
 * {@link RocketMQWebSocketMessage} 广播消息的消费者，真正把消息发送出去
 * <p>
 * 该类用于消费 RocketMQ 广播的 WebSocket 消息，并将消息发送到对应的 WebSocket 会话。它实现了 RocketMQListener 接口，
 * 通过 RocketMQ 消费消息并调用 {@link RocketMQWebSocketMessageSender} 来广播 WebSocket 消息。
 */
@RocketMQMessageListener( // 重点：添加 @RocketMQMessageListener 注解，声明消费的 topic
        topic = "${engine.websocket.sender-rocketmq.topic}", // 消费的 RocketMQ topic
        consumerGroup = "${engine.websocket.sender-rocketmq.consumer-group}", // 消费者组
        messageModel = MessageModel.BROADCASTING // 设置为广播模式，保证每个实例都能收到消息
)
@RequiredArgsConstructor
public class RocketMQWebSocketMessageConsumer implements RocketMQListener<RocketMQWebSocketMessage> {

    /**
     * RocketMQWebSocketMessageSender 实例，用于发送消息到 WebSocket 客户端
     */
    private final RocketMQWebSocketMessageSender rocketMQWebSocketMessageSender;

    /**
     * 处理接收到的消息并将其通过 WebSocket 发送到客户端
     * <p>
     * 该方法会在接收到 RocketMQ 消息后调用，通过 {@link RocketMQWebSocketMessageSender} 将消息发送到 WebSocket 客户端。
     *
     * @param message 接收到的 RocketMQWebSocketMessage 消息
     */
    @Override
    public void onMessage(RocketMQWebSocketMessage message) {
        // 调用 RocketMQWebSocketMessageSender 发送消息到 WebSocket 客户端
        rocketMQWebSocketMessageSender.send(message.getSessionId(),
                message.getUserType(), message.getUserId(),
                message.getMessageType(), message.getMessageContent());
    }

}
