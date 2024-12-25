package org.nstep.engine.module.infra.controller.admin.codegen.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 代码生成预览 Response VO
 * <p>
 * 该类用于返回代码生成器的文件预览信息。每个文件都对应一个该对象，包含文件路径和生成的代码内容。
 * </p>
 */
@Schema(description = "管理后台 - 代码生成预览 Response VO，注意，每个文件都是一个该对象")
@Data
public class CodegenPreviewRespVO {

    @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "java/org.nstep.engine/adminserver/modules/system/controller/test/SysTestDemoController.java")
    private String filePath; // 文件路径，指示生成代码文件的保存位置

    @Schema(description = "代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "Hello World")
    private String code; // 生成的代码内容
}
