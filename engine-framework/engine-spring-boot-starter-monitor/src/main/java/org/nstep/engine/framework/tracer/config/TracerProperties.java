package org.nstep.engine.framework.tracer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BizTracer 配置类
 * <p>
 * 该类用于将配置文件中的 `engine.tracer` 配置项映射为 Java 对象。
 * 它通过 `@ConfigurationProperties` 注解与 `engine.tracer` 前缀的配置项绑定，
 * 使得我们可以在配置文件中灵活地管理与追踪相关的配置。
 */
@ConfigurationProperties("engine.tracer") // 将配置文件中的 engine.tracer 前缀的属性映射到该类
@Data
public class TracerProperties {
    // 目前没有定义任何属性，未来可以根据需要添加配置项
}
