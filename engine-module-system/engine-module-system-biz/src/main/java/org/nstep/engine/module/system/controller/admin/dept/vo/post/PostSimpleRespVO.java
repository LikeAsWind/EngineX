package org.nstep.engine.module.system.controller.admin.dept.vo.post;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 岗位信息的精简 Response VO (Value Object)
 * <p>
 * 该类用于返回管理后台中的岗位精简信息，包括岗位的基本标识信息，如岗位编号和岗位名称。
 */
@Schema(description = "管理后台 - 岗位信息的精简 Response VO")
@Data
public class PostSimpleRespVO {

    /**
     * 岗位序号
     * <p>
     * 岗位的唯一标识符，用于区分不同的岗位。
     *
     * @example 1024 岗位序号示例
     */
    @Schema(description = "岗位序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("岗位序号")
    private Long id;

    /**
     * 岗位名称
     * <p>
     * 岗位的名称，用于标识岗位的名称。
     *
     * @example "小土豆" 岗位名称示例
     */
    @Schema(description = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小土豆")
    @ExcelProperty("岗位名称")
    private String name;

}
