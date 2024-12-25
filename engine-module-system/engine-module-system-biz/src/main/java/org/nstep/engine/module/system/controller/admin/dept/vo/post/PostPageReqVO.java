package org.nstep.engine.module.system.controller.admin.dept.vo.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nstep.engine.framework.common.pojo.PageParam;

/**
 * 管理后台 - 岗位分页请求 VO (Value Object)
 * <p>
 * 该类用于管理后台中查询岗位的分页请求参数，继承自 PageParam 类，支持分页查询岗位信息。
 */
@Schema(description = "管理后台 - 岗位分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class PostPageReqVO extends PageParam {

    /**
     * 岗位编码
     * <p>
     * 用于模糊匹配岗位的编码。
     *
     * @example "engine" 岗位编码示例
     */
    @Schema(description = "岗位编码，模糊匹配", example = "engine")
    private String code;

    /**
     * 岗位名称
     * <p>
     * 用于模糊匹配岗位的名称。
     *
     * @example "engine" 岗位名称示例
     */
    @Schema(description = "岗位名称，模糊匹配", example = "engine")
    private String name;

    /**
     * 展示状态
     * <p>
     * 用于筛选岗位的展示状态，参见 CommonStatusEnum 枚举类。该字段用于过滤岗位的启用或禁用状态。
     *
     * @example 1 展示状态示例，1 表示启用，0 表示禁用
     */
    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

}
