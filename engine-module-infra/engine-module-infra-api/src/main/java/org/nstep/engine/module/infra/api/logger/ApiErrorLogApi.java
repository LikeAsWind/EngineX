package org.nstep.engine.module.infra.api.logger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.infra.api.logger.dto.ApiErrorLogCreateReqDTO;
import org.nstep.engine.module.infra.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * RPC 服务 - API 异常日志接口
 * <p>
 * 该接口定义了用于创建 API 异常日志的 RPC 服务方法，包括同步和异步两种方式。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - API 异常日志")
public interface ApiErrorLogApi {

    String PREFIX = ApiConstants.PREFIX + "/api-error-log";

    /**
     * 创建 API 异常日志
     * <p>
     * 该方法用于同步创建 API 异常日志，传入日志请求 DTO，返回操作结果。
     *
     * @param createDTO API 异常日志创建请求 DTO
     * @return 操作结果，成功为 true，失败为 false
     */
    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建 API 异常日志")
    CommonResult<Boolean> createApiErrorLog(@Valid @RequestBody ApiErrorLogCreateReqDTO createDTO);

    /**
     * 【异步】创建 API 异常日志
     * <p>
     * 该方法用于异步创建 API 异常日志，适用于不需要立即返回结果的场景。
     *
     * @param createDTO 异常日志 DTO
     */
    @Async
    default void createApiErrorLogAsync(ApiErrorLogCreateReqDTO createDTO) {
        // 调用同步方法并检查结果
        createApiErrorLog(createDTO).checkError();
    }
}
