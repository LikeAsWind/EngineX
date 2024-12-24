package org.nstep.engine.framework.datapermission.config;

// 导入所需的类

import org.nstep.engine.framework.datapermission.core.rpc.DataPermissionRequestInterceptor;
import org.nstep.engine.framework.datapermission.core.rpc.DataPermissionRpcWebFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import static org.nstep.engine.framework.common.enums.WebFilterOrderEnum.TENANT_CONTEXT_FILTER;

/**
 * 数据权限针对 RPC 的自动配置类
 */
@AutoConfiguration
@ConditionalOnClass(name = "feign.RequestInterceptor") // 条件注解，当类路径下存在Feign的RequestInterceptor类时，这个配置类才会被注册
public class EngineDataPermissionRpcAutoConfiguration {

    /**
     * 创建数据权限请求拦截器的Bean
     *
     * @return 返回一个数据权限请求拦截器实例
     */
    @Bean
    public DataPermissionRequestInterceptor dataPermissionRequestInterceptor() {
        return new DataPermissionRequestInterceptor();
    }

    /**
     * 创建数据权限RPC过滤器的Bean，并进行注册
     *
     * @return 返回一个FilterRegistrationBean实例，用于注册DataPermissionRpcWebFilter
     */
    @Bean
    public FilterRegistrationBean<DataPermissionRpcWebFilter> dataPermissionRpcFilter() {
        FilterRegistrationBean<DataPermissionRpcWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DataPermissionRpcWebFilter()); // 设置过滤器实例
        registrationBean.setOrder(TENANT_CONTEXT_FILTER - 1); // 设置过滤器的顺序，这里设置为在TENANT_CONTEXT_FILTER之前
        return registrationBean;
    }

}