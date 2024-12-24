package org.nstep.engine.framework.websocket.core.sender.kafka;

import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.sender.WebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.session.WebSocketSessionManager;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.ExecutionException;

/**
 * 基于 Kafka 的 {@link WebSocketMessageSender} 实现类
 * <p>
 * 该类负责将 WebSocket 消息发送到 Kafka 消息队列，以便其他消费者将其广播到 WebSocket 客户端。
 */
@Slf4j
public class KafkaWebSocketMessageSender extends AbstractWebSocketMessageSender {

    /**
     * Kafka 消息模板，用于发送消息到 Kafka。
     */
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    /**
     * Kafka 主题，用于指定消息发送到的目标主题。
     */
    private final String topic;

    /**
     * 构造函数，初始化 Kafka 消息发送器。
     *
     * @param sessionManager 会话管理器
     * @param kafkaTemplate  Kafka 消息模板
     * @param topic          Kafka 主题
     */
    public KafkaWebSocketMessageSender(WebSocketSessionManager sessionManager,
                                       KafkaTemplate<Object, Object> kafkaTemplate,
                                       String topic) {
        super(sessionManager);
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    /**
     * 发送消息到 Kafka，按用户类型和用户编号发送。
     *
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(Integer userType, Long userId, String messageType, String messageContent) {
        sendKafkaMessage(null, userId, userType, messageType, messageContent);
    }

    /**
     * 发送消息到 Kafka，按用户类型发送。
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(Integer userType, String messageType, String messageContent) {
        sendKafkaMessage(null, null, userType, messageType, messageContent);
    }

    /**
     * 发送消息到 Kafka，按会话 ID 发送。
     *
     * @param sessionId      会话 ID
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(String sessionId, String messageType, String messageContent) {
        sendKafkaMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 通过 Kafka 广播消息
     *
     * @param sessionId      会话编号
     * @param userId         用户编号
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    private void sendKafkaMessage(String sessionId, Long userId, Integer userType,
                                  String messageType, String messageContent) {
        // 创建 Kafka 消息对象
        KafkaWebSocketMessage mqMessage = new KafkaWebSocketMessage();
        mqMessage.setSessionId(sessionId);
        mqMessage.setUserId(userId);
        mqMessage.setUserType(userType);
        mqMessage.setMessageType(messageType);
        mqMessage.setMessageContent(messageContent);

        try {
            // 发送消息到 Kafka
            kafkaTemplate.send(topic, mqMessage).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("[sendKafkaMessage][发送消息({}) 到 Kafka 失败]", mqMessage, e);
        }
    }
}
