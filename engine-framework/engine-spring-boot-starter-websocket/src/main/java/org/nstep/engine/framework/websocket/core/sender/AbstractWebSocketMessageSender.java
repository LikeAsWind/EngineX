package org.nstep.engine.framework.websocket.core.sender;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.framework.websocket.core.message.JsonWebSocketMessage;
import org.nstep.engine.framework.websocket.core.session.WebSocketSessionManager;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * WebSocketMessageSender 实现类
 * <p>
 * 该抽象类实现了 WebSocket 消息发送的基本逻辑。它负责根据不同的条件（如 sessionId、userType 和 userId）来选择 WebSocket 会话，并通过这些会话发送消息。
 * 子类需要实现具体的消息发送方式。
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractWebSocketMessageSender implements WebSocketMessageSender {

    /**
     * WebSocket 会话管理器，用于获取会话列表和单个会话
     */
    private final WebSocketSessionManager sessionManager;

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
        send(null, userType, userId, messageType, messageContent);
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
        send(null, userType, null, messageType, messageContent);
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
        send(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 发送消息的核心方法
     * <p>
     * 根据传入的会话 ID、用户类型、用户 ID 等条件，查找匹配的 WebSocket 会话列表，并调用 {@link #doSend} 方法进行消息发送。
     *
     * @param sessionId      会话编号
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    public void send(String sessionId, Integer userType, Long userId, String messageType, String messageContent) {
        // 1. 根据条件获取匹配的 WebSocket 会话列表
        List<WebSocketSession> sessions = Collections.emptyList();
        if (StrUtil.isNotEmpty(sessionId)) {
            // 如果提供了 sessionId，则根据 sessionId 获取会话
            WebSocketSession session = sessionManager.getSession(sessionId);
            if (session != null) {
                sessions = Collections.singletonList(session);
            }
        } else if (userType != null && userId != null) {
            // 如果提供了 userType 和 userId，则根据这两个条件获取会话列表
            sessions = (List<WebSocketSession>) sessionManager.getSessionList(userType, userId);
        } else if (userType != null) {
            // 如果只有 userType，则根据 userType 获取会话列表
            sessions = (List<WebSocketSession>) sessionManager.getSessionList(userType);
        }

        // 如果没有找到匹配的会话，则记录日志并返回
        if (CollUtil.isEmpty(sessions)) {
            log.info("[send][sessionId({}) userType({}) userId({}) messageType({}) messageContent({}) 未匹配到会话]",
                    sessionId, userType, userId, messageType, messageContent);
        }

        // 2. 执行消息发送
        doSend(sessions, messageType, messageContent);
    }

    /**
     * 发送消息的具体实现
     * <p>
     * 该方法将消息发送到指定的会话列表中。它会先进行一系列校验，确保会话有效，并且会话是打开状态。
     *
     * @param sessions       会话列表
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    public void doSend(Collection<WebSocketSession> sessions, String messageType, String messageContent) {
        // 创建消息对象
        JsonWebSocketMessage message = new JsonWebSocketMessage();
        message.setType(messageType);
        message.setContent(messageContent);

        // 将消息对象转换为 JSON 字符串
        String payload = JsonUtils.toJsonString(message); // 关键，使用 JSON 序列化

        // 遍历会话列表，发送消息
        sessions.forEach(session -> {
            // 1. 校验会话是否有效
            if (session == null) {
                log.error("[doSend][session 为空, message({})]", message);
                return;
            }
            if (!session.isOpen()) {
                log.error("[doSend][session({}) 已关闭, message({})]", session.getId(), message);
                return;
            }
            // 2. 发送消息
            try {
                session.sendMessage(new TextMessage(payload));
                log.info("[doSend][session({}) 发送消息成功，message({})]", session.getId(), message);
            } catch (IOException ex) {
                log.error("[doSend][session({}) 发送消息失败，message({})]", session.getId(), message, ex);
            }
        });
    }

}
