package org.nstep.engine.module.message.controller.admin.template.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.nstep.engine.module.message.domain.content.ProcessContent;

/**
 * 对应前端发送表单对象=
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "管理后台 - 消息发送VO Request VO")
public class TemplateSendReqVO extends ProcessContent {

    @Schema(description = "模板编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "24546")
    @NotEmpty(message = "模板编号不能为空")
    private Long messageTemplateId;
    /**
     * 接受者集合
     */
    @Schema(description = "接受者集合", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "接受者集合不能为空")
    private String receivers;
    /**
     * 占位符集合
     */
    @Schema(description = "占位符集合", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "占位符集合不能为空")
    private String variables;

    /**
     * 渠道类型
     */
    @Schema(description = "消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotEmpty(message = "渠道类型不能为空")
    private Integer sendChannel;

    /**
     * 是否带有占位符数据 0.没有 其他.占位符数量
     */
    @Schema(description = "是否带有占位符数据 0.没有 其他.占位符数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotEmpty(message = "是否带有占位符数据不能为空")
    private Integer isExitVariables;

    /**
     * 发送方
     */
    @Schema(description = "渠道类型", example = "24546")
    private Long sender;
}
