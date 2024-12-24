package org.nstep.engine.framework.websocket.core.sender;

import org.nstep.engine.framework.common.util.json.JsonUtils;

/**
 * WebSocket 消息的发送器接口
 * <p>
 * 该接口定义了发送 WebSocket 消息的方法。实现类需要提供具体的消息发送逻辑，支持通过不同的标识符（如用户类型、用户编号、会话 ID）来发送消息。
 */
public interface WebSocketMessageSender {

    /**
     * 发送消息给指定用户
     * <p>
     * 该方法根据用户类型和用户编号发送消息。实现类需要根据这些信息查找对应的 WebSocket 会话并发送消息。
     *
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    void send(Integer userType, Long userId, String messageType, String messageContent);

    /**
     * 发送消息给指定用户类型
     * <p>
     * 该方法根据用户类型发送消息。实现类需要根据用户类型查找对应的 WebSocket 会话并发送消息。
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    void send(Integer userType, String messageType, String messageContent);

    /**
     * 发送消息给指定 Session
     * <p>
     * 该方法根据会话 ID 发送消息。实现类需要根据会话 ID 查找对应的 WebSocket 会话并发送消息。
     *
     * @param sessionId      会话编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    void send(String sessionId, String messageType, String messageContent);

    /**
     * 发送消息给指定用户，并将消息内容序列化为 JSON 字符串
     * <p>
     * 该方法与 {@link #send(Integer, Long, String, String)} 类似，但消息内容是一个对象，会先被转换为 JSON 格式。
     *
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，作为对象传递，最终会被转换为 JSON 格式
     */
    default void sendObject(Integer userType, Long userId, String messageType, Object messageContent) {
        send(userType, userId, messageType, JsonUtils.toJsonString(messageContent));
    }

    /**
     * 发送消息给指定用户类型，并将消息内容序列化为 JSON 字符串
     * <p>
     * 该方法与 {@link #send(Integer, String, String)} 类似，但消息内容是一个对象，会先被转换为 JSON 格式。
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容，作为对象传递，最终会被转换为 JSON 格式
     */
    default void sendObject(Integer userType, String messageType, Object messageContent) {
        send(userType, messageType, JsonUtils.toJsonString(messageContent));
    }

    /**
     * 发送消息给指定 Session，并将消息内容序列化为 JSON 字符串
     * <p>
     * 该方法与 {@link #send(String, String, String)} 类似，但消息内容是一个对象，会先被转换为 JSON 格式。
     *
     * @param sessionId      会话编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，作为对象传递，最终会被转换为 JSON 格式
     */
    default void sendObject(String sessionId, String messageType, Object messageContent) {
        send(sessionId, messageType, JsonUtils.toJsonString(messageContent));
    }

}
