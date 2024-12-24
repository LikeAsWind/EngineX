package org.nstep.engine.framework.websocket.core.listener;

import org.nstep.engine.framework.websocket.core.message.JsonWebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket 消息监听器接口。
 * <p>
 * 用于处理来自前端的 WebSocket 消息，根据消息的 {@link #getType()} 类型匹配对应的监听器，并执行消息处理逻辑。
 *
 * @param <T> 泛型，表示消息的具体类型。
 */
public interface WebSocketMessageListener<T> {

    /**
     * 处理收到的 WebSocket 消息。
     *
     * @param session 当前的 WebSocket 会话，表示消息来源的连接。
     * @param message 消息内容，类型为 T。
     *                - 该对象通常通过 JSON 反序列化得到，具体类型由实现类定义。
     */
    void onMessage(WebSocketSession session, T message);

    /**
     * 获取该监听器支持的消息类型。
     * <p>
     * 前端发送的消息应包含一个 `type` 字段，通过该字段与返回的消息类型进行匹配。
     * 例如：
     * ```json
     * {
     * "type": "chat",
     * "content": "{\"sender\":\"user1\",\"message\":\"Hello!\"}"
     * }
     * ```
     *
     * @return 消息类型的标识符，通常为字符串。
     * @see JsonWebSocketMessage#getType()
     */
    String getType();
}
