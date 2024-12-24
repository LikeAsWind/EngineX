package org.nstep.engine.framework.xss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

/**
 * Xss 配置属性类，用于从配置文件中加载与 XSS 防护相关的配置。
 * <p>
 * 该类将根据配置文件中的 `engine.xss` 前缀自动加载相关的配置项，主要用于控制 XSS 防护功能的启用与禁用，
 * 以及指定需要排除 XSS 过滤的 URL 列表。
 */
@ConfigurationProperties(prefix = "engine.xss") // 从配置文件中加载以 "engine.xss" 为前缀的配置
@Validated // 启用验证功能，确保配置项符合预期
@Data // 自动生成 getter、setter、toString、equals 和 hashCode 方法
public class XssProperties {

    /**
     * 是否开启 XSS 防护功能。默认为 true，表示启用 XSS 防护。
     * <p>
     * 如果设置为 false，将禁用 XSS 防护功能。
     */
    private boolean enable = true;

    /**
     * 需要排除 XSS 过滤的 URL 列表。默认为空列表，表示没有 URL 被排除。
     * <p>
     * 可以在配置中指定某些 URL，不对其进行 XSS 过滤。格式为列表，可以包含多个 URL 模式。
     */
    private List<String> excludeUrls = Collections.emptyList();

}
