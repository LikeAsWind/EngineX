package org.nstep.engine.module.infra.controller.admin.codegen.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.column.CodegenColumnRespVO;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.table.CodegenTableRespVO;

import java.util.List;

/**
 * 管理后台 - 代码生成表和字段的明细 Response VO
 * <p>
 * 该类用于管理后台返回代码生成器表和字段的详细信息，包括表的定义和字段的定义列表。
 * </p>
 */
@Schema(description = "管理后台 - 代码生成表和字段的明细 Response VO")
@Data
public class CodegenDetailRespVO {

    @Schema(description = "表定义")
    private CodegenTableRespVO table; // 表的定义

    @Schema(description = "字段定义")
    private List<CodegenColumnRespVO> columns; // 字段的定义列表
}
