package org.nstep.engine.module.system.api.permission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Set;

/**
 * 权限相关的 RPC 服务接口
 * <p>
 * 该接口提供了一些方法，用于判断用户是否具有某些角色或权限，并获取用户的部门数据权限。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 权限")
public interface PermissionApi {

    // 权限相关 API 前缀
    String PREFIX = ApiConstants.PREFIX + "/permission";

    /**
     * 根据角色编号集合获取拥有多个角色的用户编号集合
     * <p>
     * 该方法接收一组角色编号，并返回拥有这些角色的用户编号集合。
     *
     * @param roleIds 角色编号集合
     * @return 拥有指定角色的用户编号集合
     */
    @GetMapping(PREFIX + "/user-role-id-list-by-role-id")
    @Operation(summary = "获得拥有多个角色的用户编号集合")
    @Parameter(name = "roleIds", description = "角色编号集合", example = "1,2", required = true)
    CommonResult<Set<Long>> getUserRoleIdListByRoleIds(@RequestParam("roleIds") Collection<Long> roleIds);

    /**
     * 判断用户是否有任意一个权限
     * <p>
     * 该方法用于判断某个用户是否具有给定的任意一个权限。
     *
     * @param userId      用户编号
     * @param permissions 权限集合
     * @return 是否具有任意一个权限
     */
    @GetMapping(PREFIX + "/has-any-permissions")
    @Operation(summary = "判断是否有权限，任一一个即可")
    @Parameters({
            @Parameter(name = "userId", description = "用户编号", example = "1", required = true),
            @Parameter(name = "permissions", description = "权限", example = "read,write", required = true)
    })
    CommonResult<Boolean> hasAnyPermissions(@RequestParam("userId") Long userId,
                                            @RequestParam("permissions") String... permissions);

    /**
     * 判断用户是否有任意一个角色
     * <p>
     * 该方法用于判断某个用户是否具有给定的任意一个角色。
     *
     * @param userId 用户编号
     * @param roles  角色数组
     * @return 是否具有任意一个角色
     */
    @GetMapping(PREFIX + "/has-any-roles")
    @Operation(summary = "判断是否有角色，任一一个即可")
    @Parameters({
            @Parameter(name = "userId", description = "用户编号", example = "1", required = true),
            @Parameter(name = "roles", description = "角色数组", example = "2", required = true)
    })
    CommonResult<Boolean> hasAnyRoles(@RequestParam("userId") Long userId,
                                      @RequestParam("roles") String... roles);

    /**
     * 获取登录用户的部门数据权限
     * <p>
     * 该方法用于获取指定用户的部门数据权限，包括是否可以查看全部数据、自己的数据和可查看的部门编号。
     *
     * @param userId 用户编号
     * @return 用户的部门数据权限信息
     */
    @GetMapping(PREFIX + "/get-dept-data-permission")
    @Operation(summary = "获得登陆用户的部门数据权限")
    @Parameter(name = "userId", description = "用户编号", example = "2", required = true)
    CommonResult<DeptDataPermissionRespDTO> getDeptDataPermission(@RequestParam("userId") Long userId);

}
