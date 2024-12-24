package org.nstep.engine.framework.websocket.core.sender.kafka;

import lombok.Data;

/**
 * Kafka 广播 WebSocket 的消息实体类。
 * <p>
 * 用于封装通过 Kafka 进行广播的 WebSocket 消息内容。
 * <p>
 * 功能说明：
 * - 包含发送消息的会话信息（如 Session 编号、用户类型、用户编号）。
 * - 包含消息的类型和具体内容。
 */
@Data
public class KafkaWebSocketMessage {

    /**
     * WebSocket Session 编号。
     * <p>
     * 用于标识消息对应的 WebSocket 会话。
     * 可用于指定消息发送到特定会话。
     */
    private String sessionId;

    /**
     * 用户类型。
     * <p>
     * 用于区分不同类型的用户（如管理员、普通用户）。
     * 具体值可以通过枚举或常量定义。
     */
    private Integer userType;

    /**
     * 用户编号。
     * <p>
     * 用于标识消息所属的用户。
     * 可用于单播消息的目标用户匹配。
     */
    private Long userId;

    /**
     * 消息类型。
     * <p>
     * 用于区分不同类型的消息（如通知、更新、警告）。
     * 可与 WebSocket 消息监听器中的 `getType()` 方法关联。
     */
    private String messageType;

    /**
     * 消息内容。
     * <p>
     * 以 JSON 格式存储消息的具体内容。
     * 可通过解析 JSON 内容获取实际的消息数据。
     */
    private String messageContent;

}
