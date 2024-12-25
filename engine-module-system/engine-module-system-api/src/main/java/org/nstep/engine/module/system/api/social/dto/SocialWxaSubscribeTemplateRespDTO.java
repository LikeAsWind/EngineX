package org.nstep.engine.module.system.api.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 小程序订阅消息模板响应 DTO
 * <p>
 * 该类用于返回小程序订阅消息模板的相关信息，包括模板编号、标题、内容、示例以及模板类型。
 */
@Schema(description = "RPC 服务 - 小程序订阅消息模版 Response DTO")
@Data
public class SocialWxaSubscribeTemplateRespDTO {

    /**
     * 模板编号
     * <p>
     * 唯一标识订阅消息模板的编号。
     */
    @Schema(description = "模版编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String id;

    /**
     * 模板标题
     * <p>
     * 描述订阅消息模板的标题。
     */
    @Schema(description = "模版标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "模版标题")
    private String title;

    /**
     * 模板内容
     * <p>
     * 存储模板的实际内容，描述消息的结构和格式。
     */
    @Schema(description = "模版内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "模版内容")
    private String content;

    /**
     * 模板内容示例
     * <p>
     * 提供一个示例，展示模板内容的实际应用。
     */
    @Schema(description = "模板内容示例", requiredMode = Schema.RequiredMode.REQUIRED, example = "模版内容示例")
    private String example;

    /**
     * 模板类型
     * <p>
     * 用于标识模板的类型。2表示一次性订阅，3表示长期订阅。
     */
    @Schema(description = "模版类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer type; // 2：为一次性订阅；3：为长期订阅
}
