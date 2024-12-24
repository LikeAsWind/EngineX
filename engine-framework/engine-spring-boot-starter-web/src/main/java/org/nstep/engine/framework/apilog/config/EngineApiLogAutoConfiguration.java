package org.nstep.engine.framework.apilog.config;

import jakarta.servlet.Filter;
import org.nstep.engine.framework.apilog.core.filter.ApiAccessLogFilter;
import org.nstep.engine.framework.apilog.core.interceptor.ApiAccessLogInterceptor;
import org.nstep.engine.framework.common.enums.WebFilterOrderEnum;
import org.nstep.engine.framework.web.config.EngineWebAutoConfiguration;
import org.nstep.engine.framework.web.config.WebProperties;
import org.nstep.engine.module.infra.api.logger.ApiAccessLogApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration(after = EngineWebAutoConfiguration.class)
public class EngineApiLogAutoConfiguration implements WebMvcConfigurer {

    /**
     * 创建 FilterRegistrationBean
     * <p>
     * 用于注册一个 Filter，并设置其执行顺序。
     *
     * @param <T>    Filter 类型
     * @param filter 要注册的 Filter
     * @return 注册后的 FilterRegistrationBean
     */
    private static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(WebFilterOrderEnum.API_ACCESS_LOG_FILTER); // 设置 Filter 的执行顺序
        return bean;
    }

    /**
     * 创建 ApiAccessLogFilter Bean，记录 API 请求日志
     * <p>
     * 该方法根据配置条件（`engine.access-log.enable`）决定是否启用 API 访问日志功能。
     * 默认情况下，访问日志是启用的。
     *
     * @param webProperties   Web 配置属性
     * @param applicationName 应用程序名称
     * @param apiAccessLogApi API 访问日志服务
     * @return 配置好的 FilterRegistrationBean
     */
    @Bean
    @ConditionalOnProperty(prefix = "engine.access-log", value = "enable", matchIfMissing = true)
    // 允许使用 engine.access-log.enable=false 禁用访问日志
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public FilterRegistrationBean<ApiAccessLogFilter> apiAccessLogFilter(WebProperties webProperties,
                                                                         @Value("${spring.application.name}") String applicationName,
                                                                         ApiAccessLogApi apiAccessLogApi) {
        ApiAccessLogFilter filter = new ApiAccessLogFilter(webProperties, applicationName, apiAccessLogApi);
        return createFilterBean(filter);
    }

    /**
     * 添加拦截器
     * <p>
     * 该方法用于注册 API 访问日志拦截器（`ApiAccessLogInterceptor`）。
     * 拦截器可以在请求进入控制器之前执行一些逻辑，通常用于记录日志、权限验证等。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiAccessLogInterceptor()); // 注册 API 访问日志拦截器
    }

}
