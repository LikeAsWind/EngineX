package org.nstep.engine.module.infra.api.logger;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.infra.api.logger.dto.ApiErrorLogCreateReqDTO;
import org.nstep.engine.module.infra.service.logger.ApiErrorLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * API 异常日志 API 实现类
 * <p>
 * 该类实现了 ApiErrorLogApi 接口，提供了 API 异常日志相关的 RESTful API 接口。
 * 通过 Feign 调用该接口时，可以创建 API 异常日志。
 * </p>
 */
@RestController // 提供 RESTful API 接口，供 Feign 调用
@Validated // 开启 Spring 的参数校验功能
public class ApiErrorLogApiImpl implements ApiErrorLogApi {

    @Resource
    private ApiErrorLogService apiErrorLogService; // 注入 ApiErrorLogService，用于处理 API 异常日志的业务逻辑

    /**
     * 创建 API 异常日志
     *
     * @param createDTO API 异常日志创建请求 DTO，包含日志的详细信息
     * @return 返回操作结果，成功则返回 true
     */
    @Override
    public CommonResult<Boolean> createApiErrorLog(ApiErrorLogCreateReqDTO createDTO) {
        // 调用 ApiErrorLogService 创建 API 异常日志
        apiErrorLogService.createApiErrorLog(createDTO);
        // 返回成功结果
        return success(true);
    }

}
