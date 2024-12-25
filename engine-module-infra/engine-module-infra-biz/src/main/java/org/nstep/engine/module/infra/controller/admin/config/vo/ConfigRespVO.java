package org.nstep.engine.module.infra.controller.admin.config.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.nstep.engine.framework.excel.core.annotations.DictFormat;
import org.nstep.engine.framework.excel.core.convert.DictConvert;
import org.nstep.engine.module.infra.enums.DictTypeConstants;

import java.time.LocalDateTime;

/**
 * 管理后台 - 参数配置信息 Response VO
 * <p>
 * 用于返回参数配置信息的响应数据传输对象，包含了参数的详细信息，包括序号、分类、名称、键值、类型、可见性等。
 * </p>
 */
@Schema(description = "管理后台 - 参数配置信息 Response VO")
@Data
@ExcelIgnoreUnannotated // 忽略没有注解的字段
public class ConfigRespVO {

    /**
     * 参数配置序号
     */
    @Schema(description = "参数配置序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("参数配置序号")
    private Long id;

    /**
     * 参数分类
     */
    @Schema(description = "参数分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "biz")
    @ExcelProperty("参数分类")
    private String category;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "数据库名")
    @ExcelProperty("参数名称")
    private String name;

    /**
     * 参数键名
     */
    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED, example = "yunai.db.username")
    @ExcelProperty("参数键名")
    private String key;

    /**
     * 参数键值
     */
    @Schema(description = "参数键值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("参数键值")
    private String value;

    /**
     * 参数类型，参见 SysConfigTypeEnum 枚举
     */
    @Schema(description = "参数类型，参见 SysConfigTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "参数类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.CONFIG_TYPE) // 字典格式化
    private Integer type;

    /**
     * 是否可见
     */
    @Schema(description = "是否可见", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @ExcelProperty(value = "是否可见", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.BOOLEAN_STRING) // 字典格式化
    private Boolean visible;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "备注一下很帅气！")
    @ExcelProperty("备注")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
