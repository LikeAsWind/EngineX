package org.nstep.engine.gateway.filter.grey;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * 支持灰度功能的 {@link ReactiveLoadBalancerClientFilter} 实现类
 * <p>
 * 由于 {@link ReactiveLoadBalancerClientFilter#choose(Request, String, Set)} 是 private 方法，无法进行重写。
 * 因此，这里只好 copy 它所有的代码，手动重写 choose 方法
 * <p>
 * 具体的使用与实现原理，可阅读如下两个文章：
 * 1. <a href=https://www.jianshu.com/p/6db15bc0be8f></a>
 * 2. <a href=https://cloud.tencent.com/developer/article/1620795</a>
 */
@Component
@AllArgsConstructor
@Slf4j
@SuppressWarnings({"JavadocReference", "rawtypes", "unchecked", "ConstantConditions"})
public class GrayReactiveLoadBalancerClientFilter implements GlobalFilter, Ordered {

    private final LoadBalancerClientFactory clientFactory;  // 用于获取负载均衡器客户端工厂
    private final GatewayLoadBalancerProperties properties;  // 网关负载均衡器的属性配置

    /**
     * 指定过滤器的执行顺序，确保它在其他过滤器之后执行
     *
     * @return 过滤器的执行顺序
     */
    @Override
    public int getOrder() {
        return ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }

    /**
     * 过滤器的核心方法，处理网关请求并选择合适的服务实例。
     *
     * @param exchange 当前请求的交换对象，包含了请求和响应的信息。
     * @param chain    过滤器链，用于调用下一个过滤器或执行后续的请求处理。
     * @return 返回一个 Mono<Void>，表示请求处理的异步结果。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求的 URI 和协议前缀
        // 这些值通常是由前面的过滤器设置的，用于确定请求的目标地址。
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);

        // 如果不是 grayLb 协议，则跳过此过滤器
        // 这里的逻辑是检查请求是否应该由这个过滤器处理。
        if (url == null || (!"grayLb".equals(url.getScheme()) && !"grayLb".equals(schemePrefix))) {
            return chain.filter(exchange);
        }

        // 保留原始请求 URL
        // 这个方法调用是为了保存原始请求的 URL，以便后续使用。
        addOriginalRequestUrl(exchange, url);

        // 日志记录
        // 如果日志级别设置为 trace，打印日志信息。
        if (log.isTraceEnabled()) {
            log.trace(ReactiveLoadBalancerClientFilter.class.getSimpleName() + " url before: " + url);
        }

        // 获取服务 ID 和生命周期处理器
        // 从请求中获取服务 ID，并获取支持的生命周期处理器。
        URI requestUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String serviceId = requestUri.getHost();
        Set<LoadBalancerLifecycle> supportedLifecycleProcessors = LoadBalancerLifecycleValidator
                .getSupportedLifecycleProcessors(clientFactory.getInstances(serviceId, LoadBalancerLifecycle.class),
                        RequestDataContext.class, ResponseData.class, ServiceInstance.class);

        // 创建请求上下文
        // 使用服务 ID 和请求数据创建一个负载均衡请求上下文。
        DefaultRequest<RequestDataContext> lbRequest = new DefaultRequest<>(new RequestDataContext(new RequestData(exchange.getRequest()), getHint(serviceId)));

        // 调用灰度负载均衡器进行选择
        // 使用请求上下文和服务 ID 调用负载均衡器，选择一个服务实例。
        return choose(lbRequest, serviceId, supportedLifecycleProcessors).doOnNext(response -> {
                    // 如果没有找到服务实例，抛出异常
                    if (!response.hasServer()) {
                        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                                .onComplete(new CompletionContext<>(CompletionContext.Status.DISCARD, lbRequest, response)));
                        throw NotFoundException.create(properties.isUse404(), "Unable to find instance for " + url.getHost());
                    }

                    // 获取选择的服务实例
                    ServiceInstance retrievedInstance = response.getServer();
                    URI uri = exchange.getRequest().getURI();

                    // 如果使用了 lb 协议前缀，使用该协议作为默认协议
                    String overrideScheme = retrievedInstance.isSecure() ? "https" : "http";
                    if (schemePrefix != null) {
                        overrideScheme = url.getScheme();
                    }

                    // 创建委托的服务实例对象
                    // 使用选择的服务实例和协议创建一个新的服务实例对象。
                    DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(retrievedInstance, overrideScheme);

                    // 重构 URI，更新请求 URL
                    // 根据选择的服务实例和原始请求 URI 构建新的请求 URI。
                    URI requestUrl = reconstructURI(serviceInstance, uri);

                    // 日志记录
                    if (log.isTraceEnabled()) {
                        log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
                    }
                    exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
                    exchange.getAttributes().put(GATEWAY_LOADBALANCER_RESPONSE_ATTR, response);

                    // 启动生命周期处理器
                    // 通知所有支持的生命周期处理器请求已经开始。
                    supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStartRequest(lbRequest, response));
                }).then(chain.filter(exchange))
                .doOnError(throwable -> supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                        .onComplete(new CompletionContext<ResponseData, ServiceInstance, RequestDataContext>(CompletionContext.Status.FAILED, throwable, lbRequest,
                                exchange.getAttribute(GATEWAY_LOADBALANCER_RESPONSE_ATTR)))))
                .doOnSuccess(aVoid -> supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                        .onComplete(new CompletionContext<ResponseData, ServiceInstance, RequestDataContext>(CompletionContext.Status.SUCCESS, lbRequest,
                                exchange.getAttribute(GATEWAY_LOADBALANCER_RESPONSE_ATTR),
                                new ResponseData(exchange.getResponse(), new RequestData(exchange.getRequest()))))));
    }

    /**
     * 重构 URI，将服务实例的信息合并到原始 URI 中
     *
     * @param serviceInstance 选择的服务实例
     * @param original        原始请求 URI
     * @return 返回重构后的 URI
     */
    protected URI reconstructURI(ServiceInstance serviceInstance, URI original) {
        return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
    }

    /**
     * 根据请求和服务 ID 选择一个服务实例
     *
     * @param lbRequest                    请求上下文
     * @param serviceId                    服务 ID
     * @param supportedLifecycleProcessors 支持的生命周期处理器
     * @return 返回选择的服务实例响应
     */
    private Mono<Response<ServiceInstance>> choose(Request<RequestDataContext> lbRequest, String serviceId,
                                                   Set<LoadBalancerLifecycle> supportedLifecycleProcessors) {
        // 创建一个新的灰度负载均衡器对象
        GrayLoadBalancer loadBalancer = new GrayLoadBalancer(
                clientFactory.getLazyProvider(serviceId, ServiceInstanceListSupplier.class), serviceId);

        // 启动生命周期处理器
        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStart(lbRequest));

        // 调用灰度负载均衡器进行选择
        return loadBalancer.choose(lbRequest);
    }

    /**
     * 获取负载均衡器的提示信息
     *
     * @param serviceId 服务 ID
     * @return 返回提示信息
     */
    private String getHint(String serviceId) {
        LoadBalancerProperties loadBalancerProperties = clientFactory.getProperties(serviceId);
        Map<String, String> hints = loadBalancerProperties.getHint();
        String defaultHint = hints.getOrDefault("default", "default");
        String hintPropertyValue = hints.get(serviceId);
        return hintPropertyValue != null ? hintPropertyValue : defaultHint;
    }
}
