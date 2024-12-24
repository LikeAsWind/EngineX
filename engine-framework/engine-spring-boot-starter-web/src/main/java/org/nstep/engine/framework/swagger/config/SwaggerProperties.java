package org.nstep.engine.framework.swagger.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Swagger 配置属性类，用于加载和验证 Swagger 配置项。
 * <p>
 * 该类通过 `@ConfigurationProperties` 注解从配置文件中加载 Swagger 相关的配置信息，
 * 包括标题、描述、作者、版本等字段，并且使用了 `@NotEmpty` 注解来确保这些字段不能为空。
 * 这些配置项将用于生成 Swagger（OpenAPI）文档的基本信息。
 * <p>
 * 例如，`title` 是文档的标题，`description` 是文档的描述，`author` 是文档的作者，`version` 是 API 的版本等。
 */
@ConfigurationProperties("engine.swagger")
@Data
public class SwaggerProperties {

    /**
     * 文档标题
     * <p>
     * 该字段用于设置生成的 Swagger 文档的标题。
     * 必须提供非空值。
     */
    @NotEmpty(message = "标题不能为空")
    private String title;

    /**
     * 文档描述
     * <p>
     * 该字段用于设置生成的 Swagger 文档的描述信息。
     * 必须提供非空值。
     */
    @NotEmpty(message = "描述不能为空")
    private String description;

    /**
     * 文档作者
     * <p>
     * 该字段用于设置 Swagger 文档的作者信息。
     * 必须提供非空值。
     */
    @NotEmpty(message = "作者不能为空")
    private String author;

    /**
     * API 版本
     * <p>
     * 该字段用于设置 Swagger 文档的版本信息。
     * 必须提供非空值。
     */
    @NotEmpty(message = "版本不能为空")
    private String version;

    /**
     * 文档 URL
     * <p>
     * 该字段用于设置 Swagger 文档的 URL 地址。
     * 必须提供非空值。
     */
    @NotEmpty(message = "扫描的 package 不能为空")
    private String url;

    /**
     * 联系人的电子邮件
     * <p>
     * 该字段用于设置文档作者的电子邮件地址。
     * 必须提供非空值。
     */
    @NotEmpty(message = "扫描的 email 不能为空")
    private String email;

    /**
     * 文档的 License 名称
     * <p>
     * 该字段用于设置 Swagger 文档的 License 名称。
     * 必须提供非空值。
     */
    @NotEmpty(message = "扫描的 license 不能为空")
    private String license;

    /**
     * License URL
     * <p>
     * 该字段用于设置 Swagger 文档的 License URL 地址。
     * 必须提供非空值。
     */
    @NotEmpty(message = "扫描的 license-url 不能为空")
    private String licenseUrl;
}
