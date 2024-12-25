package org.nstep.engine.module.system.api.permission;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import org.nstep.engine.module.system.service.permission.PermissionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Set;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 权限管理 API 实现类
 * <p>
 * 该类提供了权限相关的 RESTful API 接口，包括根据角色 ID 获取用户角色 ID 列表、检查用户是否具有指定权限或角色、获取用户的部门数据权限等。
 * 提供给 Feign 调用，处理与权限相关的业务逻辑。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class PermissionApiImpl implements PermissionApi {

    @Resource
    private PermissionService permissionService; // 权限服务，处理与权限相关的业务逻辑

    /**
     * 根据角色 ID 列表获取用户角色 ID 列表
     *
     * @param roleIds 角色 ID 列表
     * @return 用户角色 ID 列表
     */
    @Override
    public CommonResult<Set<Long>> getUserRoleIdListByRoleIds(Collection<Long> roleIds) {
        // 调用权限服务获取用户角色 ID 列表
        return success(permissionService.getUserRoleIdListByRoleId(roleIds));
    }

    /**
     * 检查用户是否拥有指定的权限
     *
     * @param userId      用户 ID
     * @param permissions 权限列表
     * @return 是否拥有指定权限
     */
    @Override
    public CommonResult<Boolean> hasAnyPermissions(Long userId, String... permissions) {
        // 调用权限服务检查用户是否拥有指定权限
        return success(permissionService.hasAnyPermissions(userId, permissions));
    }

    /**
     * 检查用户是否拥有指定的角色
     *
     * @param userId 用户 ID
     * @param roles  角色列表
     * @return 是否拥有指定角色
     */
    @Override
    public CommonResult<Boolean> hasAnyRoles(Long userId, String... roles) {
        // 调用权限服务检查用户是否拥有指定角色
        return success(permissionService.hasAnyRoles(userId, roles));
    }

    /**
     * 获取用户的部门数据权限
     *
     * @param userId 用户 ID
     * @return 用户的部门数据权限
     */
    @Override
    public CommonResult<DeptDataPermissionRespDTO> getDeptDataPermission(Long userId) {
        // 调用权限服务获取用户的部门数据权限
        return success(permissionService.getDeptDataPermission(userId));
    }

}
