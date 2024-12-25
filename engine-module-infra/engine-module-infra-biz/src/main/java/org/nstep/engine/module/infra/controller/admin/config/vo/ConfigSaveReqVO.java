package org.nstep.engine.module.infra.controller.admin.config.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理后台 - 参数配置创建/修改 Request VO
 * <p>
 * 用于管理后台创建或修改参数配置信息的请求数据传输对象，包含了参数的详细信息，如分组、名称、键名、键值、可见性等。
 * </p>
 */
@Schema(description = "管理后台 - 参数配置创建/修改 Request VO")
@Data
public class ConfigSaveReqVO {

    /**
     * 参数配置序号
     * <p>
     * 用于标识更新时的目标配置项的唯一 ID，创建时可为空。
     * </p>
     */
    @Schema(description = "参数配置序号", example = "1024")
    private Long id;

    /**
     * 参数分组
     * <p>
     * 用于标识参数所属的分组。不能为空，最大长度为 50 个字符。
     * </p>
     */
    @Schema(description = "参数分组", requiredMode = Schema.RequiredMode.REQUIRED, example = "biz")
    @NotEmpty(message = "参数分组不能为空")
    @Size(max = 50, message = "参数名称不能超过 50 个字符")
    private String category;

    /**
     * 参数名称
     * <p>
     * 用于标识参数的名称。不能为空，最大长度为 100 个字符。
     * </p>
     */
    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "数据库名")
    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称不能超过 100 个字符")
    private String name;

    /**
     * 参数键名
     * <p>
     * 用于标识参数的唯一键名。不能为空，最大长度为 100 个字符。
     * </p>
     */
    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED, example = "yunai.db.username")
    @NotBlank(message = "参数键名长度不能为空")
    @Size(max = 100, message = "参数键名长度不能超过 100 个字符")
    private String key;

    /**
     * 参数键值
     * <p>
     * 用于存储参数的实际值。不能为空，最大长度为 500 个字符。
     * </p>
     */
    @Schema(description = "参数键值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值长度不能超过 500 个字符")
    private String value;

    /**
     * 是否可见
     * <p>
     * 标识参数是否可见。不能为空。
     * </p>
     */
    @Schema(description = "是否可见", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否可见不能为空")
    private Boolean visible;

    /**
     * 备注
     * <p>
     * 可选字段，用于存储参数的附加说明信息。
     * </p>
     */
    @Schema(description = "备注", example = "备注一下很帅气！")
    private String remark;
}
