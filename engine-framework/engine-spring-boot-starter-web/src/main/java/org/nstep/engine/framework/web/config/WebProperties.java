package org.nstep.engine.framework.web.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * WebProperties 类用于存储 Web 配置的属性，配置文件中的 `engine.web` 前缀的所有配置项。
 * <p>
 * 该类包含了与 Web 相关的配置信息，如 API 的前缀、Controller 所在包的路径规则以及 Admin UI 的访问地址。
 * 它还通过 `@Validated` 注解确保配置项的有效性，并通过 `@ConfigurationProperties` 注解将配置文件中的属性映射到该类中。
 */
@ConfigurationProperties(prefix = "engine.web")
@Validated
@Data
public class WebProperties {

    /**
     * App API 配置，包含 API 前缀和 Controller 所在包的路径规则。
     * <p>
     * 用于定义与 App 相关的 RESTFul API 的前缀和匹配的 Controller 包路径。
     */
    @NotNull(message = "APP API 不能为空")
    private Api appApi = new Api("/app-api", "**.controller.app.**");

    /**
     * Admin API 配置，包含 API 前缀和 Controller 所在包的路径规则。
     * <p>
     * 用于定义与 Admin 相关的 RESTFul API 的前缀和匹配的 Controller 包路径。
     */
    @NotNull(message = "Admin API 不能为空")
    private Api adminApi = new Api("/admin-api", "**.controller.admin.**");

    /**
     * Admin UI 配置，包含访问地址。
     * <p>
     * 用于配置 Admin UI 的访问地址。
     */
    @NotNull(message = "Admin UI 不能为空")
    private Ui adminUi;

    /**
     * Api 内部类用于定义 API 配置，包括 API 前缀和 Controller 所在包路径。
     * <p>
     * API 前缀用于设置统一的 API 前缀，Controller 所在包的路径规则用于限定哪些 Controller 属于该 API。
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Valid
    public static class Api {

        /**
         * API 前缀，实现所有 Controller 提供的 RESTFul API 的统一前缀。
         * <p>
         * 该前缀有助于避免 Swagger、Actuator 等工具意外暴露敏感接口，提升安全性。
         */
        @NotEmpty(message = "API 前缀不能为空")
        private String prefix;

        /**
         * Controller 所在包的 Ant 路径规则，用于匹配指定的 Controller。
         * <p>
         * 主要用于给指定的 Controller 设置前缀。
         */
        @NotEmpty(message = "Controller 所在包不能为空")
        private String controller;

    }

    /**
     * Ui 内部类用于定义 Admin UI 配置，包括访问地址。
     * <p>
     * 用于配置 Admin UI 的访问地址。
     */
    @Data
    @Valid
    public static class Ui {

        /**
         * Admin UI 的访问地址。
         */
        private String url;

    }

}

