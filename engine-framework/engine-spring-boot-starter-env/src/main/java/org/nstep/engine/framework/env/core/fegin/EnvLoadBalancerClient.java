package org.nstep.engine.framework.env.core.fegin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.balancer.NacosBalancer;
import com.alibaba.nacos.shaded.com.google.common.cache.Cache;
import com.alibaba.nacos.shaded.com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.env.core.context.EnvContextHolder;
import org.nstep.engine.framework.env.core.util.EnvUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 多环境的 {@link org.springframework.cloud.client.loadbalancer.LoadBalancerClient} 实现类。
 * <p>
 * 该类实现了基于环境标签（tag）的负载均衡策略。它在从服务实例列表选择时，优先选择与当前环境标签匹配的服务实例。
 * 如果没有匹配的服务实例，则回退到默认的负载均衡策略。
 */
@RequiredArgsConstructor
@Slf4j
public class EnvLoadBalancerClient implements ReactorServiceInstanceLoadBalancer {

    /**
     * 用于获取服务实例列表的供应商。该供应商提供了从注册中心获取服务实例的能力。
     */
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    /**
     * 需要获取的服务实例名。
     * <p>
     * 该字段目前仅用于日志打印，记录当前需要进行负载均衡的服务 ID。
     */
    private final String serviceId;

    /**
     * 被代理的 ReactiveLoadBalancer 对象。
     * <p>
     * 该对象提供了默认的负载均衡策略，在没有环境标签时将被使用。
     */
    private final ReactiveLoadBalancer<ServiceInstance> reactiveLoadBalancer;

    /**
     * 选择一个服务实例。
     * <p>
     * 根据当前环境标签（tag）选择服务实例。如果没有标签，则使用默认的负载均衡策略。
     *
     * @param request 请求信息，包含负载均衡所需的上下文信息
     * @return Mono<Response < ServiceInstance>> 包含选择的服务实例的响应
     */
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        // 情况一：没有标签时，使用默认的 reactiveLoadBalancer 实现负载均衡
        String tag = EnvContextHolder.getTag();
        if (StrUtil.isEmpty(tag)) {
            return Mono.from(reactiveLoadBalancer.choose(request));
        }

        // 情况二：有标签时，使用标签匹配服务实例
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next().map(list -> getInstanceResponse(list, tag));
    }

    /**
     * 根据服务实例列表和环境标签，选择合适的服务实例。
     * <p>
     * 如果服务实例列表为空，则返回空响应。如果有标签，则筛选匹配标签的服务实例；如果没有匹配的实例，则使用所有服务实例。
     *
     * @param instances 服务实例列表
     * @param tag       环境标签
     * @return 选择的服务实例响应
     */
    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, String tag) {
        // 如果服务实例为空，直接返回空响应
        if (CollUtil.isEmpty(instances)) {
            log.warn("[getInstanceResponse][serviceId({}) 服务实例列表为空]", serviceId);
            return new EmptyResponse();
        }

        // 获取当前实例的网段
        String currentNetworkSegment = getNetworkSegment(EnvUtils.getHostName());

        // 根据网段筛选服务实例
        List<ServiceInstance> sameNetworkInstances = filterInstancesByNetworkSegment(instances, currentNetworkSegment);

        // 如果有相同网段的实例，优先选择它们
        if (!sameNetworkInstances.isEmpty()) {
            log.warn("[getInstanceResponse][serviceId({}) 没有满足 tag({}) 的服务实例列表，直接使用所有服务实例列表]", serviceId, tag);
            return new DefaultResponse(NacosBalancer.getHostByRandomWeight3(sameNetworkInstances));
        }

        // 使用随机 + 权重的方式选择服务实例
        log.warn("[getInstanceResponse][serviceId({}) 没有相同网段的服务实例，使用所有实例]", serviceId);
        return new DefaultResponse(NacosBalancer.getHostByRandomWeight3(instances));
    }

    /**
     * 缓存计算过的网段，以减少重复计算。
     * 使用 Guava Cache 提供缓存功能，自动过期。
     */
    private static final Cache<String, String> networkSegmentCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)  // 设置缓存过期时间
            .build();


    private String getNetworkSegment(String ip) {
        try {
            return networkSegmentCache.get(ip, () -> calculateNetworkSegment(ip));
        } catch (ExecutionException e) {
            log.error("[getNetworkSegment] 缓存计算失败: {}", ip, e);
            return "default-network-segment";
        }
    }

    /**
     * 计算给定 IP 的网段。
     * 这里假设使用 /24 子网掩码，即前 24 位作为网络部分。
     *
     * @param ip IP 地址
     * @return 网段
     */
    private String calculateNetworkSegment(String ip) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            byte[] ipBytes = inetAddress.getAddress();
            ipBytes[3] = 0; // 将最后一个字节设置为 0，表示网段的起始地址
            InetAddress networkAddress = InetAddress.getByAddress(ipBytes);
            return networkAddress.getHostAddress();
        } catch (UnknownHostException e) {
            log.error("[calculateNetworkSegment] 获取 IP 地址的网段失败: {}, 返回默认网段", ip, e);
            return "default-network-segment";
        }
    }


    /**
     * 根据网段筛选服务实例。
     *
     * @param instances             服务实例列表
     * @param currentNetworkSegment 当前实例的网段
     * @return 匹配的服务实例列表
     */
    private List<ServiceInstance> filterInstancesByNetworkSegment(List<ServiceInstance> instances, String currentNetworkSegment) {
        // 如果实例列表较小，使用串行流；如果较大，使用并行流提高性能
        if (instances.size() > 100) {
            return instances.parallelStream().filter(instance ->
                            currentNetworkSegment.equals(getNetworkSegment(instance.getHost())))
                    .collect(Collectors.toList());
        } else {
            return instances.stream().filter(instance ->
                            currentNetworkSegment.equals(getNetworkSegment(instance.getHost())))
                    .collect(Collectors.toList());
        }
    }

}
