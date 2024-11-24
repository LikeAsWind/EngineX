package org.nstep.engine.framework.tracer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BizTracer配置类
 */
@ConfigurationProperties("engine.tracer")
@Data
public class TracerProperties {
}
