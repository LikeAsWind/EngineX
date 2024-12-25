package org.nstep.engine.module.system.controller.admin.dept.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 部门列表请求 VO (Value Object)
 * <p>
 * 该类用于接收前端传递的查询参数，主要用于查询部门列表。可以根据部门名称和展示状态进行筛选。
 */
@Schema(description = "管理后台 - 部门列表 Request VO")
@Data
public class DeptListReqVO {

    /**
     * 部门名称，模糊匹配
     * <p>
     * 用于模糊匹配部门名称，前端传递的部门名称将用于过滤查询结果。
     *
     * @example "engine" 部门名称示例
     */
    @Schema(description = "部门名称，模糊匹配", example = "engine")
    private String name;

    /**
     * 展示状态，参见 CommonStatusEnum 枚举类
     * <p>
     * 用于根据部门的展示状态进行筛选。可以通过状态值过滤出启用或禁用的部门。
     *
     * @example 1 状态示例，表示启用状态
     */
    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

}
