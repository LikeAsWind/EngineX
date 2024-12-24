package org.nstep.engine.framework.websocket.core.session;

import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;

/**
 * {@link WebSocketSession} 管理器的接口
 * <p>
 * 该接口定义了 WebSocket 会话管理的基本操作，包括添加、移除、获取指定会话以及获取特定用户类型或用户编号的会话列表。
 */
public interface WebSocketSessionManager {

    /**
     * 添加 Session
     * <p>
     * 将一个 WebSocket 会话添加到管理器中，以便进行管理和后续的消息发送等操作。
     *
     * @param session 要添加的 WebSocket 会话
     */
    void addSession(WebSocketSession session);

    /**
     * 移除 Session
     * <p>
     * 从管理器中移除一个 WebSocket 会话，通常在会话关闭时调用。
     *
     * @param session 要移除的 WebSocket 会话
     */
    void removeSession(WebSocketSession session);

    /**
     * 获得指定编号的 Session
     * <p>
     * 根据会话编号获取 WebSocket 会话。此方法用于根据会话的唯一标识符获取对应的会话。
     *
     * @param id 会话的编号
     * @return 对应的 WebSocket 会话
     */
    WebSocketSession getSession(String id);

    /**
     * 获得指定用户类型的 Session 列表
     * <p>
     * 根据用户类型获取所有与该用户类型相关联的 WebSocket 会话。用户类型可以是例如“管理员”、“普通用户”等。
     *
     * @param userType 用户类型
     * @return 与该用户类型相关的 WebSocket 会话列表
     */
    Collection<WebSocketSession> getSessionList(Integer userType);

    /**
     * 获得指定用户编号的 Session 列表
     * <p>
     * 根据用户类型和用户编号获取特定用户的所有 WebSocket 会话。适用于用户有多个设备或多个会话时的情况。
     *
     * @param userType 用户类型
     * @param userId   用户编号
     * @return 与该用户类型和用户编号相关的 WebSocket 会话列表
     */
    Collection<WebSocketSession> getSessionList(Integer userType, Long userId);

}
