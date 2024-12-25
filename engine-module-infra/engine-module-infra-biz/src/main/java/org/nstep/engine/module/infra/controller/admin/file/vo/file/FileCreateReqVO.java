package org.nstep.engine.module.infra.controller.admin.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 文件创建请求 VO
 * <p>
 * 该类用于接收文件创建请求，包含文件配置编号、文件路径、文件名、文件 URL、MIME 类型和文件大小等信息。
 * </p>
 */
@Schema(description = "管理后台 - 文件创建 Request VO")
@Data
public class FileCreateReqVO {

    /**
     * 文件配置编号
     * <p>
     * 用于标识文件所属的文件配置，通常与文件存储相关的配置进行绑定。
     * </p>
     */
    @NotNull(message = "文件配置编号不能为空")
    @Schema(description = "文件配置编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    /**
     * 文件路径
     * <p>
     * 文件在存储系统中的路径，通常是相对于存储根目录的路径。
     * </p>
     */
    @NotNull(message = "文件路径不能为空")
    @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine.jpg")
    private String path;

    /**
     * 原文件名
     * <p>
     * 文件上传时的原始文件名，通常用于文件展示或备份。
     * </p>
     */
    @NotNull(message = "原文件名不能为空")
    @Schema(description = "原文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine.jpg")
    private String name;

    /**
     * 文件 URL
     * <p>
     * 文件在存储系统中的访问 URL，通常用于提供文件下载或查看的链接。
     * </p>
     */
    @NotNull(message = "文件 URL不能为空")
    @Schema(description = "文件 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine.jpg")
    private String url;

    /**
     * 文件 MIME 类型
     * <p>
     * 文件的 MIME 类型，用于标识文件的内容类型，如 `application/octet-stream` 或 `image/jpeg`。
     * </p>
     */
    @Schema(description = "文件 MIME 类型", example = "application/octet-stream")
    private String type;

    /**
     * 文件大小
     * <p>
     * 文件的大小，单位为字节（bytes）。
     * </p>
     */
    @Schema(description = "文件大小", example = "2048", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer size;

}
