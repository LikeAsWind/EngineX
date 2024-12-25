package org.nstep.engine.module.system.api.logger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.logger.dto.LoginLogCreateReqDTO;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 登录日志服务接口
 * <p>
 * 该接口提供了用于创建登录日志的功能。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 登录日志")
public interface LoginLogApi {

    String PREFIX = ApiConstants.PREFIX + "/login-log";

    /**
     * 创建登录日志
     * <p>
     * 该方法接收一个登录日志创建请求，保存登录日志信息。
     *
     * @param reqDTO 登录日志创建请求数据
     * @return 创建结果，返回一个布尔值表示是否成功
     */
    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建登录日志")
    CommonResult<Boolean> createLoginLog(@Valid @RequestBody LoginLogCreateReqDTO reqDTO);

}
