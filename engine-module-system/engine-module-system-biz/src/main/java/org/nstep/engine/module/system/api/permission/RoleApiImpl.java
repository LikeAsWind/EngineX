package org.nstep.engine.module.system.api.permission;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.service.permission.RoleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 角色管理 API 实现类
 * <p>
 * 该类提供了角色相关的 RESTful API 接口，主要用于验证角色列表的有效性。
 * 提供给 Feign 调用，处理与角色相关的业务逻辑。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class RoleApiImpl implements RoleApi {

    @Resource
    private RoleService roleService; // 角色服务，处理与角色相关的业务逻辑

    /**
     * 验证角色 ID 列表是否有效
     *
     * @param ids 角色 ID 列表
     * @return 是否验证成功
     */
    @Override
    public CommonResult<Boolean> validRoleList(Collection<Long> ids) {
        // 调用角色服务验证角色 ID 列表
        roleService.validateRoleList(ids);
        return success(true);
    }
}
