package org.nstep.engine.framework.dict.config;

import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.system.api.dict.DictDataApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 字典模块的 Feign 配置自动化配置类。
 * <p>
 * 通过自动配置启用 Feign 客户端 {@link DictDataApi}，用于远程调用字典服务。
 */
@AutoConfiguration
@EnableFeignClients(clients = DictDataApi.class)
@Slf4j
public class EngineDictRpcAutoConfiguration {

    public EngineDictRpcAutoConfiguration() {
        log.info("[EngineDictRpcAutoConfiguration] Feign 客户端 DictDataApi 已启用");
    }
}