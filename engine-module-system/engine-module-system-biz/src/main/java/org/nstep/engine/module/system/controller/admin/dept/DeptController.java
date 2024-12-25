package org.nstep.engine.module.system.controller.admin.dept;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.enums.CommonStatusEnum;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import org.nstep.engine.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import org.nstep.engine.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import org.nstep.engine.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import org.nstep.engine.module.system.dal.dataobject.dept.DeptDO;
import org.nstep.engine.module.system.service.dept.DeptService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 部门控制器
 * <p>
 * 该控制器处理与部门相关的所有操作，包括创建、更新、删除部门以及获取部门信息等。
 * 所有操作均需要进行权限验证，确保用户具有相应的权限。
 */
@Tag(name = "管理后台 - 部门")
@RestController
@RequestMapping("/system/dept")
@Validated
public class DeptController {

    @Resource
    private DeptService deptService;

    /**
     * 创建部门
     * <p>
     * 根据传入的部门创建请求数据创建一个新部门，并返回部门的 ID。
     *
     * @param createReqVO 部门创建请求数据
     * @return 创建成功后返回部门的 ID
     */
    @PostMapping("create")
    @Operation(summary = "创建部门")
    @PreAuthorize("@ss.hasPermission('system:dept:create')")
    public CommonResult<Long> createDept(@Valid @RequestBody DeptSaveReqVO createReqVO) {
        Long deptId = deptService.createDept(createReqVO);
        return success(deptId);
    }

    /**
     * 更新部门
     * <p>
     * 根据传入的部门更新请求数据更新已有部门的信息。
     *
     * @param updateReqVO 部门更新请求数据
     * @return 更新成功返回 true
     */
    @PutMapping("update")
    @Operation(summary = "更新部门")
    @PreAuthorize("@ss.hasPermission('system:dept:update')")
    public CommonResult<Boolean> updateDept(@Valid @RequestBody DeptSaveReqVO updateReqVO) {
        deptService.updateDept(updateReqVO);
        return success(true);
    }

    /**
     * 删除部门
     * <p>
     * 根据部门 ID 删除部门。
     *
     * @param id 部门的 ID
     * @return 删除成功返回 true
     */
    @DeleteMapping("delete")
    @Operation(summary = "删除部门")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public CommonResult<Boolean> deleteDept(@RequestParam("id") Long id) {
        deptService.deleteDept(id);
        return success(true);
    }

    /**
     * 获取部门列表
     * <p>
     * 根据查询条件获取部门的列表。
     *
     * @param reqVO 查询条件
     * @return 返回部门列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取部门列表")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<List<DeptRespVO>> getDeptList(DeptListReqVO reqVO) {
        List<DeptDO> list = deptService.getDeptList(reqVO);
        return success(BeanUtils.toBean(list, DeptRespVO.class));
    }

    /**
     * 获取部门精简信息列表
     * <p>
     * 获取所有开启状态的部门精简信息列表，主要用于前端的下拉选项。
     *
     * @return 返回精简的部门列表
     */
    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取部门精简信息列表", description = "只包含被开启的部门，主要用于前端的下拉选项")
    public CommonResult<List<DeptSimpleRespVO>> getSimpleDeptList() {
        DeptListReqVO deptListReqVO = new DeptListReqVO();
        deptListReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        List<DeptDO> list = deptService.getDeptList(deptListReqVO);
        return success(BeanUtils.toBean(list, DeptSimpleRespVO.class));
    }

    /**
     * 获取部门信息
     * <p>
     * 根据部门 ID 获取详细的部门信息。
     *
     * @param id 部门的 ID
     * @return 返回部门详细信息
     */
    @GetMapping("/get")
    @Operation(summary = "获得部门信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<DeptRespVO> getDept(@RequestParam("id") Long id) {
        DeptDO dept = deptService.getDept(id);
        return success(BeanUtils.toBean(dept, DeptRespVO.class));
    }

}
