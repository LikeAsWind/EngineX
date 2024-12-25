package org.nstep.engine.module.infra.controller.admin.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理后台 - 上传文件请求 VO
 * <p>
 * 该类用于接收上传文件的请求参数，包括文件附件和文件路径。
 * </p>
 */
@Schema(description = "管理后台 - 上传文件 Request VO")
@Data
public class FileUploadReqVO {

    /**
     * 文件附件
     * <p>
     * 用户上传的文件，不能为空。
     * </p>
     */
    @Schema(description = "文件附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

    /**
     * 文件路径
     * <p>
     * 文件存储的路径，若未提供路径，则使用默认路径。
     * </p>
     */
    @Schema(description = "文件附件", example = "engine.png")
    private String path;

}
