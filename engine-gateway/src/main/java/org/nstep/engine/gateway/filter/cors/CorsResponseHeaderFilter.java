package org.nstep.engine.gateway.filter.cors;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

/**
 * 解决 Spring Cloud Gateway 2.x 跨域时，出现重复 Origin 的 BUG
 * <p>
 * 参考文档：<a href="https://blog.csdn.net/zimou5581/article/details/90043178" />
 */
@Component
public class CorsResponseHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        // 指定此过滤器位于 NettyWriteResponseFilter 之后
        // 即待处理完响应体后接着处理响应头
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.defer(() -> {
            // 遍历响应头，检查重复的 CORS 头部
            exchange.getResponse().getHeaders().entrySet().stream()
                    // 过滤掉值为 null 或没有多个值的情况
                    .filter(kv -> (kv.getValue() != null && kv.getValue().size() > 1))
                    // 只关注 CORS 相关的头部
                    .filter(kv -> (kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
                            || kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)))
                    // 保留第一个值，去掉重复的值
                    .forEach(kv -> kv.setValue(new ArrayList<>() {{
                        add(kv.getValue().get(0));
                    }}));
            return chain.filter(exchange);
        }));
    }
}
