package org.nstep.engine.framework.env.config;

import org.nstep.engine.framework.common.enums.WebFilterOrderEnum;
import org.nstep.engine.framework.env.core.web.EnvWebFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 多环境的 Web 组件的自动配置
 * <p>
 * 该类负责自动配置与多环境相关的 Web 组件，特别是 Web 过滤器（`EnvWebFilter`）的注册。
 * 它只会在 Web 应用（特别是 Servlet 类型的 Web 应用）中生效。
 */
@AutoConfiguration  // 标记为自动配置类，Spring Boot 会自动加载此配置
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)  // 仅在 Servlet 类型的 Web 应用中生效
@EnableConfigurationProperties(EnvProperties.class)  // 启用 EnvProperties 配置类，加载环境配置属性
public class EngineEnvWebAutoConfiguration {

    /**
     * 创建 {@link EnvWebFilter} Bean
     * <p>
     * 该方法用于创建并注册一个 `EnvWebFilter` 过滤器。
     * `EnvWebFilter` 是一个自定义的过滤器，用于处理与环境相关的 Web 请求。
     * 它将被注册到 Spring Web 环境中，并且可以在请求生命周期中执行特定的环境逻辑。
     *
     * @return 返回一个 {@link FilterRegistrationBean}，用于注册 `EnvWebFilter` 过滤器
     */
    @Bean  // 将该方法的返回值注册为 Spring 容器中的 Bean
    public FilterRegistrationBean<EnvWebFilter> envWebFilterFilter() {
        // 创建 EnvWebFilter 实例
        EnvWebFilter filter = new EnvWebFilter();

        // 创建 FilterRegistrationBean，并将 filter 注册进去
        FilterRegistrationBean<EnvWebFilter> bean = new FilterRegistrationBean<>(filter);

        // 设置过滤器的执行顺序，值越小越先执行
        bean.setOrder(WebFilterOrderEnum.ENV_TAG_FILTER);

        // 返回注册好的过滤器 Bean
        return bean;
    }

}
