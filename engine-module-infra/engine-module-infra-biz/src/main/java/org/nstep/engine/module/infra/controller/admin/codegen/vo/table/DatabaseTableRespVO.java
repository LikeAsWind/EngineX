package org.nstep.engine.module.infra.controller.admin.codegen.vo.table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 数据库的表定义 Response VO
 * <p>
 * 该类用于管理后台返回数据库表的定义信息，包括表的名称和描述。
 * </p>
 */
@Schema(description = "管理后台 - 数据库的表定义 Response VO")
@Data
public class DatabaseTableRespVO {

    @Schema(description = "表名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxxx")
    private String name; // 数据库表的名称

    @Schema(description = "表描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine源码")
    private String comment; // 数据库表的描述
}
