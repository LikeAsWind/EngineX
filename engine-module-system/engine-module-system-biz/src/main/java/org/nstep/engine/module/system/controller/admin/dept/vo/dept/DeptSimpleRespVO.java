package org.nstep.engine.module.system.controller.admin.dept.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理后台 - 部门精简信息响应 VO (Value Object)
 * <p>
 * 该类用于管理后台中返回部门的精简信息，通常用于展示部门列表时，包含部门的基本信息，如部门编号、名称、父部门 ID。
 */
@Schema(description = "管理后台 - 部门精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptSimpleRespVO {

    /**
     * 部门编号
     * <p>
     * 部门的唯一标识符，用于区分不同的部门。
     *
     * @example 1024 部门编号示例
     */
    @Schema(description = "部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    /**
     * 部门名称
     * <p>
     * 部门的名称，不能为空。
     *
     * @example "engine" 部门名称示例
     */
    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    private String name;

    /**
     * 父部门 ID
     * <p>
     * 如果该部门是子部门，则指向父部门的 ID。如果是顶级部门，通常为 null 或 0。
     *
     * @example 1024 父部门 ID 示例
     */
    @Schema(description = "父部门 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long parentId;

}
