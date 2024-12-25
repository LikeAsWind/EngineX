package org.nstep.engine.module.infra.api.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.infra.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign 客户端接口，用于与参数配置服务进行通信
 * <p>
 * 该接口提供了与参数配置相关的 RPC 调用，使用 Feign 客户端与服务进行交互。
 * <p>
 * 使用 Feign 时，可以通过 @FeignClient 注解指定服务名，并通过方法注解指定 HTTP 请求的路径和参数。
 * <p>
 * 示例：通过指定参数键查询对应的参数值。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory = 配置回退工厂，处理失败情况
@Tag(name = "RPC 服务 - 参数配置") // 用于文档生成的标签，表示该接口属于参数配置服务
public interface ConfigApi {

    // 参数配置的路径前缀
    String PREFIX = ApiConstants.PREFIX + "/config";

    /**
     * 根据参数键查询参数值
     * <p>
     * 该方法用于根据传入的参数键查询对应的参数值。返回值为字符串类型。
     *
     * @param key 参数键
     * @return 包装了参数值的 CommonResult 对象
     */
    @GetMapping(PREFIX + "/get-value-by-key")
    @Operation(summary = "根据参数键查询参数值")
    // OpenAPI 注解，提供接口文档说明
    CommonResult<String> getConfigValueByKey(@RequestParam("key") String key);

}
