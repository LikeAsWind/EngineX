package org.nstep.engine.framework.tracer.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Metrics 配置类
 * <p>
 * 该配置类用于集成 Micrometer Metrics 框架，配置应用程序的度量指标。
 * 通过配置 `MeterRegistryCustomizer`，为应用程序的度量指标添加统一的标签（例如：`application` 标签）。
 * <p>
 * 配置项 `engine.metrics.enable` 可以控制是否启用 Metrics 功能，默认启用。如果设置为 `false`，则禁用 Metrics。
 */
@AutoConfiguration // 自动配置类
@ConditionalOnClass({MeterRegistryCustomizer.class}) // 仅当 Micrometer 的 MeterRegistryCustomizer 类存在时生效
@ConditionalOnProperty(prefix = "engine.metrics", value = "enable", matchIfMissing = true)
// 允许通过 engine.metrics.enable=false 禁用 Metrics
public class EngineMetricsAutoConfiguration {

    /**
     * MeterRegistryCustomizer Bean 配置
     * <p>
     * 该 Bean 用于配置 MeterRegistry（度量注册表）的公共标签。
     * 所有的度量指标都会带有 `application` 标签，值为应用程序的名称。
     * 这有助于在多服务环境中区分不同应用程序的度量数据。
     *
     * @param applicationName 应用程序名称，来自配置文件的 `spring.application.name`
     * @return MeterRegistryCustomizer 实例
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(
            @Value("${spring.application.name}") String applicationName) {
        return registry -> registry.config().commonTags("application", applicationName);
    }

}
