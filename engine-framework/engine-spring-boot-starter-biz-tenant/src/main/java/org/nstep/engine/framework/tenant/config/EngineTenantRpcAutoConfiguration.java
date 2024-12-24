package org.nstep.engine.framework.tenant.config;

import org.nstep.engine.framework.tenant.core.rpc.TenantRequestInterceptor;
import org.nstep.engine.module.system.api.tenant.TenantApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "engine.tenant", value = "enable", matchIfMissing = true)
// 通过 engine.tenant.enable 配置项控制是否启用多租户功能，默认启用（如果没有配置该项，则默认为 true）
@EnableFeignClients(clients = TenantApi.class) // 启用 Feign 客户端，主要是引入 TenantApi 接口，供远程调用租户服务
public class EngineTenantRpcAutoConfiguration {

    /**
     * 创建 TenantRequestInterceptor Bean
     * 用于在每个请求中添加租户信息的拦截器，确保租户上下文在远程调用时被传递
     * @return TenantRequestInterceptor 实例
     */
    @Bean
    public TenantRequestInterceptor tenantRequestInterceptor() {
        return new TenantRequestInterceptor();
    }

}
