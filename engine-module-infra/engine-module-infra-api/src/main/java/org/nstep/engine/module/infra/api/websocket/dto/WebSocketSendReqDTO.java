package org.nstep.engine.module.infra.api.websocket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * RPC 服务 - WebSocket 消息发送 Request DTO
 * <p>
 * 该 DTO 用于表示 WebSocket 消息发送的请求数据结构，包括消息的基本信息，如 session 编号、用户信息、消息类型和内容。
 */
@Schema(description = "RPC 服务 - WebSocket 消息发送 Request DTO")
@Data
public class WebSocketSendReqDTO {

    /**
     * 会话编号，用于标识 WebSocket 连接的唯一会话。
     * 示例：abc
     */
    @Schema(description = "Session 编号", example = "abc")
    private String sessionId;

    /**
     * 用户编号，用于标识消息发送的用户。
     * 示例：1024
     */
    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    /**
     * 用户类型，用于标识用户的类型（例如：管理员、普通用户等）。
     * 示例：1
     */
    @Schema(description = "用户类型", example = "1")
    private Integer userType;

    /**
     * 消息类型，用于标识消息的种类（例如：通知消息、聊天消息等）。
     * 必须提供消息类型。
     * 示例：demo-message
     */
    @Schema(description = "消息类型", example = "demo-message")
    @NotEmpty(message = "消息类型不能为空")
    private String messageType;

    /**
     * 消息内容，包含消息的实际内容，通常为 JSON 格式字符串。
     * 必须提供消息内容。
     * 示例：{"name":"李四"}
     */
    @Schema(description = "消息内容", example = "{\"name\":\"李四\"}}")
    @NotEmpty(message = "消息内容不能为空")
    private String messageContent;

}
