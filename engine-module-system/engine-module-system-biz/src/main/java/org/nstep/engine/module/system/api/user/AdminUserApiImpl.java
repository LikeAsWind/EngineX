package org.nstep.engine.module.system.api.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.framework.datapermission.core.annotation.DataPermission;
import org.nstep.engine.module.system.api.user.dto.AdminUserRespDTO;
import org.nstep.engine.module.system.dal.dataobject.dept.DeptDO;
import org.nstep.engine.module.system.dal.dataobject.user.AdminUserDO;
import org.nstep.engine.module.system.service.dept.DeptService;
import org.nstep.engine.module.system.service.user.AdminUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;
import static org.nstep.engine.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * 管理员用户 API 实现类
 * <p>
 * 该类提供了管理员用户相关的 RESTful API 接口，
 * 包括获取用户信息、获取用户列表等功能。
 * 提供给 Feign 调用，处理用户相关的业务逻辑。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class AdminUserApiImpl implements AdminUserApi {

    @Resource
    private AdminUserService userService; // 管理员用户服务，处理用户相关的业务逻辑
    @Resource
    private DeptService deptService; // 部门服务，处理部门相关的业务逻辑

    /**
     * 获取指定 ID 的管理员用户信息
     *
     * @param id 用户 ID
     * @return 管理员用户信息
     */
    @Override
    public CommonResult<AdminUserRespDTO> getUser(Long id) {
        AdminUserDO user = userService.getUser(id);
        return success(BeanUtils.toBean(user, AdminUserRespDTO.class));
    }

    /**
     * 获取指定用户负责的部门下的所有用户（包括子部门用户）
     *
     * @param id 用户 ID
     * @return 用户列表
     */
    @Override
    public CommonResult<List<AdminUserRespDTO>> getUserListBySubordinate(Long id) {
        // 1.1 获取用户负责的部门
        AdminUserDO user = userService.getUser(id);
        if (user == null) {
            return success(Collections.emptyList());
        }
        ArrayList<Long> deptIds = new ArrayList<>();
        DeptDO dept = deptService.getDept(user.getDeptId());
        if (dept == null) {
            return success(Collections.emptyList());
        }
        if (ObjUtil.notEqual(dept.getLeaderUserId(), id)) { // 校验为负责人
            return success(Collections.emptyList());
        }
        deptIds.add(dept.getId());
        // 1.2 获取所有子部门
        List<DeptDO> childDeptList = deptService.getChildDeptList(dept.getId());
        if (CollUtil.isNotEmpty(childDeptList)) {
            deptIds.addAll(convertSet(childDeptList, DeptDO::getId));
        }

        // 2. 获取部门对应的用户信息
        List<AdminUserDO> users = userService.getUserListByDeptIds(deptIds);
        users.removeIf(item -> ObjUtil.equal(item.getId(), id)); // 排除自己
        return success(BeanUtils.toBean(users, AdminUserRespDTO.class));
    }

    /**
     * 获取指定用户 ID 列表的管理员用户信息
     *
     * @param ids 用户 ID 列表
     * @return 用户列表
     */
    @Override
    @DataPermission(enable = false) // 禁用数据权限。原因是，一般基于指定 id 的 API 查询，都是数据拼接为主
    public CommonResult<List<AdminUserRespDTO>> getUserList(Collection<Long> ids) {
        List<AdminUserDO> users = userService.getUserList(ids);
        return success(BeanUtils.toBean(users, AdminUserRespDTO.class));
    }

    /**
     * 获取指定部门 ID 列表的管理员用户信息
     *
     * @param deptIds 部门 ID 列表
     * @return 用户列表
     */
    @Override
    public CommonResult<List<AdminUserRespDTO>> getUserListByDeptIds(Collection<Long> deptIds) {
        List<AdminUserDO> users = userService.getUserListByDeptIds(deptIds);
        return success(BeanUtils.toBean(users, AdminUserRespDTO.class));
    }

    /**
     * 获取指定岗位 ID 列表的管理员用户信息
     *
     * @param postIds 岗位 ID 列表
     * @return 用户列表
     */
    @Override
    public CommonResult<List<AdminUserRespDTO>> getUserListByPostIds(Collection<Long> postIds) {
        List<AdminUserDO> users = userService.getUserListByPostIds(postIds);
        return success(BeanUtils.toBean(users, AdminUserRespDTO.class));
    }

    /**
     * 验证用户列表的有效性
     *
     * @param ids 用户 ID 列表
     * @return 验证结果
     */
    @Override
    public CommonResult<Boolean> validateUserList(Collection<Long> ids) {
        userService.validateUserList(ids);
        return success(true);
    }

}
