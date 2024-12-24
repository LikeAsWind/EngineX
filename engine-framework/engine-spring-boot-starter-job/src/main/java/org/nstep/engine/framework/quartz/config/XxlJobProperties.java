package org.nstep.engine.framework.quartz.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * XXL-Job 配置类
 * <p>
 * 该类用于映射 XXL-Job 配置文件中的属性，并提供执行器和调度器的相关配置。
 * 配置项包括是否启用 XXL-Job、访问令牌、执行器配置和调度器配置。
 */
@ConfigurationProperties("xxl.job") // 从配置文件中加载以 "xxl.job" 为前缀的属性
@Validated // 启用验证，确保配置的合法性
@Data // 使用 Lombok 自动生成 getter、setter、toString 等方法
public class XxlJobProperties {

    /**
     * 是否开启 XXL-Job，默认为 true
     * <p>
     * 如果为 false，则禁用 XXL-Job 执行器。
     */
    private Boolean enabled = true;

    /**
     * 访问令牌，用于安全验证
     */
    private String accessToken;

    /**
     * 控制器配置，必须提供
     * <p>
     * 配置调度器的相关信息。
     */
    @NotNull(message = "控制器配置不能为空") // 控制器配置不能为空
    private AdminProperties admin;

    /**
     * 执行器配置，必须提供
     * <p>
     * 配置执行器的相关信息。
     */
    @NotNull(message = "执行器配置不能为空") // 执行器配置不能为空
    private ExecutorProperties executor;

    /**
     * XXL-Job 调度器配置类
     * <p>
     * 用于配置调度器的相关属性，如调度器地址。
     */
    @Data // 使用 Lombok 自动生成 getter、setter、toString 等方法
    @Valid // 验证该类中的属性
    public static class AdminProperties {

        /**
         * 调度器地址，不能为空
         * <p>
         * 用于配置 XXL-Job Admin 的地址，多个地址用逗号分隔。
         */
        @NotEmpty(message = "调度器地址不能为空") // 调度器地址不能为空
        private String addresses;

    }

    /**
     * XXL-Job 执行器配置类
     * <p>
     * 用于配置执行器的相关属性，如应用名、IP、端口、日志路径等。
     */
    @Data // 使用 Lombok 自动生成 getter、setter、toString 等方法
    @Valid // 验证该类中的属性
    public static class ExecutorProperties {

        /**
         * 默认端口
         * <p>
         * 如果需要随机端口，设置为 -1。
         */
        private static final Integer PORT_DEFAULT = -1;

        /**
         * 默认日志保留天数
         * <p>
         * 如果想永久保留日志，则设置为 -1。
         */
        private static final Integer LOG_RETENTION_DAYS_DEFAULT = 30;

        /**
         * 应用名，不能为空
         * <p>
         * 用于标识执行器所属的应用。
         */
        @NotEmpty(message = "应用名不能为空") // 应用名不能为空
        private String appName;

        /**
         * 执行器的 IP 地址
         * <p>
         * 如果为空，则使用默认值。
         */
        private String ip;

        /**
         * 执行器的端口，默认为 -1（随机端口）
         * <p>
         * 如果不指定端口，将使用随机端口。
         */
        private Integer port = PORT_DEFAULT;

        /**
         * 日志路径，不能为空
         * <p>
         * 用于指定日志文件存储的路径。
         */
        @NotEmpty(message = "日志地址不能为空") // 日志地址不能为空
        private String logPath;

        /**
         * 日志保留天数，默认为 30 天
         * <p>
         * 如果想永久保留日志，设置为 -1。
         */
        private Integer logRetentionDays = LOG_RETENTION_DAYS_DEFAULT;

    }

}
