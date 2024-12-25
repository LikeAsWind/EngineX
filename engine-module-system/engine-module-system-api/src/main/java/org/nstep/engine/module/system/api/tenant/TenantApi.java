package org.nstep.engine.module.system.api.tenant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 多租户的 RPC 服务接口
 * <p>
 * 该接口提供了与多租户相关的 API，包括获取所有租户编号和校验租户是否合法等功能。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 多租户")
public interface TenantApi {

    // 定义请求的前缀
    String PREFIX = ApiConstants.PREFIX + "/tenant";

    /**
     * 获得所有租户编号
     * <p>
     * 用于获取系统中所有租户的编号列表。
     *
     * @return 返回所有租户编号的列表
     */
    @GetMapping(PREFIX + "/id-list")
    @Operation(summary = "获得所有租户编号")
    CommonResult<List<Long>> getTenantIdList();

    /**
     * 校验租户是否合法
     * <p>
     * 用于校验指定租户编号是否合法。
     *
     * @param id 租户编号
     * @return 返回租户是否合法的结果
     */
    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验租户是否合法")
    @Parameter(name = "id", description = "租户编号", required = true, example = "1024")
    CommonResult<Boolean> validTenant(@RequestParam("id") Long id);

}
