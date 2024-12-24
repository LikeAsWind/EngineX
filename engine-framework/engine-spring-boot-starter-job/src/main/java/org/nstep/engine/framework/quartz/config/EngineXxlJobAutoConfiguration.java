package org.nstep.engine.framework.quartz.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * XXL-Job 自动配置类
 * <p>
 * 该配置类用于自动配置 XXL-Job 执行器，并根据配置文件中的 `xxl.job.enabled` 属性来决定是否启用 XXL-Job。
 * 通过 `@ConditionalOnClass` 和 `@ConditionalOnProperty` 注解，确保在相关依赖存在且配置启用时才会初始化 XXL-Job 执行器。
 * 同时，启用 Spring 的定时任务功能，确保 XXL-Job 的调度能够正常工作。
 */
@AutoConfiguration // 标记为自动配置类
@ConditionalOnClass(XxlJobSpringExecutor.class) // 仅当 XxlJobSpringExecutor 类存在时加载该配置类
@ConditionalOnProperty(prefix = "xxl.job", name = "enabled", havingValue = "true", matchIfMissing = true)
// 当配置文件中 xxl.job.enabled 为 true 时启用
@EnableConfigurationProperties({XxlJobProperties.class}) // 启用 XXL-Job 配置属性的绑定
@EnableScheduling // 开启 Spring 自带的定时任务功能
@Slf4j // 使用 Lombok 提供的日志功能
public class EngineXxlJobAutoConfiguration {

    /**
     * 配置 XXL-Job 执行器
     * <p>
     * 该方法会初始化 XXL-Job 执行器，并根据配置文件中的参数设置执行器的相关属性。
     * 如果配置文件中没有设置某些属性，则会使用默认值。
     *
     * @param properties 从配置文件中加载的 XXL-Job 配置属性
     * @return 配置好的 XXL-Job 执行器
     */
    @Bean
    @ConditionalOnMissingBean // 如果容器中没有指定的 Bean，则创建该 Bean
    public XxlJobExecutor xxlJobExecutor(XxlJobProperties properties) {
        log.info("[xxlJobExecutor][初始化 XXL-Job 执行器的配置]");

        // 获取配置中的 Admin 和 Executor 属性
        XxlJobProperties.AdminProperties admin = properties.getAdmin();
        XxlJobProperties.ExecutorProperties executor = properties.getExecutor();

        // 初始化 XXL-Job 执行器
        XxlJobExecutor xxlJobExecutor = new XxlJobSpringExecutor();
        xxlJobExecutor.setIp(executor.getIp()); // 设置执行器 IP
        xxlJobExecutor.setPort(executor.getPort()); // 设置执行器端口
        xxlJobExecutor.setAppname(executor.getAppName()); // 设置执行器应用名
        xxlJobExecutor.setLogPath(executor.getLogPath()); // 设置日志路径
        xxlJobExecutor.setLogRetentionDays(executor.getLogRetentionDays()); // 设置日志保留天数
        xxlJobExecutor.setAdminAddresses(admin.getAddresses()); // 设置 XXL-Job Admin 地址
        xxlJobExecutor.setAccessToken(properties.getAccessToken()); // 设置访问令牌
        return xxlJobExecutor;
    }

}
