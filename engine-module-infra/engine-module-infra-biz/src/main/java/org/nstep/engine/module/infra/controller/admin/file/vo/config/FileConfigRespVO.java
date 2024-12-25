package org.nstep.engine.module.infra.controller.admin.file.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.nstep.engine.module.infra.framework.file.core.client.FileClientConfig;

import java.time.LocalDateTime;

/**
 * 管理后台 - 文件配置响应 VO
 * <p>
 * 该类用于返回文件配置的详细信息，包括文件配置的基本信息，如配置名、存储器类型、
 * 是否为主配置、存储配置、备注以及创建时间等。
 * </p>
 */
@Schema(description = "管理后台 - 文件配置 Response VO")
@Data
public class FileConfigRespVO {

    /**
     * 编号
     * <p>
     * 文件配置的唯一标识符，通常用于在系统中进行操作（如更新、删除等）。
     * </p>
     */
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 配置名
     * <p>
     * 文件配置的名称，通常用于标识不同的文件配置。示例：S3 - 阿里云。
     * </p>
     */
    @Schema(description = "配置名", requiredMode = Schema.RequiredMode.REQUIRED, example = "S3 - 阿里云")
    private String name;

    /**
     * 存储器类型
     * <p>
     * 存储器的类型，参见 `FileStorageEnum` 枚举类。示例：1（代表阿里云存储器）。
     * </p>
     */
    @Schema(description = "存储器，参见 FileStorageEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer storage;

    /**
     * 是否为主配置
     * <p>
     * 标识当前配置是否为主配置。主配置通常用于作为默认配置使用。
     * </p>
     */
    @Schema(description = "是否为主配置", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean master;

    /**
     * 存储配置
     * <p>
     * 存储相关的配置，通常包括访问密钥、存储路径等信息。使用 `FileClientConfig` 类型存储。
     * </p>
     */
    @Schema(description = "存储配置", requiredMode = Schema.RequiredMode.REQUIRED)
    private FileClientConfig config;

    /**
     * 备注
     * <p>
     * 文件配置的备注信息，用于描述该配置的附加信息或特殊用途。
     * </p>
     */
    @Schema(description = "备注", example = "我是备注")
    private String remark;

    /**
     * 创建时间
     * <p>
     * 文件配置的创建时间，用于记录配置创建的时间。
     * </p>
     */
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
