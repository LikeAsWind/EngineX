package org.nstep.engine.gateway.util;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.gateway.filter.security.LoginUser;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 安全服务工具类，提供与安全相关的操作
 * <p>
 * 本类包含了与认证、用户信息管理等安全相关的工具方法。主要功能包括：
 * - 从请求中提取认证 Token。
 * - 设置和获取登录用户信息（如用户编号、用户类型）。
 * - 移除请求头中的用户信息。
 * - 将用户信息以 JSON 格式设置到请求头中。
 * <p>
 * 该类用于处理与用户认证和请求头管理相关的操作。
 */
@Slf4j
public class SecurityFrameworkUtils {

    // 常量：认证头名称
    private static final String AUTHORIZATION_HEADER = "Authorization";
    // 常量：认证头中的 Bearer 前缀
    private static final String AUTHORIZATION_BEARER = "Bearer";
    // 常量：登录用户的请求头名称
    private static final String LOGIN_USER_HEADER = "login-user";
    // 常量：登录用户 ID 属性名
    private static final String LOGIN_USER_ID_ATTR = "login-user-id";
    // 常量：登录用户类型属性名
    private static final String LOGIN_USER_TYPE_ATTR = "login-user-type";

    // 私有构造函数，防止实例化
    private SecurityFrameworkUtils() {
    }

    /**
     * 从请求中获得认证 Token
     * <p>
     * 从请求头中提取出 Bearer Token，若不存在或格式不正确，则返回 null。
     *
     * @param exchange 请求
     * @return 认证 Token，若未找到则返回 null
     */
    public static String obtainAuthorization(ServerWebExchange exchange) {
        String authorization = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        int index = authorization.indexOf(AUTHORIZATION_BEARER + " ");
        if (index == -1) { // 未找到 Bearer 前缀
            return null;
        }
        return authorization.substring(index + 7).trim(); // 提取 Token
    }

    /**
     * 设置登录用户信息到请求属性中
     *
     * @param exchange 请求
     * @param user     登录用户
     */
    public static void setLoginUser(ServerWebExchange exchange, LoginUser user) {
        exchange.getAttributes().put(LOGIN_USER_ID_ATTR, user.getId());
        exchange.getAttributes().put(LOGIN_USER_TYPE_ATTR, user.getUserType());
    }

    /**
     * 移除请求头中的登录用户信息
     *
     * @param exchange 请求
     * @return 移除用户信息后的请求
     */
    public static ServerWebExchange removeLoginUser(ServerWebExchange exchange) {
        // 如果请求头中没有 login-user，则直接返回原请求
        if (!exchange.getRequest().getHeaders().containsKey(LOGIN_USER_HEADER)) {
            return exchange;
        }
        // 如果存在，则移除该头
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(httpHeaders -> httpHeaders.remove(LOGIN_USER_HEADER)).build();
        return exchange.mutate().request(request).build();
    }

    /**
     * 获取登录用户的编号
     *
     * @param exchange 请求
     * @return 用户编号
     */
    public static Long getLoginUserId(ServerWebExchange exchange) {
        return MapUtil.getLong(exchange.getAttributes(), LOGIN_USER_ID_ATTR);
    }

    /**
     * 获取登录用户的类型
     *
     * @param exchange 请求
     * @return 用户类型
     */
    public static Integer getLoginUserType(ServerWebExchange exchange) {
        return MapUtil.getInt(exchange.getAttributes(), LOGIN_USER_TYPE_ATTR);
    }

    /**
     * 将登录用户信息设置到请求头中，以 JSON 格式存储
     *
     * @param builder 请求构建器
     * @param user    登录用户
     */
    public static void setLoginUserHeader(ServerHttpRequest.Builder builder, LoginUser user) {
        try {
            String userStr = JsonUtils.toJsonString(user);
            userStr = URLEncoder.encode(userStr, StandardCharsets.UTF_8); // 编码，避免中文乱码
            builder.header(LOGIN_USER_HEADER, userStr); // 设置请求头
        } catch (Exception ex) {
            log.error("[setLoginUserHeader][序列化 user({}) 发生异常]", user, ex);
            throw ex; // 异常抛出
        }
    }

}
