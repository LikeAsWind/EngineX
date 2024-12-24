package org.nstep.engine.framework.env.config;

import org.nstep.engine.framework.env.core.fegin.EnvLoadBalancerClientFactory;
import org.nstep.engine.framework.env.core.fegin.EnvRequestInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientSpecification;
import org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.List;

/**
 * 多环境的 RPC 组件的自动配置
 * <p>
 * 该类负责自动配置与多环境相关的 RPC 组件，特别是 Feign 客户端的负载均衡和请求拦截器。
 */
@AutoConfiguration  // 标记为自动配置类，Spring Boot 会自动加载此配置
// 启用 EnvProperties 配置类，加载环境配置属性
@EnableConfigurationProperties(EnvProperties.class)
public class EngineEnvRpcAutoConfiguration {

    // ========== Feign 相关 ==========

    /**
     * 创建 {@link EnvLoadBalancerClientFactory} Bean
     * <p>
     * 该方法用于创建一个负载均衡客户端工厂，返回一个 {@link LoadBalancerClientFactory} 的实例。
     * 该工厂会根据配置的属性和负载均衡客户端的指定配置来创建相应的客户端。
     * <p>
     * 参考 {@link LoadBalancerAutoConfiguration loadBalancerClientFactory(LoadBalancerClientsProperties)} 方法。
     *
     * @param properties     配置文件中的负载均衡客户端属性
     * @param configurations 负载均衡客户端的额外配置（如有）
     * @return 返回一个 {@link EnvLoadBalancerClientFactory} 实例
     */
    @Bean  // 将该方法的返回值注册为 Spring 容器中的 Bean
    public LoadBalancerClientFactory loadBalancerClientFactory(LoadBalancerClientsProperties properties,
                                                               ObjectProvider<List<LoadBalancerClientSpecification>> configurations) {
        // 创建 EnvLoadBalancerClientFactory 实例
        EnvLoadBalancerClientFactory clientFactory = new EnvLoadBalancerClientFactory(properties);

        // 设置额外的负载均衡配置，如果没有提供则使用空列表
        clientFactory.setConfigurations(configurations.getIfAvailable(Collections::emptyList));

        // 返回创建的客户端工厂
        return clientFactory;
    }

    /**
     * 创建 {@link EnvRequestInterceptor} Bean
     * <p>
     * 该方法创建并返回一个 {@link EnvRequestInterceptor} 实例，用于拦截 RPC 请求并处理与环境相关的逻辑。
     *
     * @return 返回一个 {@link EnvRequestInterceptor} 实例
     */
    @Bean  // 将该方法的返回值注册为 Spring 容器中的 Bean
    public EnvRequestInterceptor envRequestInterceptor() {
        // 创建并返回一个新的 EnvRequestInterceptor 实例
        return new EnvRequestInterceptor();
    }

}
