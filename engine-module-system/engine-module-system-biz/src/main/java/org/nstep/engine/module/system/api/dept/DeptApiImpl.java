package org.nstep.engine.module.system.api.dept;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.system.api.dept.dto.DeptRespDTO;
import org.nstep.engine.module.system.dal.dataobject.dept.DeptDO;
import org.nstep.engine.module.system.service.dept.DeptService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 部门 API 实现类
 * <p>
 * 该类提供了部门相关的 RESTful API 接口，供 Feign 调用。
 * 主要功能包括获取部门信息、获取部门列表、验证部门有效性等。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class DeptApiImpl implements DeptApi {

    @Resource
    private DeptService deptService; // 部门服务，处理部门相关的业务逻辑

    /**
     * 获取指定部门的信息
     *
     * @param id 部门 ID
     * @return 部门信息的响应结果
     */
    @Override
    public CommonResult<DeptRespDTO> getDept(Long id) {
        // 获取部门数据对象
        DeptDO dept = deptService.getDept(id);
        // 将部门数据对象转换为响应 DTO 并返回
        return success(BeanUtils.toBean(dept, DeptRespDTO.class));
    }

    /**
     * 获取多个部门的信息
     *
     * @param ids 部门 ID 列表
     * @return 部门信息的响应结果
     */
    @Override
    public CommonResult<List<DeptRespDTO>> getDeptList(Collection<Long> ids) {
        // 获取多个部门数据对象
        List<DeptDO> depts = deptService.getDeptList(ids);
        // 将部门数据对象列表转换为响应 DTO 列表并返回
        return success(BeanUtils.toBean(depts, DeptRespDTO.class));
    }

    /**
     * 校验多个部门是否有效
     *
     * @param ids 部门 ID 列表
     * @return 校验结果的响应
     */
    @Override
    public CommonResult<Boolean> validateDeptList(Collection<Long> ids) {
        // 调用部门服务进行校验
        deptService.validateDeptList(ids);
        // 返回校验成功的结果
        return success(true);
    }

    /**
     * 获取指定部门的所有子部门
     *
     * @param id 部门 ID
     * @return 子部门信息的响应结果
     */
    @Override
    public CommonResult<List<DeptRespDTO>> getChildDeptList(Long id) {
        // 获取指定部门的子部门数据对象
        List<DeptDO> depts = deptService.getChildDeptList(id);
        // 将子部门数据对象列表转换为响应 DTO 列表并返回
        return success(BeanUtils.toBean(depts, DeptRespDTO.class));
    }

}
