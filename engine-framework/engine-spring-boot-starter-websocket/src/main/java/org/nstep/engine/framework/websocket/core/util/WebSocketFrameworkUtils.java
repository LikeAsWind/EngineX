package org.nstep.engine.framework.websocket.core.util;

import org.nstep.engine.framework.security.core.LoginUser;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

/**
 * 专属于 web 包的工具类
 * <p>
 * 该类提供了与 WebSocket 会话相关的用户信息管理功能，主要包括设置和获取当前登录用户的信息。
 */
public class WebSocketFrameworkUtils {

    // 常量：用于在 WebSocket 会话中存储登录用户信息的属性名称
    public static final String ATTRIBUTE_LOGIN_USER = "LOGIN_USER";

    /**
     * 设置当前用户
     *
     * @param loginUser  登录用户
     * @param attributes Session 的属性
     */
    public static void setLoginUser(LoginUser loginUser, Map<String, Object> attributes) {
        // 将登录用户信息存储到会话属性中
        attributes.put(ATTRIBUTE_LOGIN_USER, loginUser);
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    public static LoginUser getLoginUser(WebSocketSession session) {
        // 从 WebSocket 会话的属性中获取登录用户
        return (LoginUser) session.getAttributes().get(ATTRIBUTE_LOGIN_USER);
    }

    /**
     * 获得当前用户的编号
     *
     * @return 用户编号
     */
    public static Long getLoginUserId(WebSocketSession session) {
        // 获取当前用户，并返回其用户编号
        LoginUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getId() : null;
    }

    /**
     * 获得当前用户的类型
     *
     * @return 用户类型
     */
    public static Integer getLoginUserType(WebSocketSession session) {
        // 获取当前用户，并返回其用户类型
        LoginUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getUserType() : null;
    }

    /**
     * 获得当前用户的租户编号
     *
     * @param session WebSocket 会话
     * @return 租户编号
     */
    public static Long getTenantId(WebSocketSession session) {
        // 获取当前用户，并返回其租户编号
        LoginUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getTenantId() : null;
    }
}
