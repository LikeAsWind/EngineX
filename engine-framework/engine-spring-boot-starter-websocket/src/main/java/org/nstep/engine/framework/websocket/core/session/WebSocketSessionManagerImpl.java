package org.nstep.engine.framework.websocket.core.session;

import cn.hutool.core.collection.CollUtil;
import org.nstep.engine.framework.security.core.LoginUser;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;
import org.nstep.engine.framework.websocket.core.util.WebSocketFrameworkUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 默认的 {@link WebSocketSessionManager} 实现类
 * <p>
 * 该类提供了 WebSocket 会话的管理功能，包括添加、移除和获取会话的操作。它通过两个主要的映射来管理会话：
 * 1. `idSessions`：根据会话 ID 映射 WebSocket 会话。
 * 2. `userSessions`：根据用户类型和用户编号映射 WebSocket 会话。
 */
public class WebSocketSessionManagerImpl implements WebSocketSessionManager {

    /**
     * id 与 WebSocketSession 映射
     * <p>
     * key：Session 编号
     * value：WebSocketSession 实例
     */
    private final ConcurrentMap<String, WebSocketSession> idSessions = new ConcurrentHashMap<>();

    /**
     * user 与 WebSocketSession 映射
     * <p>
     * key1：用户类型
     * key2：用户编号
     * value：与该用户类型和用户编号相关联的 WebSocket 会话列表
     */
    private final ConcurrentMap<Integer, ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>>> userSessions
            = new ConcurrentHashMap<>();

    @Override
    public void addSession(WebSocketSession session) {
        // 将会话添加到 idSessions 中，使用会话 ID 作为键
        idSessions.put(session.getId(), session);

        // 获取当前会话的登录用户信息
        LoginUser user = WebSocketFrameworkUtils.getLoginUser(session);
        if (user == null) {
            return; // 如果没有用户信息，返回
        }

        // 获取用户类型对应的会话映射
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(user.getUserType());
        if (userSessionsMap == null) {
            userSessionsMap = new ConcurrentHashMap<>();
            // 如果用户类型映射不存在，创建并加入
            if (userSessions.putIfAbsent(user.getUserType(), userSessionsMap) != null) {
                userSessionsMap = userSessions.get(user.getUserType());
            }
        }

        // 获取指定用户 ID 对应的会话列表
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(user.getId());
        if (sessions == null) {
            sessions = new CopyOnWriteArrayList<>();
            // 如果该用户的会话列表不存在，创建并加入
            if (userSessionsMap.putIfAbsent(user.getId(), sessions) != null) {
                sessions = userSessionsMap.get(user.getId());
            }
        }

        // 将当前会话添加到该用户的会话列表中
        sessions.add(session);
    }

    @Override
    public void removeSession(WebSocketSession session) {
        // 从 idSessions 中移除会话
        idSessions.remove(session.getId());

        // 获取当前会话的登录用户信息
        LoginUser user = WebSocketFrameworkUtils.getLoginUser(session);
        if (user == null) {
            return; // 如果没有用户信息，返回
        }

        // 获取用户类型对应的会话映射
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(user.getUserType());
        if (userSessionsMap == null) {
            return; // 如果用户类型的会话映射不存在，返回
        }

        // 获取指定用户 ID 对应的会话列表
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(user.getId());
        // 移除当前会话
        sessions.removeIf(session0 -> session0.getId().equals(session.getId()));

        // 如果该用户的会话列表为空，移除该用户的会话映射
        if (CollUtil.isEmpty(sessions)) {
            userSessionsMap.remove(user.getId(), sessions);
        }
    }

    @Override
    public WebSocketSession getSession(String id) {
        // 根据会话 ID 获取会话
        return idSessions.get(id);
    }

    @Override
    public Collection<WebSocketSession> getSessionList(Integer userType) {
        // 获取用户类型对应的会话映射
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType);
        if (CollUtil.isEmpty(userSessionsMap)) {
            return new ArrayList<>(); // 如果没有会话，返回空列表
        }

        // 存储最终会话列表
        LinkedList<WebSocketSession> result = new LinkedList<>();
        Long contextTenantId = TenantContextHolder.getTenantId();

        // 遍历用户类型的所有会话
        for (List<WebSocketSession> sessions : userSessionsMap.values()) {
            if (CollUtil.isEmpty(sessions)) {
                continue;
            }

            // 如果租户 ID 不匹配，跳过该会话
            if (contextTenantId != null) {
                Long userTenantId = WebSocketFrameworkUtils.getTenantId(sessions.get(0));
                if (!contextTenantId.equals(userTenantId)) {
                    continue;
                }
            }

            // 将符合条件的会话添加到结果列表中
            result.addAll(sessions);
        }
        return result;
    }

    @Override
    public Collection<WebSocketSession> getSessionList(Integer userType, Long userId) {
        // 获取用户类型对应的会话映射
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType);
        if (CollUtil.isEmpty(userSessionsMap)) {
            return new ArrayList<>(); // 如果没有会话，返回空列表
        }

        // 获取指定用户 ID 对应的会话列表
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(userId);
        return CollUtil.isNotEmpty(sessions) ? new ArrayList<>(sessions) : new ArrayList<>();
    }

}
