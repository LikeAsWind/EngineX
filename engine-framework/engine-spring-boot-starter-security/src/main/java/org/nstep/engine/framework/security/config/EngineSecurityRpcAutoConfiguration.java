package org.nstep.engine.framework.security.config;

import org.nstep.engine.framework.security.core.rpc.LoginUserRequestInterceptor;
import org.nstep.engine.module.system.api.oauth2.OAuth2TokenApi;
import org.nstep.engine.module.system.api.permission.PermissionApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * Security 使用到 Feign 的配置项
 * <p>
 * 该类用于配置 Feign 客户端相关的服务，主要引入与安全相关的 API 服务，并定义 Feign 请求拦截器。
 * </p>
 */
@AutoConfiguration
@EnableFeignClients(clients = {OAuth2TokenApi.class, // 主要是引入 OAuth2 Token 相关的 API 服务
        PermissionApi.class}) // 引入权限相关的 API 服务
public class EngineSecurityRpcAutoConfiguration {

    /**
     * Feign 请求拦截器 Bean
     * <p>
     * 该拦截器用于在每次 Feign 请求时，自动添加登录用户的信息（例如用户 ID、用户类型等）到请求中。
     * </p>
     */
    @Bean
    public LoginUserRequestInterceptor loginUserRequestInterceptor() {
        return new LoginUserRequestInterceptor(); // 自定义的 Feign 请求拦截器
    }

}
