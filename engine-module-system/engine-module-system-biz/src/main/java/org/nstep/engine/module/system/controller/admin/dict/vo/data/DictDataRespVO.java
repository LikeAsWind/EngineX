package org.nstep.engine.module.system.controller.admin.dict.vo.data;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.nstep.engine.framework.excel.core.annotations.DictFormat;
import org.nstep.engine.framework.excel.core.convert.DictConvert;
import org.nstep.engine.module.system.enums.DictTypeConstants;

import java.time.LocalDateTime;

/**
 * 管理后台 - 字典数据信息响应 VO
 * <p>
 * 该类用于返回字典数据的详细信息，包含字典的编号、标签、值、类型、状态等信息。
 * 并且支持通过 Excel 导出时的字段映射。
 */
@Schema(description = "管理后台 - 字典数据信息 Response VO")
@Data
@ExcelIgnoreUnannotated // 表示忽略未注解的字段，避免导出时出现不需要的字段
public class DictDataRespVO {

    /**
     * 字典数据编号
     * <p>
     * 用于唯一标识字典数据的 ID，通常为数据库主键。
     */
    @Schema(description = "字典数据编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("字典编码") // Excel 导出时显示的字段名称
    private Long id;

    /**
     * 显示顺序
     * <p>
     * 用于排序字典数据，通常用于前端显示时的排序。
     */
    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("字典排序")
    private Integer sort;

    /**
     * 字典标签
     * <p>
     * 用于描述字典项的名称，通常是用户可见的标签。
     */
    @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    @ExcelProperty("字典标签")
    private String label;

    /**
     * 字典值
     * <p>
     * 用于存储字典项的实际值，通常是字典项的唯一标识符。
     */
    @Schema(description = "字典值", requiredMode = Schema.RequiredMode.REQUIRED, example = "nstep")
    @ExcelProperty("字典键值")
    private String value;

    /**
     * 字典类型
     * <p>
     * 用于区分不同类型的字典数据，例如：性别、状态等。
     */
    @Schema(description = "字典类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_common_sex")
    @ExcelProperty("字典类型")
    private String dictType;

    /**
     * 状态
     * <p>
     * 字典项的状态，通常为启用或禁用，值通过 `DictConvert` 转换器进行处理。
     */
    @Schema(description = "状态, 见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class) // 使用自定义转换器处理状态
    @DictFormat(DictTypeConstants.COMMON_STATUS) // 格式化字典状态
    private Integer status;

    /**
     * 颜色类型
     * <p>
     * 用于前端显示字典项的颜色类型，例如：default、primary、success 等。
     */
    @Schema(description = "颜色类型, default、primary、success、info、warning、danger", example = "default")
    private String colorType;

    /**
     * CSS 样式
     * <p>
     * 用于自定义字典项的样式，例如：按钮的样式类名。
     */
    @Schema(description = "css 样式", example = "btn-visible")
    private String cssClass;

    /**
     * 备注
     * <p>
     * 用于描述字典项的附加信息。
     */
    @Schema(description = "备注", example = "我是一个角色")
    private String remark;

    /**
     * 创建时间
     * <p>
     * 字典数据的创建时间，通常为数据库记录的创建时间。
     */
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private LocalDateTime createTime;

}
