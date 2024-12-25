package org.nstep.engine.module.infra.controller.admin.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 文件响应 VO
 * <p>
 * 该类用于返回文件信息，不包含文件内容（因为文件内容可能过大）。
 * </p>
 */
@Schema(description = "管理后台 - 文件 Response VO,不返回 content 字段，太大")
@Data
public class FileRespVO {

    /**
     * 文件编号
     * <p>
     * 唯一标识文件的编号。
     * </p>
     */
    @Schema(description = "文件编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    /**
     * 配置编号
     * <p>
     * 标识文件所属的配置编号。
     * </p>
     */
    @Schema(description = "配置编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    /**
     * 文件路径
     * <p>
     * 文件在存储系统中的路径。
     * </p>
     */
    @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine.jpg")
    private String path;

    /**
     * 原文件名
     * <p>
     * 文件上传时的原始文件名。
     * </p>
     */
    @Schema(description = "原文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine.jpg")
    private String name;

    /**
     * 文件 URL
     * <p>
     * 访问文件的 URL 地址。
     * </p>
     */
    @Schema(description = "文件 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.xxx.jpg")
    private String url;

    /**
     * 文件 MIME 类型
     * <p>
     * 文件的 MIME 类型，表示文件的内容类型。
     * </p>
     */
    @Schema(description = "文件MIME类型", example = "application/octet-stream")
    private String type;

    /**
     * 文件大小
     * <p>
     * 文件的大小，单位为字节。
     * </p>
     */
    @Schema(description = "文件大小", example = "2048", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer size;

    /**
     * 创建时间
     * <p>
     * 文件的创建时间。
     * </p>
     */
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
