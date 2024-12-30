package org.nstep.engine.module.message.framework.security.config;


import org.nstep.engine.framework.security.config.AuthorizeRequestsCustomizer;
import org.nstep.engine.module.infra.enums.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * message 模块的 Security 配置
 * <p>
 * 该配置类用于设置应用程序的安全配置，主要通过 Spring Security 框架进行控制。它定义了哪些路径可以公开访问，哪些需要授权。
 * 包含对Swagger接口文档、Druid监控、Spring Boot Actuator和RPC服务的安全配置。
 */
@Configuration(proxyBeanMethods = false, value = "messageSecurityConfiguration")
public class SecurityConfiguration {

    /**
     * 定义授权请求的定制化配置
     * <p>
     * 该方法用于配置哪些请求可以被公开访问，不需要身份验证。它通过覆盖默认的安全配置，允许特定的URL路径不受保护。
     * 主要配置了Swagger、Druid监控、Spring Boot Actuator以及RPC服务的公开访问权限。
     *
     * @return 返回一个自定义的AuthorizeRequestsCustomizer对象，用于配置请求匹配器的授权规则。
     */
    @Bean
    public AuthorizeRequestsCustomizer authorizeRequestsCustomizer() {
        return new AuthorizeRequestsCustomizer() {
            @Override
            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
                // Swagger 接口文档
                registry.requestMatchers("/v3/api-docs/**").permitAll()  // 允许访问Swagger的API文档
                        .requestMatchers("/webjars/**").permitAll()  // 允许访问Swagger的Web资源
                        .requestMatchers("/swagger-ui").permitAll()  // 允许访问Swagger UI首页
                        .requestMatchers("/swagger-ui/**").permitAll();  // 允许访问Swagger UI的其他资源
                // Druid 监控
                registry.requestMatchers("/druid/**").permitAll();  // 允许访问Druid监控页面
                // Spring Boot Actuator 的安全配置
                registry.requestMatchers("/actuator").permitAll()  // 允许访问Actuator的根路径
                        .requestMatchers("/actuator/**").permitAll();  // 允许访问Actuator的其他路径
                // RPC 服务的安全配置
                registry.requestMatchers(ApiConstants.PREFIX + "/**").permitAll();  // 允许访问RPC服务相关的路径
            }

        };
    }

}
