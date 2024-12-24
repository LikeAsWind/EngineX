package org.nstep.engine.framework.datasource.config;

import com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties;
import org.nstep.engine.framework.datasource.core.filter.DruidAdRemoveFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据库配置类
 * <p>
 * 该类负责配置数据源相关的设置，并启用事务管理。还根据条件注册了一个 Druid 过滤器，用于去除 Druid 监控页面中的广告。
 */
@AutoConfiguration
@EnableTransactionManagement(proxyTargetClass = true) // 启动事务管理，使用 CGLIB 动态代理
@EnableConfigurationProperties(DruidStatProperties.class) // 启用 DruidStatProperties 配置类，用于读取 Druid 监控相关的配置
public class EngineDataSourceAutoConfiguration {

    /**
     * 创建 DruidAdRemoveFilter 过滤器，过滤 common.js 的广告
     * <p>
     * 该方法根据配置文件中的设置，判断是否启用 Druid 监控页面，并根据 URL 模式配置过滤器，用于去除广告。
     *
     * @param properties DruidStatProperties 配置类
     * @return FilterRegistrationBean<DruidAdRemoveFilter> 返回配置好的过滤器注册 Bean
     */
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.stat-view-servlet.enabled", havingValue = "true")
    // 仅当配置项为 true 时启用该过滤器
    public FilterRegistrationBean<DruidAdRemoveFilter> druidAdRemoveFilterFilter(DruidStatProperties properties) {
        // 获取 Druid 监控页面的配置
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();

        // 获取 URL 模式，默认值为 "/druid/*"
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";

        // 构造 common.js 的 URL 模式
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");

        // 创建 DruidAdRemoveFilter 过滤器并注册
        FilterRegistrationBean<DruidAdRemoveFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DruidAdRemoveFilter()); // 设置过滤器实例
        registrationBean.addUrlPatterns(commonJsPattern); // 配置过滤器的 URL 匹配模式

        return registrationBean; // 返回配置好的过滤器注册 Bean
    }

}
