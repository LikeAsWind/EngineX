package org.nstep.engine.framework.env.core.fegin;


import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;

/**
 * 多环境的 {@link LoadBalancerClientFactory} 实现类
 * 目的：在创建 {@link ReactiveLoadBalancer} 时，会额外增加 {@link EnvLoadBalancerClient} 代理，
 * 用于 tag 过滤服务实例，确保根据环境标签选择合适的服务实例。
 */
public class EnvLoadBalancerClientFactory extends LoadBalancerClientFactory {

    /**
     * 构造函数，初始化父类 {@link LoadBalancerClientFactory}，并传入配置属性。
     *
     * @param properties LoadBalancerClientsProperties 配置对象，包含负载均衡客户端的相关配置
     */
    public EnvLoadBalancerClientFactory(LoadBalancerClientsProperties properties) {
        super(properties);  // 调用父类构造函数，初始化配置
    }

    /**
     * 获取服务实例的负载均衡器 {@link ReactiveLoadBalancer}，并为其添加 {@link EnvLoadBalancerClient} 代理，
     * 该代理将根据环境标签（tag）过滤服务实例。
     *
     * @param serviceId 服务的 ID
     * @return ReactiveLoadBalancer<ServiceInstance> 返回一个负载均衡器实例，包装了 {@link EnvLoadBalancerClient}
     */
    @Override
    public ReactiveLoadBalancer<ServiceInstance> getInstance(String serviceId) {
        // 获取父类提供的默认 ReactiveLoadBalancer 实现
        ReactiveLoadBalancer<ServiceInstance> reactiveLoadBalancer = super.getInstance(serviceId);

        // 参考 {@link com.alibaba.cloud.nacos.loadbalancer.NacosLoadBalancerClientConfiguration#nacosLoadBalancer(Environment, LoadBalancerClientFactory, NacosDiscoveryProperties)} 方法
        // 创建一个 EnvLoadBalancerClient 代理对象，将默认的负载均衡器传入，进行 tag 过滤
        return new EnvLoadBalancerClient(super.getLazyProvider(serviceId, ServiceInstanceListSupplier.class),  // 获取服务实例列表的供应商
                serviceId,  // 服务 ID
                reactiveLoadBalancer  // 默认的负载均衡器
        );
    }
}
