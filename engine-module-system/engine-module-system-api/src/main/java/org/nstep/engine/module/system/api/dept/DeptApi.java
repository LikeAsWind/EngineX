package org.nstep.engine.module.system.api.dept;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.collection.CollectionUtils;
import org.nstep.engine.module.system.api.dept.dto.DeptRespDTO;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 部门 API 接口
 * <p>
 * 该接口提供了部门信息的获取、校验等操作，供 RPC 服务使用。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 部门")
public interface DeptApi {

    String PREFIX = ApiConstants.PREFIX + "/dept";

    /**
     * 获取部门信息
     * <p>
     * 根据部门编号获取部门的详细信息。
     *
     * @param id 部门编号
     * @return 部门响应 DTO
     */
    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得部门信息")
    @Parameter(name = "id", description = "部门编号", example = "1024", required = true)
    CommonResult<DeptRespDTO> getDept(@RequestParam("id") Long id);

    /**
     * 获取部门信息数组
     * <p>
     * 根据部门编号数组获取多个部门的详细信息。
     *
     * @param ids 部门编号数组
     * @return 部门响应 DTO 列表
     */
    @GetMapping(PREFIX + "/list")
    @Operation(summary = "获得部门信息数组")
    @Parameter(name = "ids", description = "部门编号数组", example = "1,2", required = true)
    CommonResult<List<DeptRespDTO>> getDeptList(@RequestParam("ids") Collection<Long> ids);

    /**
     * 校验部门是否合法
     * <p>
     * 根据部门编号数组校验部门是否有效。
     *
     * @param ids 部门编号数组
     * @return 是否有效
     */
    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验部门是否合法")
    @Parameter(name = "ids", description = "部门编号数组", example = "1,2", required = true)
    CommonResult<Boolean> validateDeptList(@RequestParam("ids") Collection<Long> ids);

    /**
     * 获取指定编号的部门 Map
     * <p>
     * 获取部门编号为指定编号的部门，并返回一个 Map 结构，其中键为部门编号，值为部门响应 DTO。
     *
     * @param ids 部门编号数组
     * @return 部门 Map，键为部门编号，值为部门响应 DTO
     */
    default Map<Long, DeptRespDTO> getDeptMap(Collection<Long> ids) {
        List<DeptRespDTO> list = getDeptList(ids).getCheckedData();
        return CollectionUtils.convertMap(list, DeptRespDTO::getId);
    }

    /**
     * 获取指定部门的所有子部门
     * <p>
     * 根据部门编号获取该部门的所有子部门的详细信息。
     *
     * @param id 部门编号
     * @return 子部门响应 DTO 列表
     */
    @GetMapping(PREFIX + "/list-child")
    @Operation(summary = "获得指定部门的所有子部门")
    @Parameter(name = "id", description = "部门编号", example = "1024", required = true)
    CommonResult<List<DeptRespDTO>> getChildDeptList(@RequestParam("id") Long id);

}
