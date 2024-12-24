package org.nstep.engine.framework.env.core.util;

import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.nstep.engine.framework.env.config.EnvProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.Objects;

/**
 * 环境 Utils 类
 * <p>
 * 提供与环境相关的工具方法，主要用于获取和设置环境标签（tag），以及获取主机名等信息。
 */
public class EnvUtils {

    // 环境变量标识，表示需要解析为本地主机名
    public static final String HOST_NAME_VALUE = "${HOSTNAME}";

    // 请求头中用于传递 tag 的字段名称
    private static final String HEADER_TAG = "tag";

    /**
     * 从 HTTP 请求中获取 tag。
     * 如果 tag 的值是 "${HOSTNAME}"，则解析为本地主机名。
     * 这种特殊逻辑用于解决在开发工具（如 IDEA Rest Client）中无法读取环境变量的情况。
     *
     * @param request HTTP 请求对象
     * @return 请求中的 tag 或本地主机名
     */
    public static String getTag(HttpServletRequest request) {
        // 获取请求头中的 tag
        String tag = request.getHeader(HEADER_TAG);

        // 如果 tag 的值是 "${HOSTNAME}"，则返回本地主机名
        return Objects.equals(tag, HOST_NAME_VALUE) ? getHostName() : tag;
    }

    /**
     * 从服务实例的元数据中获取 tag。
     *
     * @param instance 服务实例
     * @return 服务实例的 tag
     */
    public static String getTag(ServiceInstance instance) {
        return instance.getMetadata().get(HEADER_TAG);
    }

    /**
     * 从 Spring 环境中获取 tag。
     * 如果 tag 的值是 "${HOSTNAME}"，则解析为本地主机名。
     * 这种特殊逻辑用于解决在开发工具（如 IDEA Rest Client）中无法读取环境变量的情况。
     *
     * @param environment Spring 环境对象
     * @return 环境中的 tag 或本地主机名
     */
    public static String getTag(Environment environment) {
        // 获取环境中配置的 tag
        String tag = environment.getProperty(EnvProperties.TAG_KEY);

        // 如果 tag 的值是 "${HOSTNAME}"，则返回本地主机名
        return Objects.equals(tag, HOST_NAME_VALUE) ? getHostName() : tag;
    }

    /**
     * 将 tag 设置到 Feign 请求的请求模板中。
     * 通过设置请求头中的 tag 字段，传递环境标签。
     *
     * @param requestTemplate Feign 请求模板
     * @param tag             环境标签
     */
    public static void setTag(RequestTemplate requestTemplate, String tag) {
        // 设置请求头中的 tag 字段
        requestTemplate.header(HEADER_TAG, tag);
    }

    /**
     * 获取本地主机名。
     * 通过 InetAddress 获取当前机器的主机名。
     *
     * @return 本地主机名
     */
    @SneakyThrows
    public static String getHostName() {
        return InetAddress.getLocalHost().getHostName();
    }

}
