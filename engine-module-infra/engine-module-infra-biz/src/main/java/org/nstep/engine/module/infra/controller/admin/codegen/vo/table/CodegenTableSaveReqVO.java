package org.nstep.engine.module.infra.controller.admin.codegen.vo.table;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.nstep.engine.module.infra.enums.codegen.CodegenSceneEnum;
import org.nstep.engine.module.infra.enums.codegen.CodegenTemplateTypeEnum;

/**
 * 管理后台 - 代码生成表定义创建/修改 Request VO
 * <p>
 * 该类用于管理后台创建或修改代码生成表定义的请求数据，包含表的基本信息、生成场景、模板类型、前端类型等字段。
 * 还包括表与其他表的关联信息，如父表编号、子表字段编号、树表相关字段等。
 * </p>
 */
@Schema(description = "管理后台 - 代码生成表定义创建/修改 Request VO")
@Data
public class CodegenTableSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id; // 表的唯一标识

    @Schema(description = "生成场景，参见 CodegenSceneEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "导入类型不能为空")
    private Integer scene; // 生成场景，参见 CodegenSceneEnum 枚举

    @Schema(description = "表名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    @NotNull(message = "表名称不能为空")
    private String tableName; // 表的名称

    @Schema(description = "表描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    @NotNull(message = "表描述不能为空")
    private String tableComment; // 表的描述

    @Schema(description = "备注", example = "我是备注")
    private String remark; // 备注信息

    @Schema(description = "模块名", requiredMode = Schema.RequiredMode.REQUIRED, example = "system")
    @NotNull(message = "模块名不能为空")
    private String moduleName; // 所属模块名称

    @Schema(description = "业务名", requiredMode = Schema.RequiredMode.REQUIRED, example = "codegen")
    @NotNull(message = "业务名不能为空")
    private String businessName; // 所属业务名称

    @Schema(description = "类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "CodegenTable")
    @NotNull(message = "类名称不能为空")
    private String className; // 类名称

    @Schema(description = "类描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "代码生成器的表定义")
    @NotNull(message = "类描述不能为空")
    private String classComment; // 类描述

    @Schema(description = "作者", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine源码")
    @NotNull(message = "作者不能为空")
    private String author; // 作者信息

    @Schema(description = "模板类型，参见 CodegenTemplateTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模板类型不能为空")
    private Integer templateType; // 模板类型，参见 CodegenTemplateTypeEnum 枚举

    @Schema(description = "前端类型，参见 CodegenFrontTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @NotNull(message = "前端类型不能为空")
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

    /**
     * 校验生成场景为管理后台时，是否设置了上级菜单。
     * <p>
     * 生成场景为管理后台时，必须设置上级菜单，否则生成的菜单 SQL 将没有父级菜单。
     * </p>
     *
     * @return 如果场景为管理后台且没有设置上级菜单，则返回 false。
     */
    @AssertTrue(message = "上级菜单不能为空，请前往 [修改生成配置 -> 生成信息] 界面，设置“上级菜单”字段")
    @JsonIgnore
    public boolean isParentMenuIdValid() {
        return ObjectUtil.notEqual(getScene(), CodegenSceneEnum.ADMIN.getScene())
                || getParentMenuId() != null;
    }

    /**
     * 校验关联的父表信息是否完整。
     * <p>
     * 当模板类型为子表时，必须提供主表编号、子表关联字段编号及是否一对多等信息。
     * </p>
     *
     * @return 如果模板类型为子表且相关字段为空，则返回 false。
     */
    @AssertTrue(message = "关联的父表信息不全")
    @JsonIgnore
    public boolean isSubValid() {
        return ObjectUtil.notEqual(getTemplateType(), CodegenTemplateTypeEnum.SUB)
                || (ObjectUtil.isAllNotEmpty(masterTableId, subJoinColumnId, subJoinMany));
    }

    /**
     * 校验关联的树表信息是否完整。
     * <p>
     * 当模板类型为树表时，必须提供树表的父字段编号和名字字段编号。
     * </p>
     *
     * @return 如果模板类型为树表且相关字段为空，则返回 false。
     */
    @AssertTrue(message = "关联的树表信息不全")
    @JsonIgnore
    public boolean isTreeValid() {
        return ObjectUtil.notEqual(templateType, CodegenTemplateTypeEnum.TREE)
                || (ObjectUtil.isAllNotEmpty(treeParentColumnId, treeNameColumnId));
    }
}
