package org.nstep.engine.framework.web.config;

import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.common.enums.WebFilterOrderEnum;
import org.nstep.engine.framework.web.core.filter.CacheRequestBodyFilter;
import org.nstep.engine.framework.web.core.filter.DemoFilter;
import org.nstep.engine.framework.web.core.handler.GlobalExceptionHandler;
import org.nstep.engine.framework.web.core.handler.GlobalResponseBodyHandler;
import org.nstep.engine.framework.web.core.util.WebFrameworkUtils;
import org.nstep.engine.module.infra.api.logger.ApiErrorLogApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * EngineWebAutoConfiguration 类是一个自动配置类，负责 Web 相关的配置和 Bean 注册。
 * <p>
 * 该类通过 Spring Boot 的 `@AutoConfiguration` 注解进行自动配置，自动加载与 Web 相关的配置项，
 * 并通过 `@EnableConfigurationProperties` 注解将 Web 配置属性（`WebProperties`）加载到 Spring 上下文中。
 * 它实现了 `WebMvcConfigurer` 接口，用于配置 Web 相关的设置，如 API 前缀、过滤器等。
 */
@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
public class EngineWebAutoConfiguration implements WebMvcConfigurer {

    @Resource
    private WebProperties webProperties;

    /**
     * 应用名称，从配置文件中读取
     */
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 创建 FilterRegistrationBean，用于注册 Filter 实例并设置执行顺序
     *
     * @param filter Filter 实例
     * @param order  执行顺序
     * @param <T>    Filter 类型
     * @return 配置好的 FilterRegistrationBean 实例
     */
    public static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }

    /**
     * 配置 API 前缀，匹配特定 Controller 包下的路径
     *
     * @param configurer 配置对象
     */
    @Override
    public void configurePathMatch(@NotNull PathMatchConfigurer configurer) {
        configurePathMatch(configurer, webProperties.getAdminApi());
        configurePathMatch(configurer, webProperties.getAppApi());
    }

    /**
     * 设置 API 前缀，仅匹配指定 Controller 包下的路径
     *
     * @param configurer 配置对象
     * @param api        API 配置
     */
    private void configurePathMatch(PathMatchConfigurer configurer, WebProperties.Api api) {
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(api.getController(), clazz.getPackage().getName())); // 仅匹配 Controller 包
    }

    /**
     * 注册 GlobalExceptionHandler Bean，用于全局异常处理
     *
     * @param apiErrorLogApi API 错误日志接口
     * @return GlobalExceptionHandler 实例
     */
    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public GlobalExceptionHandler globalExceptionHandler(ApiErrorLogApi apiErrorLogApi) {
        return new GlobalExceptionHandler(applicationName, apiErrorLogApi);
    }

    /**
     * 注册 GlobalResponseBodyHandler Bean，用于全局响应处理
     *
     * @return GlobalResponseBodyHandler 实例
     */
    @Bean
    public GlobalResponseBodyHandler globalResponseBodyHandler() {
        return new GlobalResponseBodyHandler();
    }

    // ========== Filter 相关 ==========

    /**
     * 注册 WebFrameworkUtils Bean，用于 Web 框架的工具类
     *
     * @param webProperties Web 配置属性
     * @return WebFrameworkUtils 实例
     */
    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public WebFrameworkUtils webFrameworkUtils(WebProperties webProperties) {
        return new WebFrameworkUtils(webProperties);
    }

    /**
     * 创建 CorsFilter Bean，解决跨域问题
     *
     * @return FilterRegistrationBean 实例
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 设置访问源地址
        config.addAllowedHeader("*"); // 设置访问源请求头
        config.addAllowedMethod("*"); // 设置访问源请求方法
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 配置跨域规则
        return createFilterBean(new CorsFilter(source), WebFilterOrderEnum.CORS_FILTER);
    }

    /**
     * 创建 RequestBodyCacheFilter Bean，用于缓存请求体，支持重复读取请求内容
     *
     * @return FilterRegistrationBean 实例
     */
    @Bean
    public FilterRegistrationBean<CacheRequestBodyFilter> requestBodyCacheFilter() {
        return createFilterBean(new CacheRequestBodyFilter(), WebFilterOrderEnum.REQUEST_BODY_CACHE_FILTER);
    }

    /**
     * 创建 DemoFilter Bean，仅在演示模式下启用
     *
     * @return FilterRegistrationBean 实例
     */
    @Bean
    @ConditionalOnProperty(value = "engine.demo", havingValue = "true")
    public FilterRegistrationBean<DemoFilter> demoFilter() {
        return createFilterBean(new DemoFilter(), WebFilterOrderEnum.DEMO_FILTER);
    }

    /**
     * 创建 RestTemplate 实例
     *
     * @param restTemplateBuilder RestTemplate 构建器
     * @return RestTemplate 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

}
