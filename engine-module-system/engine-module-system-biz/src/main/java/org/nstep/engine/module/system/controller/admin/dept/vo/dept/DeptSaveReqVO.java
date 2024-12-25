package org.nstep.engine.module.system.controller.admin.dept.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.nstep.engine.framework.common.enums.CommonStatusEnum;
import org.nstep.engine.framework.common.validation.InEnum;

/**
 * 管理后台 - 部门创建/修改请求 VO (Value Object)
 * <p>
 * 该类用于管理后台中部门创建或修改时接收请求的数据，包含部门的基本信息，如名称、排序、负责人、状态等。
 */
@Schema(description = "管理后台 - 部门创建/修改 Request VO")
@Data
public class DeptSaveReqVO {

    /**
     * 部门编号
     * <p>
     * 部门的唯一标识符，通常用于修改现有部门时传递。
     *
     * @example 1024 部门编号示例
     */
    @Schema(description = "部门编号", example = "1024")
    private Long id;

    /**
     * 部门名称
     * <p>
     * 部门的名称，不能为空，最大长度为 30 个字符。
     *
     * @example "engine" 部门名称示例
     */
    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 30, message = "部门名称长度不能超过 30 个字符")
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
     * 部门在列表中的显示顺序，不能为空。
     *
     * @example 1024 显示顺序示例
     */
    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "显示顺序不能为空")
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
     * 部门的联系电话，最大长度为 11 个字符。
     *
     * @example "15601691000" 部门联系电话示例
     */
    @Schema(description = "联系电话", example = "15601691000")
    @Size(max = 11, message = "联系电话长度不能超过11个字符")
    private String phone;

    /**
     * 邮箱
     * <p>
     * 部门的邮箱地址，格式必须正确，最大长度为 50 个字符。
     *
     * @example "engine@nstep.cn" 部门邮箱示例
     */
    @Schema(description = "邮箱", example = "engine@nstep.cn")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;

    /**
     * 状态
     * <p>
     * 部门的当前状态，参见 `CommonStatusEnum` 枚举，表示部门是否启用，不能为空。
     *
     * @example 1 状态示例，表示启用状态
     */
    @Schema(description = "状态,见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    private Integer status;

}
