package org.nstep.engine.module.system.api.permission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

/**
 * 角色相关的 RPC 服务接口
 * <p>
 * 该接口提供了校验角色是否合法的方法。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 角色")
public interface RoleApi {

    // 角色相关 API 前缀
    String PREFIX = ApiConstants.PREFIX + "/role";

    /**
     * 校验角色是否合法
     * <p>
     * 该方法用于校验一组角色编号是否有效。
     *
     * @param ids 角色编号数组
     * @return 校验结果，返回是否所有角色编号都是有效的
     */
    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验角色是否合法")
    @Parameter(name = "ids", description = "角色编号数组", example = "1,2", required = true)
    CommonResult<Boolean> validRoleList(@RequestParam("ids") Collection<Long> ids);

}
