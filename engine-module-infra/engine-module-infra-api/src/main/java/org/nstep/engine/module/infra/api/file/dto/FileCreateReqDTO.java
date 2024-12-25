package org.nstep.engine.module.infra.api.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * RPC 服务 - 文件创建请求数据传输对象 (DTO)
 * <p>
 * 该类用于表示文件创建请求的参数，包含文件的名称、路径以及文件内容。
 * <p>
 * 通过此 DTO 对象，RPC 服务接收来自客户端的文件创建请求。
 */
@Schema(description = "RPC 服务 - 文件创建 Request DTO")
@Data
public class FileCreateReqDTO {

    /**
     * 原文件名称
     * <p>
     * 用于指定文件的名称。
     * 示例：xxx.png
     */
    @Schema(description = "原文件名称", example = "xxx.png")
    private String name;

    /**
     * 文件路径
     * <p>
     * 用于指定文件的存储路径。
     * 示例：xxx.png
     */
    @Schema(description = "文件路径", example = "xxx.png")
    private String path;

    /**
     * 文件内容
     * <p>
     * 文件的实际内容，要求不能为空。
     * <p>
     * 该字段为必填项，不能为空，使用 `@NotEmpty` 注解进行验证。
     */
    @Schema(description = "文件内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "文件内容不能为空")
    private byte[] content;

}
