package org.nstep.engine.framework.websocket.core.sender.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.nstep.engine.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.sender.WebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.session.WebSocketSessionManager;

/**
 * 基于 RocketMQ 的 {@link WebSocketMessageSender} 实现类
 * <p>
 * 该类实现了 WebSocket 消息发送器，通过 RocketMQ 发送 WebSocket 消息。它将消息通过 RocketMQ 广播到指定的 topic。
 * 该类继承自 {@link AbstractWebSocketMessageSender}，并实现了消息发送的具体逻辑。
 */
@Slf4j
public class RocketMQWebSocketMessageSender extends AbstractWebSocketMessageSender {

    /**
     * RocketMQTemplate 实例，用于与 RocketMQ 进行交互
     */
    private final RocketMQTemplate rocketMQTemplate;

    /**
     * RocketMQ 发送消息的 topic
     */
    private final String topic;

    /**
     * 构造函数，初始化 RocketMQWebSocketMessageSender
     *
     * @param sessionManager   WebSocket 会话管理器
     * @param rocketMQTemplate RocketMQTemplate 实例
     * @param topic            消息发送的 RocketMQ topic
     */
    public RocketMQWebSocketMessageSender(WebSocketSessionManager sessionManager,
                                          RocketMQTemplate rocketMQTemplate,
                                          String topic) {
        super(sessionManager);
        this.rocketMQTemplate = rocketMQTemplate;
        this.topic = topic;
    }

    /**
     * 发送消息，基于用户类型和用户 ID
     *
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(Integer userType, Long userId, String messageType, String messageContent) {
        sendRocketMQMessage(null, userId, userType, messageType, messageContent);
    }

    /**
     * 发送消息，基于用户类型
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(Integer userType, String messageType, String messageContent) {
        sendRocketMQMessage(null, null, userType, messageType, messageContent);
    }

    /**
     * 发送消息，基于会话 ID
     *
     * @param sessionId      会话编号
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(String sessionId, String messageType, String messageContent) {
        sendRocketMQMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 通过 RocketMQ 广播消息
     * <p>
     * 该方法将构造一个 RocketMQWebSocketMessage 对象，并通过 RocketMQTemplate 将其发送到指定的 topic。
     *
     * @param sessionId      会话编号
     * @param userId         用户编号
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    private void sendRocketMQMessage(String sessionId, Long userId, Integer userType,
                                     String messageType, String messageContent) {
        RocketMQWebSocketMessage mqMessage = new RocketMQWebSocketMessage();
        mqMessage.setSessionId(sessionId);
        mqMessage.setUserId(userId);
        mqMessage.setUserType(userType);
        mqMessage.setMessageType(messageType);
        mqMessage.setMessageContent(messageContent);
        // 通过 RocketMQTemplate 同步发送消息
        rocketMQTemplate.syncSend(topic, mqMessage);
    }

}
