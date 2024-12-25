package org.nstep.engine.gateway.filter.cors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 跨域 Filter
 * <p>
 * 该过滤器用于处理跨域请求，向响应头添加 CORS 相关的头信息。
 */
@Component
public class CorsFilter implements WebFilter {

    // 允许所有来源
    private static final String ALL = "*";
    // 设置最大缓存时间为 3600 秒
    private static final String MAX_AGE = "3600L";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 获取当前请求
        ServerHttpRequest request = exchange.getRequest();

        // 如果不是跨域请求，直接放行
        if (!CorsUtils.isCorsRequest(request)) {
            return chain.filter(exchange);
        }

        // 获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();

        // 设置跨域响应头
        headers.add("Access-Control-Allow-Origin", ALL);  // 允许所有源
        headers.add("Access-Control-Allow-Methods", ALL);  // 允许所有方法
        headers.add("Access-Control-Allow-Headers", ALL);  // 允许所有请求头
        headers.add("Access-Control-Max-Age", MAX_AGE);  // 设置预检请求的缓存时间为 3600 秒

        // 如果是预检请求 (OPTIONS)，直接返回 200 OK
        if (request.getMethod() == HttpMethod.OPTIONS) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }

        // 继续处理请求
        return chain.filter(exchange);
    }
}

