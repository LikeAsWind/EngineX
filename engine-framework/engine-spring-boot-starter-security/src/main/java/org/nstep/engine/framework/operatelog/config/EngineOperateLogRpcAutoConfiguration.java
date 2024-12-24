package org.nstep.engine.framework.operatelog.config;

import org.nstep.engine.module.system.api.logger.OperateLogApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * OperateLog 使用到 Feign 的配置项
 * <p>
 * 该类用于配置与操作日志相关的 Feign 客户端。通过 `@EnableFeignClients` 注解引入相关的 API 服务，
 * 使得应用能够通过 Feign 调用远程操作日志服务的接口。
 * </p>
 */
@AutoConfiguration // 标记为自动配置类，Spring Boot 会自动扫描并加载该配置
@EnableFeignClients(clients = {OperateLogApi.class}) // 启用 Feign 客户端，指定需要引入的 OperateLogApi 接口
public class EngineOperateLogRpcAutoConfiguration {
    // 该类的主要作用是通过 Feign 配置远程调用操作日志服务的 API 接口
}
