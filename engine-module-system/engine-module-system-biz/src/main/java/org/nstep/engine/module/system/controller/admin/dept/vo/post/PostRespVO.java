package org.nstep.engine.module.system.controller.admin.dept.vo.post;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.nstep.engine.framework.excel.core.annotations.DictFormat;
import org.nstep.engine.framework.excel.core.convert.DictConvert;
import org.nstep.engine.module.system.enums.DictTypeConstants;

import java.time.LocalDateTime;

/**
 * 管理后台 - 岗位信息响应 VO (Value Object)
 * <p>
 * 该类用于管理后台中返回岗位信息的响应数据，包括岗位的基本信息及状态等，支持 Excel 导出。
 */
@Schema(description = "管理后台 - 岗位信息 Response VO")
@Data
@ExcelIgnoreUnannotated // 忽略没有标注 @ExcelProperty 的字段
public class PostRespVO {

    /**
     * 岗位序号
     * <p>
     * 岗位的唯一标识符。
     *
     * @example 1024 岗位序号示例
     */
    @Schema(description = "岗位序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("岗位序号") // Excel 导出的列名
    private Long id;

    /**
     * 岗位名称
     * <p>
     * 岗位的名称。
     *
     * @example "小土豆" 岗位名称示例
     */
    @Schema(description = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小土豆")
    @ExcelProperty("岗位名称") // Excel 导出的列名
    private String name;

    /**
     * 岗位编码
     * <p>
     * 岗位的唯一编码。
     *
     * @example "engine" 岗位编码示例
     */
    @Schema(description = "岗位编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    @ExcelProperty("岗位编码") // Excel 导出的列名
    private String code;

    /**
     * 显示顺序
     * <p>
     * 岗位的显示顺序，决定岗位在展示时的排序位置。
     *
     * @example 1024 显示顺序示例
     */
    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("岗位排序") // Excel 导出的列名
    private Integer sort;

    /**
     * 状态
     * <p>
     * 岗位的状态，参见 CommonStatusEnum 枚举类，用于标识岗位是否启用。
     *
     * @example 1 状态示例，1 表示启用，0 表示禁用
     */
    @Schema(description = "状态，参见 CommonStatusEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class) // Excel 导出时使用 DictConvert 转换器
    @DictFormat(DictTypeConstants.COMMON_STATUS) // 使用字典格式转换状态
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

    /**
     * 创建时间
     * <p>
     * 岗位的创建时间，记录岗位的创建日期和时间。
     */
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
