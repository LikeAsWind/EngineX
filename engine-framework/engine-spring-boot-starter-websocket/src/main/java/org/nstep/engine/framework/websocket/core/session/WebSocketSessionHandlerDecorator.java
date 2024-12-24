package org.nstep.engine.framework.websocket.core.session;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

/**
 * {@link WebSocketHandler} 的装饰类，实现了以下功能：
 * <p>
 * 1. {@link WebSocketSession} 连接或关闭时，使用 {@link #sessionManager} 进行管理
 * 2. 封装 {@link WebSocketSession} 支持并发操作
 * <p>
 * 该类是 {@link WebSocketHandler} 的装饰类，负责在 WebSocket 连接建立和关闭时管理会话，并为每个会话提供并发操作的支持。
 */
public class WebSocketSessionHandlerDecorator extends WebSocketHandlerDecorator {

    /**
     * 发送时间的限制，单位：毫秒
     * <p>
     * 限制每个 WebSocket 会话发送消息的最大时间。超过该时间的消息会被限制或丢弃。
     */
    private static final Integer SEND_TIME_LIMIT = 1000 * 5;

    /**
     * 发送消息缓冲上线，单位：bytes
     * <p>
     * 限制每个 WebSocket 会话发送消息的最大缓冲大小。超过该大小的消息会被限制或丢弃。
     */
    private static final Integer BUFFER_SIZE_LIMIT = 1024 * 100;

    private final WebSocketSessionManager sessionManager;

    /**
     * 构造方法，初始化装饰类
     *
     * @param delegate       被装饰的 WebSocketHandler 实现
     * @param sessionManager WebSocket 会话管理器
     */
    public WebSocketSessionHandlerDecorator(WebSocketHandler delegate,
                                            WebSocketSessionManager sessionManager) {
        super(delegate);
        this.sessionManager = sessionManager;
    }

    /**
     * WebSocket 连接建立后调用，添加会话到 {@link WebSocketSessionManager}
     * <p>
     * 在连接建立时，使用 {@link ConcurrentWebSocketSessionDecorator} 对会话进行封装，支持并发操作，
     * 并将该会话添加到 {@link WebSocketSessionManager} 中进行管理。
     *
     * @param session 建立的 WebSocket 会话
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 实现 session 支持并发，可参考 https://blog.csdn.net/abu935009066/article/details/131218149
        // 使用 ConcurrentWebSocketSessionDecorator 装饰 WebSocketSession，设置发送时间限制和缓冲大小限制
        session = new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT);
        // 将装饰后的会话添加到 WebSocketSessionManager 中进行管理
        sessionManager.addSession(session);
    }

    /**
     * WebSocket 连接关闭时调用，从 {@link WebSocketSessionManager} 中移除会话
     * <p>
     * 在连接关闭时，移除会话，确保会话管理器中的会话列表保持最新。
     *
     * @param session     关闭的 WebSocket 会话
     * @param closeStatus 关闭状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        // 从 WebSocketSessionManager 中移除会话
        sessionManager.removeSession(session);
    }

}
