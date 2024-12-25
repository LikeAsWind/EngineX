package org.nstep.engine.module.infra.controller.admin.codegen.vo.column;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 代码生成字段定义创建/修改 Request VO
 * <p>
 * 该类用于接收管理后台代码生成器模块的字段定义创建或修改请求数据，
 * 包含字段的详细信息，并进行必要的校验。
 * </p>
 */
@Schema(description = "管理后台 - 代码生成字段定义创建/修改 Request VO")
@Data
public class CodegenColumnSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id; // 字段的唯一标识符

    @Schema(description = "表编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "表编号不能为空")
    private Long tableId; // 对应表的编号

    @Schema(description = "字段名", requiredMode = Schema.RequiredMode.REQUIRED, example = "user_age")
    @NotNull(message = "字段名不能为空")
    private String columnName; // 字段的名称

    @Schema(description = "字段类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "int(11)")
    @NotNull(message = "字段类型不能为空")
    private String dataType; // 字段的数据类型（如 int、varchar 等）

    @Schema(description = "字段描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "年龄")
    @NotNull(message = "字段描述不能为空")
    private String columnComment; // 字段的描述信息

    @Schema(description = "是否允许为空", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否允许为空不能为空")
    private Boolean nullable; // 是否允许该字段为空

    @Schema(description = "是否主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否主键不能为空")
    private Boolean primaryKey; // 是否为主键字段

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "排序不能为空")
    private Integer ordinalPosition; // 字段在表中的排序位置

    @Schema(description = "Java 属性类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "userAge")
    @NotNull(message = "Java 属性类型不能为空")
    private String javaType; // 对应的 Java 属性类型（如 Integer、String 等）

    @Schema(description = "Java 属性名", requiredMode = Schema.RequiredMode.REQUIRED, example = "Integer")
    @NotNull(message = "Java 属性名不能为空")
    private String javaField; // 对应的 Java 属性名

    @Schema(description = "字典类型", example = "sys_gender")
    private String dictType; // 如果字段是字典类型，字典的类型

    @Schema(description = "数据示例", example = "1024")
    private String example; // 字段的数据示例

    @Schema(description = "是否为 Create 创建操作的字段", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否为 Create 创建操作的字段不能为空")
    private Boolean createOperation; // 是否是创建操作中的字段

    @Schema(description = "是否为 Update 更新操作的字段", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否为 Update 更新操作的字段不能为空")
    private Boolean updateOperation; // 是否是更新操作中的字段

    @Schema(description = "是否为 List 查询操作的字段", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否为 List 查询操作的字段不能为空")
    private Boolean listOperation; // 是否是查询操作中的字段

    @Schema(description = "List 查询操作的条件类型，参见 CodegenColumnListConditionEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "LIKE")
    @NotNull(message = "List 查询操作的条件类型不能为空")
    private String listOperationCondition; // 查询条件类型（如 LIKE、= 等）

    @Schema(description = "是否为 List 查询操作的返回字段", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否为 List 查询操作的返回字段不能为空")
    private Boolean listOperationResult; // 是否是查询操作的返回字段

    @Schema(description = "显示类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "input")
    @NotNull(message = "显示类型不能为空")
    private String htmlType; // 字段的显示类型（如 input、select 等）
}
