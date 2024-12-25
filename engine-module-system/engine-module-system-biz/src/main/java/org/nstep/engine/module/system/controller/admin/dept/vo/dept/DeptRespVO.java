package org.nstep.engine.module.system.controller.admin.dept.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 部门信息响应 VO (Value Object)
 * <p>
 * 该类用于返回部门信息的响应数据，包括部门的基本信息、负责人、联系方式、状态等。
 */
@Schema(description = "管理后台 - 部门信息 Response VO")
@Data
public class DeptRespVO {

    /**
     * 部门编号
     * <p>
     * 部门的唯一标识符，通常是数据库中的主键 ID。
     *
     * @example 1024 部门编号示例
     */
    @Schema(description = "部门编号", example = "1024")
    private Long id;

    /**
     * 部门名称
     * <p>
     * 部门的名称，通常是部门的显示名称。
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
    @Schema(description = "父部门 ID", example = "1024")
    private Long parentId;

    /**
     * 显示顺序
     * <p>
     * 部门在列表中的显示顺序，通常用于排序显示。
     *
     * @example 1024 显示顺序示例
     */
    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer sort;

    /**
     * 负责人的用户编号
     * <p>
     * 部门负责人的用户 ID，通常是指向用户表中的主键。
     *
     * @example 2048 负责人用户编号示例
     */
    @Schema(description = "负责人的用户编号", example = "2048")
    private Long leaderUserId;

    /**
     * 联系电话
     * <p>
     * 部门的联系电话。
     *
     * @example "15601691000" 部门联系电话示例
     */
    @Schema(description = "联系电话", example = "15601691000")
    private String phone;

    /**
     * 邮箱
     * <p>
     * 部门的邮箱地址。
     *
     * @example "engine@nstep.cn" 部门邮箱示例
     */
    @Schema(description = "邮箱", example = "engine@nstep.cn")
    private String email;

    /**
     * 状态
     * <p>
     * 部门的当前状态，参见 `CommonStatusEnum` 枚举，表示部门是否启用。
     *
     * @example 1 状态示例，表示启用状态
     */
    @Schema(description = "状态,见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    /**
     * 创建时间
     * <p>
     * 部门的创建时间。
     *
     * @example "时间戳格式" 创建时间示例
     */
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private LocalDateTime createTime;

}
