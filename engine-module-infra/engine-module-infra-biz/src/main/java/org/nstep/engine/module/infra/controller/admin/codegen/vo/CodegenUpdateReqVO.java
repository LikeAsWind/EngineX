package org.nstep.engine.module.infra.controller.admin.codegen.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.column.CodegenColumnSaveReqVO;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.table.CodegenTableSaveReqVO;

import java.util.List;

/**
 * 管理后台 - 代码生成表和字段的修改 Request VO
 * <p>
 * 该类用于接收代码生成表和字段的修改请求，包含表定义和字段定义的修改信息。
 * </p>
 */
@Schema(description = "管理后台 - 代码生成表和字段的修改 Request VO")
@Data
public class CodegenUpdateReqVO {

    @Valid // 校验内嵌的字段
    @NotNull(message = "表定义不能为空")
    private CodegenTableSaveReqVO table; // 表定义对象，包含表的详细信息

    @Valid // 校验内嵌的字段
    @NotNull(message = "字段定义不能为空")
    private List<CodegenColumnSaveReqVO> columns; // 字段定义对象，包含字段的详细信息
}
