package org.nstep.engine.module.system.api.dept.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 岗位 Response DTO
 * <p>
 * 该数据传输对象（DTO）用于表示岗位的响应数据，供 RPC 服务使用。
 */
@Schema(description = "RPC 服务 - 岗位 Response DTO")
@Data
public class PostRespDTO {

    /**
     * 岗位编号
     * <p>
     * 唯一标识岗位的编号。
     * 必填项，示例值为 "1"。
     */
    @Schema(description = "岗位编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 岗位名称
     * <p>
     * 岗位的名称，例如 "小土豆"。
     * 必填项，示例值为 "小土豆"。
     */
    @Schema(description = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小土豆")
    private String name;

    /**
     * 岗位编码
     * <p>
     * 岗位的唯一编码，用于标识岗位，例如 "engine"。
     * 必填项，示例值为 "engine"。
     */
    @Schema(description = "岗位编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    private String code;

    /**
     * 岗位排序
     * <p>
     * 用于岗位显示排序的字段，值越小，排序越靠前。
     * 必填项，示例值为 "1"。
     */
    @Schema(description = "岗位排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sort;

    /**
     * 状态
     * <p>
     * 岗位的当前状态，使用整数值表示，参见 `CommonStatusEnum` 枚举类。
     * 必填项，示例值为 "1"。
     */
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status; // 参见 CommonStatusEnum 枚举

}
