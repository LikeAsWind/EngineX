package org.nstep.engine.framework.tenant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

/**
 * 多租户配置类
 * 该类用于配置多租户相关的属性，控制多租户的启用与禁用，及需要忽略多租户的请求、表和缓存等。
 */
@ConfigurationProperties(prefix = "engine.tenant") // 绑定 engine.tenant 配置前缀的属性
@Data // 自动生成 getter、setter、toString、equals、hashCode 方法
public class TenantProperties {

    /**
     * 默认启用多租户功能
     */
    private static final Boolean ENABLE_DEFAULT = true;

    /**
     * 是否启用多租户功能
     * 默认值为 ENABLE_DEFAULT，即默认启用多租户功能
     */
    private Boolean enable = ENABLE_DEFAULT;

    /**
     * 需要忽略多租户的请求 URL
     * <p>
     * 默认情况下，每个请求需要带上 tenant-id 的请求头。但某些请求如短信回调、支付回调等 Open API 可能不需要带上租户信息，
     * 可以通过配置该属性来指定这些 URL。
     */
    private Set<String> ignoreUrls = Collections.emptySet();

    /**
     * 需要忽略多租户的数据库表
     * <p>
     * 默认情况下，所有数据库表都启用多租户功能。对于某些表（如系统表等），可能不需要使用租户信息，可以通过该属性配置需要忽略的表。
     * 需要确保这些表没有 tenant_id 字段。
     */
    private Set<String> ignoreTables = Collections.emptySet();

    /**
     * 需要忽略多租户的 Spring Cache 缓存
     * <p>
     * 默认情况下，所有缓存都启用多租户功能。如果某些缓存不需要使用租户信息，可以通过该属性配置需要忽略的缓存。
     * 需要确保这些缓存没有 tenant_id 字段。
     */
    private Set<String> ignoreCaches = Collections.emptySet();

}
