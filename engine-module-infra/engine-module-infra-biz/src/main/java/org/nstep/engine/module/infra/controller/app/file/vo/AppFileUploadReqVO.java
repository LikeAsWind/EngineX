package org.nstep.engine.module.infra.controller.app.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户App - 上传文件请求值对象（Request VO）。
 * 用于封装上传文件时所需的数据。
 */
@Schema(description = "用户 App - 上传文件 Request VO")
@Data
public class AppFileUploadReqVO {

    /**
     * 文件附件。
     * 必须提供，用于接收上传的文件。
     */
    @Schema(description = "文件附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

    /**
     * 文件路径。
     * 用于存储文件上传后的路径。
     * 示例值："engineyuanma.png"。
     */
    @Schema(description = "文件附件", example = "engineyuanma.png")
    private String path;

}