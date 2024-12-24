package org.nstep.engine.framework.security.config;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.web.config.WebProperties;
import org.springframework.core.Ordered;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * 自定义的 URL 的安全配置
 * <p>
 * 该类的目的是为每个 Maven Module 提供自定义的安全规则配置。通过扩展该类，可以根据需要自定义不同模块的安全配置。
 * </p>
 */
public abstract class AuthorizeRequestsCustomizer
        implements Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>, Ordered {

    @Resource
    private WebProperties webProperties; // 注入 WebProperties 配置类，用于获取 API 前缀等配置

    /**
     * 构建管理员 API URL
     * <p>
     * 通过 WebProperties 中的 adminApi 配置项，拼接出完整的管理员 API URL。
     * </p>
     *
     * @param url 需要拼接的 URL 路径
     * @return 完整的管理员 API URL
     */
    protected String buildAdminApi(String url) {
        return webProperties.getAdminApi().getPrefix() + url; // 拼接管理员 API 前缀和 URL
    }

    /**
     * 构建应用 API URL
     * <p>
     * 通过 WebProperties 中的 appApi 配置项，拼接出完整的应用 API URL。
     * </p>
     *
     * @param url 需要拼接的 URL 路径
     * @return 完整的应用 API URL
     */
    protected String buildAppApi(String url) {
        return webProperties.getAppApi().getPrefix() + url; // 拼接应用 API 前缀和 URL
    }

    /**
     * 获取排序顺序
     * <p>
     * 返回 0，表示该配置类的优先级较高，通常用于 Spring 安全配置中的自定义规则。
     * </p>
     *
     * @return 排序顺序，0 表示优先级最高
     */
    @Override
    public int getOrder() {
        return 0; // 该配置类的优先级为 0，表示它会最先执行
    }

}
