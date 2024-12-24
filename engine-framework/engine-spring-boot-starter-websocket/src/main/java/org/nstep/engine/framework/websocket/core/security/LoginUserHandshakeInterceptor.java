package org.nstep.engine.framework.websocket.core.security;

import org.nstep.engine.framework.security.core.LoginUser;
import org.nstep.engine.framework.security.core.filter.TokenAuthenticationFilter;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.framework.websocket.core.util.WebSocketFrameworkUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 登录用户的 {@link HandshakeInterceptor} 实现类。
 * <p>
 * 该拦截器的主要作用是将登录用户信息注入到 WebSocket 会话中，以便后续业务逻辑中可以方便地获取当前登录用户。
 * <p>
 * 流程说明：
 * 1. 前端在连接 WebSocket 时，通过拼接 `?token={token}` 到 `ws://` 连接 URL 中，携带用户的身份认证信息。
 * 2. 后端的 {@link TokenAuthenticationFilter} 负责解析和验证该 token，并将认证结果存储在上下文中。
 * 3. 本拦截器在握手阶段执行，通过 {@link SecurityFrameworkUtils#getLoginUser()} 获取当前登录用户，并将其存储到 WebSocket 会话的属性中。
 * 4. 后续的 WebSocket 处理逻辑可以通过 {@link WebSocketFrameworkUtils#getLoginUser(WebSocketSession)} 获取登录用户信息。
 */
public class LoginUserHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * 在握手前执行。
     * <p>
     * 如果当前请求已经通过身份认证，则将登录用户信息注入到 WebSocket 会话属性中。
     *
     * @param request    当前的 HTTP 请求
     * @param response   当前的 HTTP 响应
     * @param wsHandler  WebSocket 处理器
     * @param attributes WebSocket 会话的属性
     * @return 是否允许握手继续进行。如果返回 false，则握手失败。
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 从安全框架工具中获取当前登录用户
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null) {
            // 将登录用户信息存入 WebSocket 会话的属性中
            WebSocketFrameworkUtils.setLoginUser(loginUser, attributes);
        }
        return true; // 返回 true 表示允许握手继续
    }

    /**
     * 在握手完成后执行。
     * <p>
     * 当前实现未做任何操作。
     *
     * @param request   当前的 HTTP 请求
     * @param response  当前的 HTTP 响应
     * @param wsHandler WebSocket 处理器
     * @param exception 握手过程中发生的异常（如果有）
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 当前实现不需要处理握手后的逻辑
    }

}
