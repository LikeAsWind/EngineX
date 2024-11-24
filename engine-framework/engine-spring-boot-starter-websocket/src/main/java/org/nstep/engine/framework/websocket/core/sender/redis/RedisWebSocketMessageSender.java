package org.nstep.engine.framework.websocket.core.sender.redis;

import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.mq.redis.core.RedisMQTemplate;
import org.nstep.engine.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.sender.WebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.session.WebSocketSessionManager;

/**
 * 基于 Redis 的 {@link WebSocketMessageSender} 实现类
 */
@Slf4j
public class RedisWebSocketMessageSender extends AbstractWebSocketMessageSender {

    private final RedisMQTemplate redisMQTemplate;

    public RedisWebSocketMessageSender(WebSocketSessionManager sessionManager,
                                       RedisMQTemplate redisMQTemplate) {
        super(sessionManager);
        this.redisMQTemplate = redisMQTemplate;
    }

    @Override
    public void send(Integer userType, Long userId, String messageType, String messageContent) {
        sendRedisMessage(null, userId, userType, messageType, messageContent);
    }

    @Override
    public void send(Integer userType, String messageType, String messageContent) {
        sendRedisMessage(null, null, userType, messageType, messageContent);
    }

    @Override
    public void send(String sessionId, String messageType, String messageContent) {
        sendRedisMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 通过 Redis 广播消息
     *
     * @param sessionId      Session 编号
     * @param userId         用户编号
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    private void sendRedisMessage(String sessionId, Long userId, Integer userType,
                                  String messageType, String messageContent) {
        RedisWebSocketMessage mqMessage = new RedisWebSocketMessage()
                .setSessionId(sessionId).setUserId(userId).setUserType(userType)
                .setMessageType(messageType).setMessageContent(messageContent);
        redisMQTemplate.send(mqMessage);
    }

}
