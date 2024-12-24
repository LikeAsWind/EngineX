package org.nstep.engine.framework.security.config;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.nstep.engine.framework.security.core.filter.TokenAuthenticationFilter;
import org.nstep.engine.framework.web.config.WebProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.nstep.engine.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 自定义的 Spring Security 配置适配器实现
 * <p>
 * 该类是对 Spring Security 的自定义配置，主要用于配置应用的安全策略，包括认证、授权、Token 认证过滤器等。
 * 通过该配置类，可以灵活地对不同的 URL 路径设置不同的安全策略，并支持动态的权限控制。
 * </p>
 */
@AutoConfiguration
@AutoConfigureOrder(-1) // 目的：先于 Spring Security 自动配置，避免一键改包后，org.* 基础包无法生效
@EnableMethodSecurity(securedEnabled = true) // 启用方法级安全
public class EngineWebSecurityConfigurerAdapter {

    @Resource
    private WebProperties webProperties; // 用于获取 Web 配置属性
    @Resource
    private SecurityProperties securityProperties; // 用于获取安全配置属性

    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint; // 认证失败处理器
    @Resource
    private AccessDeniedHandler accessDeniedHandler; // 权限不足处理器
    @Resource
    private TokenAuthenticationFilter authenticationTokenFilter; // Token 认证过滤器

    @Resource
    private List<AuthorizeRequestsCustomizer> authorizeRequestsCustomizers; // 自定义权限映射配置

    @Resource
    private ApplicationContext applicationContext; // Spring 应用上下文

    /**
     * 由于 Spring Security 创建 AuthenticationManager 对象时，没声明 @Bean 注解，导致无法被注入
     * 通过覆写父类的该方法，添加 @Bean 注解，解决该问题
     */
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // 获取 AuthenticationManager
    }

    /**
     * 配置 URL 的安全配置
     * <p>
     * 该方法配置了不同的 URL 路径的安全策略，包括：
     * - 免认证访问的 URL
     * - 需要认证的 URL
     * - 自定义的权限规则
     * </p>
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // 配置跨域、禁用 CSRF、无状态的会话管理
        httpSecurity
                .cors(Customizer.withDefaults()) // 开启跨域支持
                .csrf(AbstractHttpConfigurer::disable) // 禁用 CSRF
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态会话
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // 禁用框架选项
                // 配置认证失败处理器和权限不足处理器
                .exceptionHandling(c -> c.authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        // 获取所有免认证访问的 URL
        Multimap<HttpMethod, String> permitAllUrls = getPermitAllUrlsFromAnnotations();

        // 设置全局的 URL 安全策略
        httpSecurity
                .authorizeHttpRequests(c -> c
                        .requestMatchers(HttpMethod.GET, "/*.html", "/*.css", "/*.js").permitAll() // 静态资源允许匿名访问
                        .requestMatchers(HttpMethod.GET, permitAllUrls.get(HttpMethod.GET).toArray(new String[0])).permitAll() // 允许 @PermitAll 注解的 URL 匿名访问
                        .requestMatchers(HttpMethod.POST, permitAllUrls.get(HttpMethod.POST).toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.PUT, permitAllUrls.get(HttpMethod.PUT).toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.DELETE, permitAllUrls.get(HttpMethod.DELETE).toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.HEAD, permitAllUrls.get(HttpMethod.HEAD).toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.PATCH, permitAllUrls.get(HttpMethod.PATCH).toArray(new String[0])).permitAll()
                        .requestMatchers(securityProperties.getPermitAllUrls().toArray(new String[0])).permitAll() // 根据配置文件中的规则允许匿名访问
                )
                // 允许自定义的权限规则
                .authorizeHttpRequests(c -> authorizeRequestsCustomizers.forEach(customizer -> customizer.customize(c)))
                // 兜底规则，要求认证后才能访问
                .authorizeHttpRequests(c -> c.anyRequest().authenticated());

        // 添加 Token 认证过滤器
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    // 构建应用 API 的 URL
    private String buildAppApi(String url) {
        return webProperties.getAppApi().getPrefix() + url;
    }

    /**
     * 获取所有带有 @PermitAll 注解的接口路径
     * <p>
     * 该方法通过扫描所有接口方法，获取带有 @PermitAll 注解的接口路径，并根据 HTTP 方法分类，返回免认证访问的 URL 列表。
     * </p>
     */
    private Multimap<HttpMethod, String> getPermitAllUrlsFromAnnotations() {
        Multimap<HttpMethod, String> result = HashMultimap.create();
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)
                applicationContext.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!handlerMethod.hasMethodAnnotation(PermitAll.class)) {
                continue;
            }

            Set<String> urls = new HashSet<>();
            if (entry.getKey().getPatternsCondition() != null) {
                urls.addAll(entry.getKey().getPatternsCondition().getPatterns());
            }
            if (entry.getKey().getPathPatternsCondition() != null) {
                urls.addAll(convertList(entry.getKey().getPathPatternsCondition().getPatterns(), PathPattern::getPatternString));
            }

            if (urls.isEmpty()) {
                continue;
            }

            Set<RequestMethod> methods = entry.getKey().getMethodsCondition().getMethods();
            if (CollUtil.isEmpty(methods)) {
                result.putAll(HttpMethod.GET, urls);
                result.putAll(HttpMethod.POST, urls);
                result.putAll(HttpMethod.PUT, urls);
                result.putAll(HttpMethod.DELETE, urls);
                result.putAll(HttpMethod.HEAD, urls);
                result.putAll(HttpMethod.PATCH, urls);
                continue;
            }

            // 根据请求方法添加到结果集
            entry.getKey().getMethodsCondition().getMethods().forEach(requestMethod -> {
                switch (requestMethod) {
                    case GET:
                        result.putAll(HttpMethod.GET, urls);
                        break;
                    case POST:
                        result.putAll(HttpMethod.POST, urls);
                        break;
                    case PUT:
                        result.putAll(HttpMethod.PUT, urls);
                        break;
                    case DELETE:
                        result.putAll(HttpMethod.DELETE, urls);
                        break;
                    case HEAD:
                        result.putAll(HttpMethod.HEAD, urls);
                        break;
                    case PATCH:
                        result.putAll(HttpMethod.PATCH, urls);
                        break;
                }
            });
        }
        return result;
    }

}
