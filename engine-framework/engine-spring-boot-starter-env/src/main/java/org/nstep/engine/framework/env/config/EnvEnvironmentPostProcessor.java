package org.nstep.engine.framework.env.config;

import cn.hutool.core.util.StrUtil;
import org.nstep.engine.framework.common.util.collection.SetUtils;
import org.nstep.engine.framework.env.core.util.EnvUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Set;

import static org.nstep.engine.framework.env.core.util.EnvUtils.HOST_NAME_VALUE;

/**
 * 多环境的 {@link EnvEnvironmentPostProcessor} 实现类
 * <p>
 * 该类实现了 {@link EnvironmentPostProcessor} 接口，用于在 Spring 环境配置处理之后，动态地修改环境配置。
 * 它的作用是将 `engine.env.tag` 设置到 nacos 等组件的对应 tag 配置项，前提是这些配置项还不存在。
 */
public class EnvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    // 目标 tag 配置项的键列表，包含 nacos 注册中心的 tag 配置项
    private static final Set<String> TARGET_TAG_KEYS = SetUtils.asSet(
            "spring.cloud.nacos.discovery.metadata.tag" // nacos 注册中心的 tag 配置项
            // 未来可以扩展支持更多的配置项，例如 MQ 等
    );

    /**
     * 在 Spring 应用环境配置处理之后执行，用于修改环境配置。
     * <p>
     * 该方法首先会设置 ${HOST_NAME} 兜底的环境变量，然后检查 `engine.env.tag` 是否为空，
     * 如果不为空，则将该 tag 设置到 nacos 等组件的 tag 配置项中（仅当它们不存在时）。
     *
     * @param environment 当前的 Spring 环境配置
     * @param application 当前的 Spring 应用实例
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 0. 设置 ${HOST_NAME} 兜底的环境变量
        // 提取 HOST_NAME 的配置项键（例如：{HOST_NAME} 会变成 HOST_NAME）
        String hostNameKey = StrUtil.subBetween(HOST_NAME_VALUE, "{", "}");

        // 如果环境中没有该键的配置，则将其设置为当前主机名
        if (!environment.containsProperty(hostNameKey)) {
            environment.getSystemProperties().put(hostNameKey, EnvUtils.getHostName());
        }

        // 1.1 检查是否有 engine.env.tag 配置项，如果没有则不进行后续操作
        // 获取 `engine.env.tag` 配置项的值
        String tag = EnvUtils.getTag(environment);

        // 如果 tag 为空，则不做任何修改
        if (StrUtil.isEmpty(tag)) {
            return;
        }

        // 1.2 遍历目标配置项键列表，检查并修改对应的 tag 配置项
        for (String targetTagKey : TARGET_TAG_KEYS) {
            // 获取目标配置项的当前值
            String targetTagValue = environment.getProperty(targetTagKey);

            // 如果目标配置项已有值，则跳过该项
            if (StrUtil.isNotEmpty(targetTagValue)) {
                continue;
            }

            // 如果目标配置项没有值，则设置为 `engine.env.tag` 的值
            environment.getSystemProperties().put(targetTagKey, tag);
        }
    }

}
