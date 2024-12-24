package org.nstep.engine.framework.xss.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nstep.engine.framework.common.enums.WebFilterOrderEnum;
import org.nstep.engine.framework.xss.core.clean.JsoupXssCleaner;
import org.nstep.engine.framework.xss.core.clean.XssCleaner;
import org.nstep.engine.framework.xss.core.filter.XssFilter;
import org.nstep.engine.framework.xss.core.json.XssStringJsonDeserializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.nstep.engine.framework.web.config.EngineWebAutoConfiguration.createFilterBean;

/**
 * 自动配置类，用于配置 XSS 安全防护。
 * <p>
 * 该配置类在应用启动时自动加载，基于配置文件中的属性来启用或禁用 XSS 过滤功能。
 * 如果启用 XSS 防护，则会注册 XSS 清理器、Jackson 序列化器和 XSS 过滤器。
 */
@AutoConfiguration
@EnableConfigurationProperties(XssProperties.class) // 启用 XssProperties 配置类的加载
@ConditionalOnProperty(prefix = "engine.xss", name = "enable", havingValue = "true", matchIfMissing = true)
// 如果配置文件中 "engine.xss.enable" 为 true 或未配置，则启用此配置
public class EngineXssAutoConfiguration implements WebMvcConfigurer {

    /**
     * 创建 XssCleaner Bean，默认使用 JsoupXssCleaner 实现。
     * <p>
     * XssCleaner 用于清理请求中的 XSS 攻击内容。
     *
     * @return XssCleaner 实现，默认使用 JsoupXssCleaner
     */
    @Bean
    @ConditionalOnMissingBean(XssCleaner.class) // 如果容器中没有 XssCleaner 类型的 Bean，则创建该 Bean
    public XssCleaner xssCleaner() {
        return new JsoupXssCleaner(); // 使用 JsoupXssCleaner 作为默认实现
    }

    /**
     * 注册 Jackson 的序列化器，用于处理 JSON 类型参数的 XSS 过滤。
     * <p>
     * 在反序列化时，使用自定义的 XssStringJsonDeserializer 来处理 XSS 过滤。
     *
     * @param properties  Xss 配置属性
     * @param pathMatcher 路径匹配器，用于匹配需要过滤的路径
     * @param xssCleaner  Xss 清理器，用于执行 XSS 清理操作
     * @return Jackson2ObjectMapperBuilderCustomizer，用于自定义 Jackson 的反序列化器
     */
    @Bean
    @ConditionalOnMissingBean(name = "xssJacksonCustomizer") // 如果没有名为 "xssJacksonCustomizer" 的 Bean，则创建
    @ConditionalOnBean(ObjectMapper.class) // 确保 ObjectMapper Bean 已存在
    @ConditionalOnProperty(value = "engine.xss.enable", havingValue = "true") // 如果启用了 XSS 防护，则注册该序列化器
    public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer(XssProperties properties,
                                                                      PathMatcher pathMatcher,
                                                                      XssCleaner xssCleaner) {
        // 在反序列化时进行 XSS 过滤，可以替换使用 XssStringJsonSerializer，在序列化时进行处理
        return builder -> builder.deserializerByType(String.class, new XssStringJsonDeserializer(properties, pathMatcher, xssCleaner));
    }

    /**
     * 创建 XssFilter Bean，解决 XSS 安全问题。
     * <p>
     * 注册 XssFilter 过滤器，用于处理 HTTP 请求中的 XSS 攻击内容。
     *
     * @param properties  Xss 配置属性
     * @param pathMatcher 路径匹配器，用于匹配需要过滤的路径
     * @param xssCleaner  Xss 清理器，用于执行 XSS 清理操作
     * @return FilterRegistrationBean，注册 XSS 过滤器
     */
    @Bean
    @ConditionalOnBean(XssCleaner.class) // 确保 XssCleaner Bean 已存在
    public FilterRegistrationBean<XssFilter> xssFilter(XssProperties properties, PathMatcher pathMatcher, XssCleaner xssCleaner) {
        // 创建并返回 XssFilter 的注册 Bean
        return createFilterBean(new XssFilter(properties, pathMatcher, xssCleaner), WebFilterOrderEnum.XSS_FILTER);
    }

}
