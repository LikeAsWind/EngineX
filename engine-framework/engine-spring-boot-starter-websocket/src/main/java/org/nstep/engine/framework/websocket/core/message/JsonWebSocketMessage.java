package org.nstep.engine.framework.websocket.core.message;

import lombok.Data;
import org.nstep.engine.framework.websocket.core.listener.WebSocketMessageListener;

import java.io.Serializable;

/**
 * JSON 格式的 WebSocket 消息帧
 */
@Data
public class JsonWebSocketMessage implements Serializable {

    /**
     * 消息类型
     * <p>
     * 目的：用于分发到对应的 {@link WebSocketMessageListener} 实现类
     */
    private String type;
    /**
     * 消息内容
     * <p>
     * 要求 JSON 对象
     */
    private String content;

}
