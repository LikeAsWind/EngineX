package org.nstep.engine.gateway.util;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Web 工具类，提供与 Web 框架相关的常用操作
 * <p>
 * 本类包含了与 HTTP 请求和响应相关的工具方法。主要功能包括：
 * - 设置和获取租户信息的请求头。
 * - 写入 JSON 格式的响应。
 * - 获取客户端 IP 地址。
 * - 获取请求匹配的路由信息。
 * <p>
 * 该类用于简化 Web 请求和响应的处理，提升开发效率。
 */
@Slf4j
public class WebFrameworkUtils {

    // 常量：租户 ID 请求头名称
    private static final String HEADER_TENANT_ID = "tenant-id";

    // 私有构造函数，防止实例化
    private WebFrameworkUtils() {
    }

    /**
     * 将 Gateway 请求中的 header 设置到 WebClient 请求的 HttpHeaders 中
     *
     * @param tenantId    租户编号
     * @param httpHeaders WebClient 的请求头
     */
    public static void setTenantIdHeader(Long tenantId, HttpHeaders httpHeaders) {
        if (tenantId == null) {
            return;
        }
        httpHeaders.set(HEADER_TENANT_ID, String.valueOf(tenantId));
    }

    /**
     * 从请求中获取租户 ID
     *
     * @param exchange 请求
     * @return 租户 ID，若不存在则返回 null
     */
    public static Long getTenantId(ServerWebExchange exchange) {
        String tenantId = exchange.getRequest().getHeaders().getFirst(HEADER_TENANT_ID);
        return NumberUtil.isNumber(tenantId) ? Long.valueOf(tenantId) : null;
    }

    /**
     * 将对象转换为 JSON 字符串并写入响应
     * <p>
     * 设置响应头的 Content-Type 为 application/json，并将对象序列化为 JSON 写入响应体。
     *
     * @param exchange 响应
     * @param object   对象，会序列化成 JSON 字符串
     * @return Mono<Void>，表示异步写入响应体
     */
    @SuppressWarnings("deprecation") // 必须使用 APPLICATION_JSON_UTF8_VALUE，否则会乱码
    public static Mono<Void> writeJSON(ServerWebExchange exchange, Object object) {
        // 设置 header
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        // 设置 body
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                return bufferFactory.wrap(JsonUtils.toJsonByte(object)); // 序列化对象并写入响应
            } catch (Exception ex) {
                ServerHttpRequest request = exchange.getRequest();
                log.error("[writeJSON][uri({}/{}) 发生异常]", request.getURI(), request.getMethod(), ex);
                return bufferFactory.wrap(new byte[0]); // 异常时返回空响应体
            }
        }));
    }

    /**
     * 获取客户端的 IP 地址
     * <p>
     * 通过多个 HTTP 头（如 X-Forwarded-For、X-Real-IP 等）尝试获取客户端 IP，
     * 若未找到则通过请求的 remoteAddress 获取客户端 IP。
     *
     * @param exchange         请求
     * @param otherHeaderNames 其它自定义的 header 名称
     * @return 客户端 IP 地址
     */
    public static String getClientIP(ServerWebExchange exchange, String... otherHeaderNames) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        if (ArrayUtil.isNotEmpty(otherHeaderNames)) {
            headers = ArrayUtil.addAll(headers, otherHeaderNames); // 添加自定义的 header 名称
        }
        // 方式一，通过 header 获取
        String ip;
        for (String header : headers) {
            ip = exchange.getRequest().getHeaders().getFirst(header);
            if (!NetUtil.isUnknown(ip)) {
                return NetUtil.getMultistageReverseProxyIp(ip); // 获取多级反向代理的真实 IP
            }
        }

        // 方式二，通过 remoteAddress 获取
        if (exchange.getRequest().getRemoteAddress() == null) {
            return null;
        }
        ip = exchange.getRequest().getRemoteAddress().getHostString();
        return NetUtil.getMultistageReverseProxyIp(ip); // 获取多级反向代理的真实 IP
    }

    /**
     * 获取请求匹配的路由信息
     *
     * @param exchange 请求
     * @return 路由信息
     */
    public static Route getGatewayRoute(ServerWebExchange exchange) {
        return exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
    }

}
