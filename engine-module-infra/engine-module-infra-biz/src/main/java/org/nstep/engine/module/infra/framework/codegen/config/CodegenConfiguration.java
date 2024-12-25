package org.nstep.engine.module.infra.framework.codegen.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 代码生成器配置类。
 *
 * <p>该类用于Spring框架的配置，启用代码生成器相关属性，并配置相关的Bean。</p>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CodegenProperties.class)
public class CodegenConfiguration {
}
