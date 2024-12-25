package org.nstep.engine.module.infra.controller.admin.codegen.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 基于数据库的表结构，创建代码生成器的表和字段定义 Request VO
 * <p>
 * 该类用于管理后台接收基于数据库表结构创建代码生成器的表和字段定义请求，包括数据源配置编号和表名列表。
 * </p>
 */
@Schema(description = "管理后台 - 基于数据库的表结构，创建代码生成器的表和字段定义 Request VO")
@Data
public class CodegenCreateListReqVO {

    @Schema(description = "数据源配置的编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "数据源配置的编号不能为空")
    private Long dataSourceConfigId; // 数据源配置的编号

    @Schema(description = "表名数组", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotNull(message = "表名数组不能为空")
    private List<String> tableNames; // 需要创建代码生成器表和字段定义的表名列表
}
