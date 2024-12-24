package org.nstep.engine.framework.websocket.core.security;

import lombok.RequiredArgsConstructor;
import org.nstep.engine.framework.security.config.AuthorizeRequestsCustomizer;
import org.nstep.engine.framework.websocket.config.WebSocketProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * WebSocket 的权限自定义配置类。
 * <p>
 * 该类继承自 {@link AuthorizeRequestsCustomizer}，用于自定义 WebSocket 相关的权限规则。
 * <p>
 * 功能说明：
 * - 配置 WebSocket 的连接路径（由 `webSocketProperties.getPath()` 指定）为允许所有访问。
 * - 通过这种方式，确保 WebSocket 握手请求不被其他安全配置拦截。
 */
@RequiredArgsConstructor
public class WebSocketAuthorizeRequestsCustomizer extends AuthorizeRequestsCustomizer {

    /**
     * WebSocket 配置项，提供连接路径等配置信息。
     */
    private final WebSocketProperties webSocketProperties;

    /**
     * 自定义权限配置。
     * <p>
     * 配置 WebSocket 连接路径的访问权限。
     *
     * @param registry 权限配置注册器，用于定义 URL 匹配规则及其权限要求。
     */
    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        // 配置 WebSocket 的连接路径允许所有访问
        registry.requestMatchers(webSocketProperties.getPath()).permitAll();
    }

}
