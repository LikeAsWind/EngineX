package org.nstep.engine.module.infra.enums;

import org.nstep.engine.framework.common.enums.RpcConstants;

/**
 * API 相关的枚举
 * <p>
 * 该类定义了与 API 相关的常量，包括服务名、API 前缀以及版本号等。这些常量在系统中用于统一管理和调用相关的 API 接口。
 */
public class ApiConstants {

    /**
     * 服务名
     * <p>
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "infra-server";

    /**
     * API 前缀
     * <p>
     * 该常量用于定义 API 路径的前缀，通常与 RPC 通信相关。
     */
    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/infra";

    /**
     * API 版本
     * <p>
     * 定义了 API 的版本号，便于版本控制和管理。
     */
    public static final String VERSION = "1.0.0";
}
