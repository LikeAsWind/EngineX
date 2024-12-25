package org.nstep.engine.gateway.filter.grey;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.balancer.NacosBalancer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.util.collection.CollectionUtils;
import org.nstep.engine.gateway.util.EnvUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 灰度 {@link GrayLoadBalancer} 实现类
 * <p>
 * 根据请求的 header[version] 匹配，筛选满足 metadata[version] 相等的服务实例列表，然后随机 + 权重进行选择一个
 * 1. 假如请求的 header[version] 为空，则不进行筛选，所有服务实例都进行选择
 * 2. 如果 metadata[version] 都不相等，则不进行筛选，所有服务实例都进行选择
 * <p>
 * 注意，考虑到实现的简易，它的权重是使用 Nacos 的 nacos.weight，所以随机 + 权重也是基于 {@link NacosBalancer} 筛选。
 * 也就是说，如果你不使用 Nacos 作为注册中心，需要微调一下筛选的实现逻辑
 */
@RequiredArgsConstructor
@Slf4j
public class GrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final String VERSION = "version"; // 定义请求头中用于版本控制的字段

    /**
     * 用于获取 serviceId 对应的服务实例的列表
     */
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    /**
     * 需要获取的服务实例名
     * <p>
     * 暂时用于打印 logger 日志
     */
    private final String serviceId;

    /**
     * 根据请求选择一个服务实例
     *
     * @param request 请求对象
     * @return 返回选中的服务实例
     */
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        // 从请求中获取 headers 属性，实现从 header 中获取 version
        HttpHeaders headers = ((RequestDataContext) request.getContext()).getClientRequest().getHeaders();

        // 获取服务实例列表供应商
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);

        // 获取实例列表并根据 version 过滤
        return supplier.get(request).next().map(list -> getInstanceResponse(list, headers));
    }

    /**
     * 根据实例列表和请求头中的 version，筛选出满足条件的实例，并随机 + 权重选择一个实例
     *
     * @param instances 服务实例列表
     * @param headers   请求头
     * @return 返回选中的服务实例
     */
    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, HttpHeaders headers) {
        // 如果服务实例列表为空，直接返回空响应
        if (CollUtil.isEmpty(instances)) {
            log.warn("[getInstanceResponse][serviceId({}) 服务实例列表为空]", serviceId);
            return new EmptyResponse();
        }

        // 从请求头获取 version 字段
        String version = headers.getFirst(VERSION);
        List<ServiceInstance> chooseInstances;

        // 如果 version 为空，则不进行版本筛选，所有实例都可选择
        if (StrUtil.isEmpty(version)) {
            chooseInstances = instances;
        } else {
            // 根据 version 筛选出符合要求的实例
            chooseInstances = CollectionUtils.filterList(instances, instance -> version.equals(instance.getMetadata().get("version")));
            // 如果没有符合版本的实例，则使用所有实例
            if (CollUtil.isEmpty(chooseInstances)) {
                log.warn("[getInstanceResponse][serviceId({}) 没有满足版本({})的服务实例列表，直接使用所有服务实例列表]", serviceId, version);
                chooseInstances = instances;
            }
        }

        // 基于 tag 过滤实例列表
        chooseInstances = filterTagServiceInstances(chooseInstances, headers);

        // 使用 Nacos 提供的随机 + 权重选择实例
        return new DefaultResponse(NacosBalancer.getHostByRandomWeight3(chooseInstances));
    }

    /**
     * 基于 tag 请求头，过滤匹配 tag 的服务实例列表
     * <p>
     * 如果请求头中没有 tag，则返回所有实例。
     * 如果有 tag，则根据 tag 过滤实例列表。
     *
     * @param instances 服务实例列表
     * @param headers   请求头
     * @return 返回符合 tag 条件的服务实例列表
     */
    private List<ServiceInstance> filterTagServiceInstances(List<ServiceInstance> instances, HttpHeaders headers) {
        // 获取请求头中的 tag 字段
        String tag = EnvUtils.getTag(headers);

        // 如果没有 tag，直接返回所有实例
        if (StrUtil.isEmpty(tag)) {
            return instances;
        }

        // 根据 tag 筛选出符合要求的实例
        List<ServiceInstance> chooseInstances = CollectionUtils.filterList(instances, instance -> tag.equals(EnvUtils.getTag(instance)));

        // 如果没有符合 tag 的实例，返回所有实例
        if (CollUtil.isEmpty(chooseInstances)) {
            log.warn("[filterTagServiceInstances][serviceId({}) 没有满足 tag({}) 的服务实例列表，直接使用所有服务实例列表]", serviceId, tag);
            chooseInstances = instances;
        }

        return chooseInstances;
    }
}
