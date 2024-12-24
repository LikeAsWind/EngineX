package org.nstep.engine.framework.apilog.config;

import org.nstep.engine.module.infra.api.logger.ApiAccessLogApi;
import org.nstep.engine.module.infra.api.logger.ApiErrorLogApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * API 日志使用到 Feign 的配置项
 * <p>
 * 该类用于配置 Feign 客户端，使其能够访问 API 日志相关的服务。
 * 通过 `@EnableFeignClients` 注解引入 `ApiAccessLogApi` 和 `ApiErrorLogApi` 两个 Feign 客户端接口。
 * 这些接口用于调用远程服务的 API 日志相关功能。
 */
@AutoConfiguration
@EnableFeignClients(clients = {ApiAccessLogApi.class, ApiErrorLogApi.class}) // 主要是引入相关的 API 服务
public class EngineApiLogRpcAutoConfiguration {
}
