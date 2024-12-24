package org.nstep.engine.framework.tracer.config;

import org.nstep.engine.framework.common.enums.WebFilterOrderEnum;
import org.nstep.engine.framework.tracer.core.aop.BizTraceAspect;
import org.nstep.engine.framework.tracer.core.filter.TraceFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Tracer 配置类
 * <p>
 * 该配置类用于配置分布式追踪功能。它通过启用 `BizTraceAspect` 和 `Tracer` 配置，
 * 实现对应用程序的请求和响应进行追踪，帮助进行性能监控、故障排查等。
 * <p>
 * 通过 `engine.tracer.enable` 配置项控制是否启用追踪功能，默认启用。
 * 如果设置为 `false`，则禁用追踪功能。
 */
@AutoConfiguration // 自动配置类
@ConditionalOnClass(value = {BizTraceAspect.class}, name = "jakarta.servlet.Filter")
// 只有在 BizTraceAspect 和 Filter 存在时加载此配置
@EnableConfigurationProperties(TracerProperties.class) // 启用 TracerProperties 配置类
@ConditionalOnProperty(prefix = "engine.tracer", value = "enable", matchIfMissing = true)
// 通过 engine.tracer.enable 控制是否启用追踪功能
public class EngineTracerAutoConfiguration {

    // TODO 重要。目前 opentracing 版本存在冲突，要么保证 skywalking，要么保证阿里云短信 sdk
    /*@Bean
    public TracerProperties bizTracerProperties() {
        return new TracerProperties();
    }

    @Bean
    public BizTraceAspect bizTracingAop() {
        return new BizTraceAspect(tracer());
    }

    @Bean
    public Tracer tracer() {
        // 创建 SkywalkingTracer 对象
        SkywalkingTracer tracer = new SkywalkingTracer();
        // 设置为 GlobalTracer 的追踪器
        GlobalTracer.register(tracer);
        return tracer;
    }*/

    /**
     * 创建 TraceFilter 过滤器，响应 header 设置 traceId
     * <p>
     * 该过滤器用于在每个 HTTP 请求的响应头中设置 traceId，支持分布式追踪。
     * traceId 是分布式追踪系统中唯一标识一次请求的标识符，可以帮助追踪请求的流转路径。
     *
     * @return FilterRegistrationBean 配置好的 TraceFilter
     */
    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilter() {
        FilterRegistrationBean<TraceFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TraceFilter()); // 设置过滤器
        registrationBean.setOrder(WebFilterOrderEnum.TRACE_FILTER); // 设置过滤器的执行顺序
        return registrationBean;
    }

}
