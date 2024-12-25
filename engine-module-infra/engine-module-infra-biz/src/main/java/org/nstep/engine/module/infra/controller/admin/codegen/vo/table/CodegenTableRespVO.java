package org.nstep.engine.module.infra.controller.admin.codegen.vo.table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 代码生成表定义 Response VO
 * <p>
 * 该类用于返回管理后台代码生成表定义的响应数据，包含表的基本信息、生成场景、模板类型、前端类型等。
 * 还包含了表与其他表的关联信息，如父表编号、子表字段编号、树表相关字段等。
 * </p>
 */
@Schema(description = "管理后台 - 代码生成表定义 Response VO")
@Data
public class CodegenTableRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id; // 表的唯一标识

    @Schema(description = "生成场景，参见 CodegenSceneEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer scene; // 生成场景，参见 CodegenSceneEnum 枚举

    @Schema(description = "表名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    private String tableName; // 表的名称

    @Schema(description = "表描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    private String tableComment; // 表的描述

    @Schema(description = "备注", example = "我是备注")
    private String remark; // 备注信息

    @Schema(description = "模块名", requiredMode = Schema.RequiredMode.REQUIRED, example = "system")
    private String moduleName; // 所属模块名称

    @Schema(description = "业务名", requiredMode = Schema.RequiredMode.REQUIRED, example = "codegen")
    private String businessName; // 所属业务名称

    @Schema(description = "类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "CodegenTable")
    private String className; // 类名称

    @Schema(description = "类描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "代码生成器的表定义")
    private String classComment; // 类描述

    @Schema(description = "作者", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine源码")
    private String author; // 作者信息

    @Schema(description = "模板类型，参见 CodegenTemplateTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer templateType; // 模板类型，参见 CodegenTemplateTypeEnum 枚举

    @Schema(description = "前端类型，参见 CodegenFrontTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer frontType; // 前端类型，参见 CodegenFrontTypeEnum 枚举

    @Schema(description = "父菜单编号", example = "1024")
    private Long parentMenuId; // 父菜单编号

    @Schema(description = "主表的编号", example = "2048")
    private Long masterTableId; // 主表的编号

    @Schema(description = "子表关联主表的字段编号", example = "4096")
    private Long subJoinColumnId; // 子表关联主表的字段编号

    @Schema(description = "主表与子表是否一对多", example = "4096")
    private Boolean subJoinMany; // 主表与子表是否一对多

    @Schema(description = "树表的父字段编号", example = "8192")
    private Long treeParentColumnId; // 树表的父字段编号

    @Schema(description = "树表的名字字段编号", example = "16384")
    private Long treeNameColumnId; // 树表的名字字段编号

    @Schema(description = "主键编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer dataSourceConfigId; // 数据源配置的主键编号

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime; // 创建时间

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime; // 更新时间
}
