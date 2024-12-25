package org.nstep.engine.module.system.api.dept.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * RPC 服务 - 部门 Response DTO
 * <p>
 * 该数据传输对象（DTO）用于表示部门的响应数据，包含部门的基本信息，供 RPC 服务使用。
 */
@Schema(description = "RPC 服务 - 部门 Response DTO")
@Data
public class DeptRespDTO {

    /**
     * 部门编号
     * <p>
     * 唯一标识部门的编号。
     * 必填项，示例值为 "1"。
     */
    @Schema(description = "部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 部门名称
     * <p>
     * 部门的名称，例如 "研发部"。
     * 必填项，示例值为 "研发部"。
     */
    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "研发部")
    private String name;

    /**
     * 父部门编号
     * <p>
     * 如果该部门是子部门，则该字段表示其父部门的编号。
     * 必填项，示例值为 "1"。
     */
    @Schema(description = "父部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long parentId;

    /**
     * 负责人的用户编号
     * <p>
     * 部门负责人的用户编号，指向系统中对应的用户。
     * 必填项，示例值为 "1"。
     */
    @Schema(description = "负责人的用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long leaderUserId;

    /**
     * 部门状态
     * <p>
     * 部门的当前状态，使用整数值表示，参见 `CommonStatusEnum` 枚举类。
     * 必填项，示例值为 "1"。
     */
    @Schema(description = "部门状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status; // 参见 CommonStatusEnum 枚举

}
