package org.nstep.engine.framework.websocket.core.sender.local;

import org.nstep.engine.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.sender.WebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.session.WebSocketSessionManager;

/**
 * 本地的 {@link WebSocketMessageSender} 实现类
 * <p>
 * 注意：仅仅适合单机场景！！！
 * <p>
 * 该类用于在单一服务器环境中发送 WebSocket 消息。
 */
public class LocalWebSocketMessageSender extends AbstractWebSocketMessageSender {

    /**
     * 构造函数，初始化 WebSocket 会话管理器。
     *
     * @param sessionManager 会话管理器
     */
    public LocalWebSocketMessageSender(WebSocketSessionManager sessionManager) {
        super(sessionManager);
    }

}
