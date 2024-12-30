package org.nstep.engine.framework.xxljob.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XXL-JOB 配置类
 * <p>
 * 该类负责配置 XXL-JOB 执行器的相关参数，并通过 `@Bean` 注解将配置的执行器对象暴露给 Spring 容器管理。
 * 配置项包括 XXL-JOB 管理平台地址、执行器名称、IP 地址、端口、日志路径等。
 */
@Configuration  // 表明这是一个配置类，Spring 会自动加载该类中的 Bean 配置
public class XxlJobConfig {

    // 从配置文件中读取 XXL-JOB 相关配置
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;  // XXL-JOB 管理平台的地址

    @Value("${xxl.job.executor.appname}")
    private String appName;  // 执行器的应用名称

    @Value("${xxl.job.executor.ip}")
    private String ip;  // 执行器的 IP 地址

    @Value("${xxl.job.executor.port}")
    private int port;  // 执行器的端口号

    @Value("${xxl.job.accessToken}")
    private String accessToken;  // 访问令牌，用于认证

    @Value("${xxl.job.executor.logpath}")
    private String logPath;  // 执行器的日志路径

    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;  // 日志保留天数

    /**
     * 配置并返回一个 XxlJobSpringExecutor 实例
     * <p>
     * 该方法通过读取配置文件中的参数，初始化 `XxlJobSpringExecutor`，并将其注册为 Spring 容器中的一个 Bean。
     * 通过该执行器，Spring Boot 项目可以与 XXL-JOB 管理平台进行交互，执行定时任务。
     *
     * @return 配置好的 XxlJobSpringExecutor 实例
     */
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        // 创建 XxlJobSpringExecutor 执行器实例
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();

        // 设置执行器的相关属性
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);  // 设置 XXL-JOB 管理平台地址
        xxlJobSpringExecutor.setAppname(appName);  // 设置执行器应用名称
        xxlJobSpringExecutor.setIp(ip);  // 设置执行器 IP 地址
        xxlJobSpringExecutor.setPort(port);  // 设置执行器端口号
        xxlJobSpringExecutor.setAccessToken(accessToken);  // 设置访问令牌
        xxlJobSpringExecutor.setLogPath(logPath);  // 设置日志路径
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);  // 设置日志保留天数

        // 返回配置好的执行器实例
        return xxlJobSpringExecutor;
    }
}
