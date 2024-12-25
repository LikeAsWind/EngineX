package org.nstep.engine.module.infra.controller.admin.file.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 管理后台 - 文件配置创建/修改请求 VO
 * <p>
 * 该类用于创建或修改文件配置，包含配置名、存储器类型、存储配置（动态参数）、
 * 备注等信息。存储配置使用 `Map<String, Object>` 类型以支持动态参数。
 * </p>
 */
@Schema(description = "管理后台 - 文件配置创建/修改 Request VO")
@Data
public class FileConfigSaveReqVO {

    /**
     * 编号
     * <p>
     * 文件配置的唯一标识符，通常用于在系统中进行操作（如更新、删除等）。
     * </p>
     */
    @Schema(description = "编号", example = "1")
    private Long id;

    /**
     * 配置名
     * <p>
     * 文件配置的名称，用于标识不同的文件配置。
     * </p>
     */
    @Schema(description = "配置名", requiredMode = Schema.RequiredMode.REQUIRED, example = "S3 - 阿里云")
    @NotNull(message = "配置名不能为空")
    private String name;

    /**
     * 存储器类型
     * <p>
     * 存储器的类型，参见 `FileStorageEnum` 枚举类，标识该配置使用的存储类型。
     * </p>
     */
    @Schema(description = "存储器，参见 FileStorageEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "存储器不能为空")
    private Integer storage;

    /**
     * 存储配置
     * <p>
     * 存储配置使用 `Map<String, Object>` 类型，支持动态参数配置，
     * 可以根据不同存储器的需求传递不同的配置项。
     * </p>
     */
    @Schema(description = "存储配置,配置是动态参数，所以使用 Map 接收", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "存储配置不能为空")
    private Map<String, Object> config;

    /**
     * 备注
     * <p>
     * 文件配置的备注信息，用于描述该配置的附加信息或特殊用途。
     * </p>
     */
    @Schema(description = "备注", example = "我是备注")
    private String remark;

}
