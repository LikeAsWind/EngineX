package org.nstep.engine.framework.security.config;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.Resource;
import org.nstep.engine.framework.security.core.context.TransmittableThreadLocalSecurityContextHolderStrategy;
import org.nstep.engine.framework.security.core.filter.TokenAuthenticationFilter;
import org.nstep.engine.framework.security.core.handler.AccessDeniedHandlerImpl;
import org.nstep.engine.framework.security.core.handler.AuthenticationEntryPointImpl;
import org.nstep.engine.framework.security.core.service.SecurityFrameworkService;
import org.nstep.engine.framework.security.core.service.SecurityFrameworkServiceImpl;
import org.nstep.engine.framework.web.core.handler.GlobalExceptionHandler;
import org.nstep.engine.module.system.api.oauth2.OAuth2TokenApi;
import org.nstep.engine.module.system.api.permission.PermissionApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Spring Security 自动配置类，主要用于相关组件的配置
 * <p>
 * 该类用于自动配置 Spring Security 相关的组件。需要注意，不能与 {@link EngineWebSecurityConfigurerAdapter} 配合使用，
 * 否则会导致初始化报错。具体问题参见：
 * <a href= https://stackoverflow.com/questions/53847050/spring-boot-delegatebuilder-cannot-be-null-on-autowiring-authenticationmanager></a>
 * </p>
 */
@AutoConfiguration
@AutoConfigureOrder(-1) // 目的：先于 Spring Security 自动配置，避免一键改包后，org.* 基础包无法生效
@EnableConfigurationProperties(SecurityProperties.class) // 启用配置属性类，读取安全配置
public class EngineSecurityAutoConfiguration {

    @Resource
    private SecurityProperties securityProperties; // 注入 SecurityProperties 配置类，用于获取安全配置

    /**
     * 认证失败处理类 Bean
     * <p>
     * 当认证失败时，使用此处理类来处理认证失败的情况。
     * </p>
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl(); // 自定义认证失败处理器
    }

    /**
     * 权限不够处理器 Bean
     * <p>
     * 当用户没有足够权限时，使用此处理器来处理权限不足的情况。
     * </p>
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl(); // 自定义权限不足处理器
    }

    /**
     * Spring Security 加密器 Bean
     * <p>
     * 采用 BCryptPasswordEncoder 作为密码加密器，考虑到安全性。
     * </p>
     *
     * @see <a href="http://stackabuse.com/password-encoding-with-spring-security/">Password Encoding with Spring Security</a>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(securityProperties.getPasswordEncoderLength()); // 使用配置的加密长度
    }

    /**
     * Token 认证过滤器 Bean
     * <p>
     * 用于拦截请求并进行 Token 认证。
     * </p>
     */
    @Bean
    public TokenAuthenticationFilter authenticationTokenFilter(GlobalExceptionHandler globalExceptionHandler,
                                                               @Qualifier("org.nstep.engine.module.system.api.oauth2.OAuth2TokenApi") OAuth2TokenApi oauth2TokenApi) {
        return new TokenAuthenticationFilter(securityProperties, globalExceptionHandler, oauth2TokenApi); // 自定义 Token 认证过滤器
    }

    /**
     * 声明一个 SecurityFrameworkService Bean
     * <p>
     * 用于处理权限相关的服务，使用 Spring Security 的缩写 "ss" 作为 Bean 名称，方便使用。
     * </p>
     */
    @Bean("ss") // 使用 Spring Security 的缩写，方便使用
    public SecurityFrameworkService securityFrameworkService(@Qualifier("org.nstep.engine.module.system.api.permission.PermissionApi") PermissionApi permissionApi) {
        return new SecurityFrameworkServiceImpl(permissionApi); // 实现权限 API 的安全框架服务
    }

    /**
     * 声明调用 {@link SecurityContextHolder#setStrategyName(String)} 方法，
     * 设置使用 {@link TransmittableThreadLocalSecurityContextHolderStrategy} 作为 Security 的上下文策略
     * <p>
     * 该配置确保 Spring Security 的上下文可以跨线程传递。
     * </p>
     */
    @Bean
    public MethodInvokingFactoryBean securityContextHolderMethodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class); // 目标类：SecurityContextHolder
        methodInvokingFactoryBean.setTargetMethod("setStrategyName"); // 目标方法：setStrategyName
        methodInvokingFactoryBean.setArguments(TransmittableThreadLocalSecurityContextHolderStrategy.class.getName()); // 设置策略名称
        return methodInvokingFactoryBean;
    }

}
