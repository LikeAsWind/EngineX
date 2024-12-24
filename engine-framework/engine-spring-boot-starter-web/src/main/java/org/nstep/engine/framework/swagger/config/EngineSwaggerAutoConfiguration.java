package org.nstep.engine.framework.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.nstep.engine.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Swagger 自动配置类，基于 OpenAPI + Springdoc 实现。
 * <p>
 * 该类用于自动配置和初始化 Swagger（OpenAPI）文档生成的相关设置，主要通过 Springdoc 库实现。
 * 它为 API 提供了分组、请求头参数的自动注入、以及安全认证等功能配置。
 * <p>
 * 友情提示：
 * 1. Springdoc 文档地址：<a href="https://github.com/springdoc/springdoc-openapi">仓库</a>
 * 2. Swagger 规范，于 2015 更名为 OpenAPI 规范，本质是一个东西。
 */
@AutoConfiguration
@ConditionalOnClass({OpenAPI.class})
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true)
// 设置为 false 时，禁用
public class EngineSwaggerAutoConfiguration {

    // ========== 全局 OpenAPI 配置 ==========

    /**
     * 构建分组 OpenAPI 配置
     *
     * @param group 分组名称
     * @return 配置好的 GroupedOpenApi 实例
     */
    public static GroupedOpenApi buildGroupedOpenApi(String group) {
        return buildGroupedOpenApi(group, group);
    }

    /**
     * 构建分组 OpenAPI 配置，允许自定义路径
     *
     * @param group 分组名称
     * @param path  自定义路径
     * @return 配置好的 GroupedOpenApi 实例
     */
    public static GroupedOpenApi buildGroupedOpenApi(String group, String path) {
        return GroupedOpenApi.builder()
                .group(group)
                .pathsToMatch("/admin-api/" + path + "/**", "/app-api/" + path + "/**")
                .addOperationCustomizer((operation, handlerMethod) -> operation
                        .addParametersItem(buildTenantHeaderParameter())
                        .addParametersItem(buildSecurityHeaderParameter()))
                .build();
    }

    /**
     * 构建 Tenant 租户编号请求头参数
     *
     * @return 多租户参数
     */
    private static Parameter buildTenantHeaderParameter() {
        return new Parameter()
                .name(HEADER_TENANT_ID) // header 名
                .description("租户编号") // 描述
                .in(String.valueOf(SecurityScheme.In.HEADER)) // 请求 header
                .schema(new IntegerSchema()._default(1L).name(HEADER_TENANT_ID).description("租户编号")); // 默认：使用租户编号为 1
    }

    /**
     * 构建 Authorization 认证请求头参数
     * 解决 Knife4j <a href="https://gitee.com/xiaoym/knife4j/issues/I69QBU">Authorize 未生效，请求header里未包含参数</a>
     *
     * @return 认证参数
     */
    private static Parameter buildSecurityHeaderParameter() {
        return new Parameter()
                .name(HttpHeaders.AUTHORIZATION) // header 名
                .description("认证 Token") // 描述
                .in(String.valueOf(SecurityScheme.In.HEADER)) // 请求 header
                .schema(new StringSchema()._default("Bearer test1").name(HEADER_TENANT_ID).description("认证 Token")); // 默认：使用用户编号为 1
    }

    // ========== 分组 OpenAPI 配置 ==========

    /**
     * 创建 OpenAPI 配置
     *
     * @param properties Swagger 配置属性
     * @return 配置好的 OpenAPI 实例
     */
    @Bean
    public OpenAPI createApi(SwaggerProperties properties) {
        Map<String, SecurityScheme> securitySchemas = buildSecuritySchemes();
        OpenAPI openAPI = new OpenAPI()
                // 接口信息
                .info(buildInfo(properties))
                // 接口安全配置
                .components(new Components().securitySchemes(securitySchemas))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
        securitySchemas.keySet().forEach(key -> openAPI.addSecurityItem(new SecurityRequirement().addList(key)));
        return openAPI;
    }

    /**
     * 构建 API 摘要信息
     *
     * @param properties Swagger 配置属性
     * @return 配置好的 Info 实例
     */
    private Info buildInfo(SwaggerProperties properties) {
        return new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion())
                .contact(new Contact().name(properties.getAuthor()).url(properties.getUrl()).email(properties.getEmail()))
                .license(new License().name(properties.getLicense()).url(properties.getLicenseUrl()));
    }

    /**
     * 配置安全模式，使用请求头 Authorization 传递 token 参数
     *
     * @return 安全模式配置
     */
    private Map<String, SecurityScheme> buildSecuritySchemes() {
        Map<String, SecurityScheme> securitySchemes = new HashMap<>();
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY) // 类型
                .name(HttpHeaders.AUTHORIZATION) // 请求头的 name
                .in(SecurityScheme.In.HEADER); // token 所在位置
        securitySchemes.put(HttpHeaders.AUTHORIZATION, securityScheme);
        return securitySchemes;
    }

    /**
     * 自定义 OpenAPI 处理器，确保我们创建的 OpenAPIService Bean 为主
     *
     * @return 配置好的 OpenAPIService 实例
     */
    @Bean
    @Primary // 目的：以我们创建的 OpenAPIService Bean 为主，避免一键改包后，启动报错！
    public OpenAPIService openApiBuilder(Optional<OpenAPI> openAPI,
                                         SecurityService securityParser,
                                         SpringDocConfigProperties springDocConfigProperties,
                                         PropertyResolverUtils propertyResolverUtils,
                                         Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers,
                                         Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers,
                                         Optional<JavadocProvider> javadocProvider) {

        return new OpenAPIService(openAPI, securityParser, springDocConfigProperties,
                propertyResolverUtils, openApiBuilderCustomizers, serverBaseUrlCustomizers, javadocProvider);
    }

    /**
     * 创建所有模块的 API 分组
     *
     * @return 配置好的 GroupedOpenApi 实例
     */
    @Bean
    public GroupedOpenApi allGroupedOpenApi() {
        return buildGroupedOpenApi("all", "");
    }

}
