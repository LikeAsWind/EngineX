package org.nstep.engine.framework.websocket.core.sender.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.sender.WebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.session.WebSocketSessionManager;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 基于 RabbitMQ 的 {@link WebSocketMessageSender} 实现类
 * <p>
 * 该类实现了 {@link WebSocketMessageSender} 接口，使用 RabbitMQ 作为消息中间件来广播 WebSocket 消息。
 * 它通过 RabbitMQ 将 WebSocket 消息发送到指定的队列，允许多个 WebSocket 客户端接收消息。
 */
@Slf4j
public class RabbitMQWebSocketMessageSender extends AbstractWebSocketMessageSender {

    /**
     * 用于与 RabbitMQ 进行交互的 {@link RabbitTemplate} 实例
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * RabbitMQ 中的 {@link TopicExchange}，用于指定消息发送的交换机
     */
    private final TopicExchange topicExchange;

    /**
     * 构造函数，初始化 {@link RabbitMQWebSocketMessageSender} 实例。
     *
     * @param sessionManager 管理 WebSocket 会话的 {@link WebSocketSessionManager} 实例
     * @param rabbitTemplate 用于发送消息的 {@link RabbitTemplate} 实例
     * @param topicExchange  用于消息发送的 {@link TopicExchange} 实例
     */
    public RabbitMQWebSocketMessageSender(WebSocketSessionManager sessionManager,
                                          RabbitTemplate rabbitTemplate,
                                          TopicExchange topicExchange) {
        super(sessionManager);
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
    }

    /**
     * 发送消息到 RabbitMQ（指定用户类型和用户 ID）
     *
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(Integer userType, Long userId, String messageType, String messageContent) {
        // 调用内部方法发送 RabbitMQ 消息
        sendRabbitMQMessage(null, userId, userType, messageType, messageContent);
    }

    /**
     * 发送消息到 RabbitMQ（仅指定用户类型）
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(Integer userType, String messageType, String messageContent) {
        // 调用内部方法发送 RabbitMQ 消息
        sendRabbitMQMessage(null, null, userType, messageType, messageContent);
    }

    /**
     * 发送消息到 RabbitMQ（仅指定会话 ID）
     *
     * @param sessionId      会话编号
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(String sessionId, String messageType, String messageContent) {
        // 调用内部方法发送 RabbitMQ 消息
        sendRabbitMQMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 通过 RabbitMQ 广播消息
     * <p>
     * 将消息构建成 {@link RabbitMQWebSocketMessage} 对象，并通过 {@link RabbitTemplate} 将消息发送到 RabbitMQ 的交换机。
     *
     * @param sessionId      会话编号
     * @param userId         用户编号
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    private void sendRabbitMQMessage(String sessionId, Long userId, Integer userType,
                                     String messageType, String messageContent) {
        // 创建 RabbitMQ 消息对象
        RabbitMQWebSocketMessage mqMessage = new RabbitMQWebSocketMessage();
        mqMessage.setSessionId(sessionId);
        mqMessage.setUserId(userId);
        mqMessage.setUserType(userType);
        mqMessage.setMessageType(messageType);
        mqMessage.setMessageContent(messageContent);

        // 将消息发送到 RabbitMQ 的指定交换机
        rabbitTemplate.convertAndSend(topicExchange.getName(), null, mqMessage);
    }

}
