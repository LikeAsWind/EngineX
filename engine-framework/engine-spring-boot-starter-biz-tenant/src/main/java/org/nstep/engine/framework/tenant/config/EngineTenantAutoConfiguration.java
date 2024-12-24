package org.nstep.engine.framework.tenant.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.nstep.engine.framework.common.enums.WebFilterOrderEnum;
import org.nstep.engine.framework.mybatis.core.util.MyBatisUtils;
import org.nstep.engine.framework.redis.config.EngineCacheProperties;
import org.nstep.engine.framework.tenant.core.aop.TenantIgnoreAspect;
import org.nstep.engine.framework.tenant.core.db.TenantDatabaseInterceptor;
import org.nstep.engine.framework.tenant.core.job.TenantJobAspect;
import org.nstep.engine.framework.tenant.core.mq.rabbitmq.TenantRabbitMQInitializer;
import org.nstep.engine.framework.tenant.core.mq.redis.TenantRedisMessageInterceptor;
import org.nstep.engine.framework.tenant.core.mq.rocketmq.TenantRocketMQInitializer;
import org.nstep.engine.framework.tenant.core.redis.TenantRedisCacheManager;
import org.nstep.engine.framework.tenant.core.security.TenantSecurityWebFilter;
import org.nstep.engine.framework.tenant.core.service.TenantFrameworkService;
import org.nstep.engine.framework.tenant.core.service.TenantFrameworkServiceImpl;
import org.nstep.engine.framework.tenant.core.web.TenantContextWebFilter;
import org.nstep.engine.framework.web.config.WebProperties;
import org.nstep.engine.framework.web.core.handler.GlobalExceptionHandler;
import org.nstep.engine.module.system.api.tenant.TenantApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

@AutoConfiguration
@ConditionalOnProperty(prefix = "engine.tenant", value = "enable", matchIfMissing = true)
// 通过 engine.tenant.enable 配置项控制是否启用多租户功能，默认启用（如果没有配置该项，则默认为 true）
@EnableConfigurationProperties(TenantProperties.class)
// 启用 TenantProperties 配置类，用于加载多租户相关的配置信息
public class EngineTenantAutoConfiguration {

    /**
     * 创建 TenantFrameworkService Bean
     *
     * @param tenantApi 租户相关的 API 接口，用于处理租户相关操作
     * @return TenantFrameworkService 实现类的实例
     */
    @Bean
    public TenantFrameworkService tenantFrameworkService(@Qualifier("org.nstep.engine.module.system.api.tenant.TenantApi")
                                                         TenantApi tenantApi) {
        return new TenantFrameworkServiceImpl(tenantApi);
    }

    // ========== AOP ==========

    /**
     * 创建 TenantIgnoreAspect Bean
     * 用于处理在 AOP 层面忽略租户信息的场景
     *
     * @return TenantIgnoreAspect 实例
     */
    @Bean
    public TenantIgnoreAspect tenantIgnoreAspect() {
        return new TenantIgnoreAspect();
    }

    // ========== DB ==========

