package org.nstep.engine.gateway.util;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpHeaders;

import java.util.Objects;

/**
 * 环境工具类，提供与环境相关的操作
 * <p>
 * 本类包含与环境配置和信息获取相关的工具方法。特别是提供获取环境标记（tag）和主机名的功能。
 * - 从请求头中获取 tag 值，若为 "${HOSTNAME}"，则解析为当前本地主机名。
 * - 提供从服务实例获取 tag 值的功能。
 * - 提供获取本地主机名的方法，若无法获取，则返回一个 UUID。
 * <p>
 * 该类用于解决环境变量无法直接在某些工具中读取的问题（如 IDEA Rest Client），通过服务器端处理来获取主机名。
 */
public class EnvUtils {

    // 常量：表示环境变量中的主机名占位符
    public static final String HOST_NAME_VALUE = "${HOSTNAME}";
    // 常量：用于标记请求头中的 tag
    private static final String HEADER_TAG = "tag";

    /**
     * 从请求头中获取 tag 值
     * <p>
     * 如果请求头中的 tag 为 "${HOSTNAME}"，则返回当前本地主机名。
     *
     * @param headers 请求头
     * @return tag 值，若是 "${HOSTNAME}" 则返回主机名
     */
    public static String getTag(HttpHeaders headers) {
        String tag = headers.getFirst(HEADER_TAG);
        // 如果请求的是 "${HOSTNAME}"，则解析成对应的本地主机名
        // 目的是处理 IDEA Rest Client 不支持环境变量的读取，服务器来做解析
        return Objects.equals(tag, HOST_NAME_VALUE) ? getHostName() : tag;
    }

    /**
     * 从服务实例的元数据中获取 tag 值
     *
     * @param instance 服务实例
     * @return tag 值
     */
    public static String getTag(ServiceInstance instance) {
        return instance.getMetadata().get(HEADER_TAG);
    }

    /**
     * 获取当前本地主机名
     * <p>
     * 如果无法获取主机名，则返回一个生成的 UUID 作为备用。
     *
     * @return 当前本地主机名，若无法获取则返回 UUID
     */
    public static String getHostName() {
        return StrUtil.blankToDefault(NetUtil.getLocalHostName(), IdUtil.fastSimpleUUID());
    }

}
