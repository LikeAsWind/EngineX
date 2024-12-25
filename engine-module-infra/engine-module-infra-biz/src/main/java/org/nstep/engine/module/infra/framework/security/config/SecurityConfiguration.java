package org.nstep.engine.module.infra.framework.security.config;

import org.nstep.engine.framework.security.config.AuthorizeRequestsCustomizer;
import org.nstep.engine.module.infra.enums.ApiConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * Infra 模块的 Security 配置类
 * <p>
 * 该配置类主要用于配置 Infra 模块的安全设置，包括对不同路径的访问权限进行控制。
 * 例如，开放 Swagger、Actuator、Druid 监控、Spring Boot Admin Server、文件读取接口等。
 * 这些配置通过 AuthorizeRequestsCustomizer 来实现。
 */
@Configuration(proxyBeanMethods = false, value = "infraSecurityConfiguration")
public class SecurityConfiguration {

    /**
     * Spring Boot Admin Server 的上下文路径，通过配置文件注入。
     * 用于确定 Admin Server 的访问路径。
     */
    @Value("${spring.boot.admin.context-path:''}")
    private String adminSeverContextPath;

    /**
     * 定义一个 Bean，用于定制化配置 Spring Security 的请求授权规则。
     * 通过此方法，我们可以配置哪些路径允许匿名访问，哪些路径需要认证。
     *
     * @return 返回一个 AuthorizeRequestsCustomizer 对象，该对象实现了对请求权限的定制化配置。
     */
    @Bean("infraAuthorizeRequestsCustomizer")
    public AuthorizeRequestsCustomizer authorizeRequestsCustomizer() {
        return new AuthorizeRequestsCustomizer() {

            /**
             * 定制化请求授权规则的方法。
             * 通过该方法配置具体的路径和访问权限，例如允许匿名访问 Swagger 文档、Actuator、Druid 监控等。
             *
             * @param registry 用于配置不同路径的访问权限。
             */
            @Override
            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
                // 配置 Swagger 接口文档路径，允许所有用户访问
                registry.requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/swagger-ui").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll();

                // 配置 Spring Boot Actuator 路径，允许所有用户访问
                registry.requestMatchers("/actuator").permitAll()
                        .requestMatchers("/actuator/**").permitAll();

                // 配置 Druid 监控路径，允许所有用户访问
                registry.requestMatchers("/druid/**").permitAll();

                // 配置 Spring Boot Admin Server 路径，允许所有用户访问
                registry.requestMatchers(adminSeverContextPath).permitAll()
                        .requestMatchers(adminSeverContextPath + "/**").permitAll();

                // 配置文件读取接口路径，允许所有用户访问
                registry.requestMatchers(buildAdminApi("/infra/file/*/get/**")).permitAll();

                // 配置 RPC 服务路径，允许所有用户访问
                // 这个配置是为了开放 API 服务的路径，确保 RPC 服务的安全性。
                registry.requestMatchers(ApiConstants.PREFIX + "/**").permitAll();
            }

        };
    }

}