    /**
     * 创建 TenantLineInnerInterceptor Bean
     * 用于 MyBatis 中添加租户信息的过滤逻辑
     *
     * @param properties  租户相关的配置属性
     * @param interceptor MybatisPlusInterceptor 用于 MyBatis 插件的配置
     * @return TenantLineInnerInterceptor 实例
     */
    @Bean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantProperties properties,
                                                                 MybatisPlusInterceptor interceptor) {
        TenantLineInnerInterceptor inner = new TenantLineInnerInterceptor(new TenantDatabaseInterceptor(properties));
        // 将该拦截器添加到 MybatisPlusInterceptor 中，确保它在分页插件之前执行
        MyBatisUtils.addInterceptor(interceptor, inner, 0);
        return inner;
    }

    // ========== WEB ==========

    /**
     * 创建 TenantContextWebFilter Bean
     * 用于 Web 层面处理租户上下文信息
     *
     * @return FilterRegistrationBean 配置的 TenantContextWebFilter 实例
     */
    @Bean
    public FilterRegistrationBean<TenantContextWebFilter> tenantContextWebFilter() {
        FilterRegistrationBean<TenantContextWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantContextWebFilter());
        registrationBean.setOrder(WebFilterOrderEnum.TENANT_CONTEXT_FILTER);
        return registrationBean;
    }

    // ========== Security ==========

    /**
     * 创建 TenantSecurityWebFilter Bean
     * 用于处理 Web 层面的租户安全相关逻辑
     *
     * @param tenantProperties       租户相关配置属性
     * @param webProperties          Web 配置属性
     * @param globalExceptionHandler 全局异常处理器
     * @param tenantFrameworkService 租户框架服务
     * @return FilterRegistrationBean 配置的 TenantSecurityWebFilter 实例
     */
    @Bean
    public FilterRegistrationBean<TenantSecurityWebFilter> tenantSecurityWebFilter(TenantProperties tenantProperties,
                                                                                   WebProperties webProperties,
                                                                                   GlobalExceptionHandler globalExceptionHandler,
                                                                                   TenantFrameworkService tenantFrameworkService) {
        FilterRegistrationBean<TenantSecurityWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantSecurityWebFilter(tenantProperties, webProperties,
                globalExceptionHandler, tenantFrameworkService));
        registrationBean.setOrder(WebFilterOrderEnum.TENANT_SECURITY_FILTER);
        return registrationBean;
    }

    // ========== Job ==========

    /**
     * 创建 TenantJobAspect Bean
     * 用于处理多租户相关的定时任务逻辑
     * 只有在类路径中存在 XxlJob 时才会注册此 Bean
     *
     * @param tenantFrameworkService 租户框架服务
     * @return TenantJobAspect 实例
     */
    @Bean
    @ConditionalOnClass(name = "com.xxl.job.core.handler.annotation.XxlJob")
    public TenantJobAspect tenantJobAspect(TenantFrameworkService tenantFrameworkService) {
        return new TenantJobAspect(tenantFrameworkService);
    }

    // ========== MQ ==========

    /**
     * 创建 TenantRabbitMQInitializer Bean
     * 用于处理多租户相关的 RabbitMQ 配置
     * 只有在类路径中存在 RabbitTemplate 时才会注册此 Bean
     *
     * @return TenantRabbitMQInitializer 实例
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.amqp.rabbit.core.RabbitTemplate")
    public TenantRabbitMQInitializer tenantRabbitMQInitializer() {
        return new TenantRabbitMQInitializer();
    }

    /**
     * 创建 TenantRocketMQInitializer Bean
     * 用于处理多租户相关的 RocketMQ 配置
     * 只有在类路径中存在 RocketMQTemplate 时才会注册此 Bean
     *
     * @return TenantRocketMQInitializer 实例
     */
    @Bean
    @ConditionalOnClass(name = "org.apache.rocketmq.spring.core.RocketMQTemplate")
    public TenantRocketMQInitializer tenantRocketMQInitializer() {
        return new TenantRocketMQInitializer();
    }

    /**
     * 创建 RedisCacheManager Bean
     * 用于多租户场景下的 Redis 缓存管理
     *
     * @param redisTemplate           Redis 操作模板
     * @param redisCacheConfiguration Redis 缓存配置
     * @param engineCacheProperties   缓存相关的配置属性
     * @param tenantProperties        租户相关配置属性
     * @return RedisCacheManager 实例
     */
    @Bean
    @Primary // 设置为主 Bean，当有多个 RedisCacheManager 时，优先使用该 Bean
    public RedisCacheManager tenantRedisCacheManager(RedisTemplate<String, Object> redisTemplate,
                                                     RedisCacheConfiguration redisCacheConfiguration,
                                                     EngineCacheProperties engineCacheProperties,
                                                     TenantProperties tenantProperties) {
        // 创建 RedisCacheWriter 对象，用于 Redis 缓存操作
        RedisConnectionFactory connectionFactory = Objects.requireNonNull(redisTemplate.getConnectionFactory());
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory,
                BatchStrategies.scan(engineCacheProperties.getRedisScanBatchSize()));
        // 创建 TenantRedisCacheManager 对象，用于多租户场景下的 Redis 缓存管理
        return new TenantRedisCacheManager(cacheWriter, redisCacheConfiguration, tenantProperties.getIgnoreCaches());
    }

    // ========== Redis ==========

    /**
     * 创建 TenantRedisMessageInterceptor Bean
     * 用于多租户环境下的 Redis 消息拦截处理
     * 只有在类路径中存在 RedisMQTemplate 时才会注册此 Bean
     */
    @Configuration
    @ConditionalOnClass(name = "org.nstep.engine.framework.mq.redis.core.RedisMQTemplate")
    public static class TenantRedisMQAutoConfiguration {

        @Bean
        public TenantRedisMessageInterceptor tenantRedisMessageInterceptor() {
            return new TenantRedisMessageInterceptor();
        }

    }
}
