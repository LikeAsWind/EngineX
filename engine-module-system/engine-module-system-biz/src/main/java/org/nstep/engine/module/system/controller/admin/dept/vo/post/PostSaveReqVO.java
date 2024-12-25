package org.nstep.engine.module.system.controller.admin.dept.vo.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.nstep.engine.framework.common.enums.CommonStatusEnum;
import org.nstep.engine.framework.common.validation.InEnum;

/**
 * 管理后台 - 岗位创建/修改请求 VO (Value Object)
 * <p>
 * 该类用于管理后台中创建或修改岗位信息的请求数据，包括岗位的基本信息及状态等。
 */
@Schema(description = "管理后台 - 岗位创建/修改 Request VO")
@Data
public class PostSaveReqVO {

    /**
     * 岗位编号
     * <p>
     * 用于标识岗位的唯一编号。
     *
     * @example 1024 岗位编号示例
     */
    @Schema(description = "岗位编号", example = "1024")
    private Long id;

    /**
     * 岗位名称
     * <p>
     * 岗位的名称，不能为空且长度不能超过 50 个字符。
     *
     * @example "小土豆" 岗位名称示例
     */
    @Schema(description = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小土豆")
    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 50, message = "岗位名称长度不能超过 50 个字符")
    private String name;

    /**
     * 岗位编码
     * <p>
     * 岗位的唯一编码，不能为空且长度不能超过 64 个字符。
     *
     * @example "engine" 岗位编码示例
     */
    @Schema(description = "岗位编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 64, message = "岗位编码长度不能超过64个字符")
    private String code;

    /**
     * 显示顺序
     * <p>
     * 岗位的显示顺序，不能为空，用于决定岗位在展示时的排序位置。
     *
     * @example 1024 显示顺序示例
     */
    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    /**
     * 状态
     * <p>
     * 岗位的状态，参见 CommonStatusEnum 枚举类，用于标识岗位是否启用。
     *
     * @example 1 状态示例，1 表示启用，0 表示禁用
     */
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @InEnum(CommonStatusEnum.class) // 验证状态值是否在 CommonStatusEnum 枚举类中
    private Integer status;

    /**
     * 备注
     * <p>
     * 岗位的备注信息，用于补充说明岗位的相关信息。
     *
     * @example "快乐的备注" 备注示例
     */
    @Schema(description = "备注", example = "快乐的备注")
    private String remark;

}
