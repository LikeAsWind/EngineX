package org.nstep.engine.module.system.api.dict.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典数据响应 DTO
 * <p>
 * 该类用于表示字典数据的响应信息，包含字典标签、字典值、字典类型和状态等信息。
 */
@Schema(description = "RPC 服务 - 字典数据 Response DTO")
@Data
public class DictDataRespDTO {

    /**
     * 字典标签
     * <p>
     * 例如，性别字典的标签可以是 "engine"。
     */
    @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
    private String label;

    /**
     * 字典值
     * <p>
     * 例如，性别字典的值可以是 "nstep"。
     */
    @Schema(description = "字典值", requiredMode = Schema.RequiredMode.REQUIRED, example = "nstep")
    private String value;

    /**
     * 字典类型
     * <p>
     * 用于标识字典的类型，例如 "sys_common_sex"。
     */
    @Schema(description = "字典类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_common_sex")
    private String dictType;

    /**
     * 状态
     * <p>
     * 用于标识字典数据的状态，例如 1 表示有效，0 表示无效。具体状态参见 `CommonStatusEnum` 枚举。
     */
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status; // 参见 CommonStatusEnum 枚举

}
