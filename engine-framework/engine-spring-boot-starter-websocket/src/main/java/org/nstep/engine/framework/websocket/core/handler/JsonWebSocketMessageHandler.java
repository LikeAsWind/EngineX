package org.nstep.engine.framework.websocket.core.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.framework.tenant.core.util.TenantUtils;
import org.nstep.engine.framework.websocket.core.listener.WebSocketMessageListener;
import org.nstep.engine.framework.websocket.core.message.JsonWebSocketMessage;
import org.nstep.engine.framework.websocket.core.util.WebSocketFrameworkUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * JSON 格式的 {@link WebSocketHandler} 实现类。
 * <p>
 * 用于处理基于 JSON 格式的 WebSocket 消息。
 * 根据 {@link JsonWebSocketMessage#getType()} 消息类型，调度到对应的 {@link WebSocketMessageListener} 监听器。
 */
@Slf4j // 自动生成日志记录器
public class JsonWebSocketMessageHandler extends TextWebSocketHandler {

    /**
     * 消息类型与 {@link WebSocketMessageListener} 的映射关系。
     * <p>
     * 用于根据消息的 `type` 字段找到对应的监听器。
     */
    private final Map<String, WebSocketMessageListener<Object>> listeners = new HashMap<>();

    /**
     * 构造方法，初始化消息监听器映射。
     *
     * @param listenersList 所有实现了 {@link WebSocketMessageListener} 接口的监听器
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonWebSocketMessageHandler(List<? extends WebSocketMessageListener> listenersList) {
        listenersList.forEach((Consumer<WebSocketMessageListener>)
                listener -> listeners.put(listener.getType(), listener));
    }

    /**
     * 处理接收到的文本消息。
     *
     * @param session 当前 WebSocket 会话
     * @param message 接收到的文本消息
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 1.1 如果消息为空，直接跳过处理。
        if (message.getPayloadLength() == 0) {
            return;
        }

        // 1.2 如果是心跳 ping 消息，返回 pong 消息。
        if (message.getPayloadLength() == 4 && Objects.equals(message.getPayload(), "ping")) {
            session.sendMessage(new TextMessage("pong"));
            return;
        }

        // 2.1 解析 JSON 消息
        try {
            JsonWebSocketMessage jsonMessage = JsonUtils.parseObject(message.getPayload(), JsonWebSocketMessage.class);
            if (jsonMessage == null) {
                log.error("[handleTextMessage][session({}) message({}) 解析为空]", session.getId(), message.getPayload());
                return;
            }

            // 2.2 校验消息类型
            if (StrUtil.isEmpty(jsonMessage.getType())) {
                log.error("[handleTextMessage][session({}) message({}) 类型为空]", session.getId(), message.getPayload());
                return;
            }

            // 2.3 获取对应的监听器
            WebSocketMessageListener<Object> messageListener = listeners.get(jsonMessage.getType());
            if (messageListener == null) {
                log.error("[handleTextMessage][session({}) message({}) 监听器为空]", session.getId(), message.getPayload());
                return;
            }

            // 2.4 反序列化消息内容
            Type type = TypeUtil.getTypeArgument(messageListener.getClass(), 0);
            Object messageObj = JsonUtils.parseObject(jsonMessage.getContent(), type);

            // 2.5 获取租户信息并执行监听器逻辑
            Long tenantId = WebSocketFrameworkUtils.getTenantId(session);
            TenantUtils.execute(tenantId, () -> messageListener.onMessage(session, messageObj));
        } catch (Throwable ex) {
            log.error("[handleTextMessage][session({}) message({}) 处理异常]", session.getId(), message.getPayload(), ex);
        }
    }
}
