package org.nstep.engine.framework.websocket.core.sender.redis;

import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.mq.redis.core.RedisMQTemplate;
import org.nstep.engine.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.sender.WebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.session.WebSocketSessionManager;

/**
 * 基于 Redis 的 {@link WebSocketMessageSender} 实现类
 * <p>
 * 该类是一个基于 Redis 的 WebSocket 消息发送器。它通过 Redis 广播消息，利用 {@link RedisMQTemplate}
 * 将消息发送到订阅的 Redis 客户端。
 */
@Slf4j
public class RedisWebSocketMessageSender extends AbstractWebSocketMessageSender {

    /**
     * RedisMQTemplate 用于将消息发送到 Redis
     */
    private final RedisMQTemplate redisMQTemplate;

    /**
     * 构造函数，初始化 RedisWebSocketMessageSender
     *
     * @param sessionManager  WebSocket 会话管理器
     * @param redisMQTemplate Redis 消息队列模板
     */
    public RedisWebSocketMessageSender(WebSocketSessionManager sessionManager,
                                       RedisMQTemplate redisMQTemplate) {
        super(sessionManager);
        this.redisMQTemplate = redisMQTemplate;
    }

    /**
     * 发送消息到 Redis，基于用户类型和用户 ID
     *
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(Integer userType, Long userId, String messageType, String messageContent) {
        sendRedisMessage(null, userId, userType, messageType, messageContent);
    }

    /**
     * 发送消息到 Redis，基于用户类型
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(Integer userType, String messageType, String messageContent) {
        sendRedisMessage(null, null, userType, messageType, messageContent);
    }

    /**
     * 发送消息到 Redis，基于 Session ID
     *
     * @param sessionId      会话编号
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    @Override
    public void send(String sessionId, String messageType, String messageContent) {
        sendRedisMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 通过 Redis 广播消息
     * <p>
     * 将消息封装成 {@link RedisWebSocketMessage} 并发送到 Redis 消息队列。
     *
     * @param sessionId      会话编号
     * @param userId         用户编号
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    private void sendRedisMessage(String sessionId, Long userId, Integer userType,
                                  String messageType, String messageContent) {
        RedisWebSocketMessage mqMessage = new RedisWebSocketMessage();
        mqMessage.setSessionId(sessionId);
        mqMessage.setUserId(userId);
        mqMessage.setUserType(userType);
        mqMessage.setMessageType(messageType);
        mqMessage.setMessageContent(messageContent);

        // 发送消息到 Redis
        redisMQTemplate.send(mqMessage);
    }

}
