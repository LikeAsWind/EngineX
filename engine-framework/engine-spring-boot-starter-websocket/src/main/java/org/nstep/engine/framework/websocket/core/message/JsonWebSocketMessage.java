package org.nstep.engine.framework.websocket.core.message;

import lombok.Data;
import org.nstep.engine.framework.websocket.core.listener.WebSocketMessageListener;

import java.io.Serializable;

/**
 * JSON 格式的 WebSocket 消息帧。
 * <p>
 * 该类定义了 WebSocket 消息的标准格式，用于在前端和后端之间传输数据。
 * 消息包含两个主要字段：
 * 1. `type`：标识消息的类型，用于路由到相应的消息处理逻辑。
 * 2. `content`：消息的实际内容，通常是一个 JSON 字符串。
 */
@Data
public class JsonWebSocketMessage implements Serializable {

    /**
     * 消息类型。
     * <p>
     * 用途：
     * - 前端发送的消息需要指定 `type` 字段，后端根据该字段分发到对应的 {@link WebSocketMessageListener} 实现类。
     * - 例如：如果 `type` 为 "chat"，消息会被路由到 `ChatMessageListener`。
     * <p>
     * 示例值：
     * - "chat" 表示聊天消息。
     * - "notification" 表示通知消息。
     */
    private String type;

    /**
     * 消息内容。
     * <p>
     * - 内容必须是一个合法的 JSON 字符串，通常包含具体的消息数据。
     * - 后端会根据消息类型将 `content` 反序列化为对应的 Java 对象。
     * <p>
     * 示例值：
     * ```json
     * {
     * "sender": "user1",
     * "message": "Hello, World!"
     * }
     * ```
     */
    private String content;

}
