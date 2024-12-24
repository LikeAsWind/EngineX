package org.nstep.engine.framework.tenant.core.mq.kafka;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 多租户的 Kafka {@link EnvironmentPostProcessor} 实现类
 * <p>
 * 在 Kafka Producer 发送消息时，动态添加 {@link TenantKafkaProducerInterceptor} 拦截器。
 * 该拦截器用于在消息发送过程中处理与租户相关的逻辑。
 * <p>
 * 通过实现 {@link EnvironmentPostProcessor} 接口，修改 Spring 环境配置，
 * 为 Kafka Producer 配置添加拦截器类。
 */
@Slf4j
public class TenantKafkaEnvironmentPostProcessor implements EnvironmentPostProcessor {

    // Kafka Producer 配置中用于存放拦截器类的属性键
    private static final String PROPERTY_KEY_INTERCEPTOR_CLASSES = "spring.kafka.producer.properties.interceptor.classes";

    /**
     * 在 Spring 环境中配置 Kafka 拦截器类，确保 {@link TenantKafkaProducerInterceptor} 被加入到 Kafka Producer 配置中。
     * <p>
     * 如果 `spring.kafka.producer.properties.interceptor.classes` 属性已经存在，
     * 则将 {@link TenantKafkaProducerInterceptor} 添加到该属性的值中。
     * 如果该属性为空，则直接将 {@link TenantKafkaProducerInterceptor} 作为唯一拦截器类。
     *
     * @param environment 当前的 Spring 环境
     * @param application Spring 应用程序
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            // 获取当前 Kafka Producer 配置中的拦截器类列表
            String value = environment.getProperty(PROPERTY_KEY_INTERCEPTOR_CLASSES);

            // 如果没有配置拦截器类，则将 TenantKafkaProducerInterceptor 作为唯一拦截器
            if (StrUtil.isEmpty(value)) {
                value = TenantKafkaProducerInterceptor.class.getName();
            } else {
                // 如果已有拦截器类，则将 TenantKafkaProducerInterceptor 添加到现有列表中
                value += "," + TenantKafkaProducerInterceptor.class.getName();
            }

            // 将更新后的拦截器类列表设置回环境配置
            environment.getSystemProperties().put(PROPERTY_KEY_INTERCEPTOR_CLASSES, value);
        } catch (NoClassDefFoundError ignore) {
            // 如果触发 NoClassDefFoundError 异常，说明 TenantKafkaProducerInterceptor 类不存在
            // 可能是因为未引入 kafka-spring 依赖，因此不进行任何操作
        }
    }
}
