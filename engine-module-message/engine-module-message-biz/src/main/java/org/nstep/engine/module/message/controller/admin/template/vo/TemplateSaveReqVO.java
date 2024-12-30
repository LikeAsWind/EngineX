package org.nstep.engine.module.message.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 消息模板信息新增/修改请求对象
 * 用于创建或更新消息模板，包含模板的各项信息，如标题、状态、推送类型、消息内容等。
 */
@Schema(description = "管理后台 - 消息模板信息新增/修改 Request VO")
@Data // 自动生成 getter, setter, toString, equals, hashCode 方法
public class TemplateSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "24546")
    private Long id; // 消息模板的唯一编号

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "标题不能为空") // 标题不能为空的验证
    private String name; // 消息模板的标题

    @Schema(description = "当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败不能为空")
    // 状态不能为空的验证
    private Integer msgStatus; // 当前消息的状态

    @Schema(description = "推送类型：10.实时 20.定时", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "推送类型：10.实时 20.定时不能为空") // 推送类型不能为空的验证
    private Integer pushType; // 推送类型，实时或定时

    @Schema(description = "定时任务Id (xxl-job-admin返回)", example = "15129")
    private Long cronTaskId; // 定时任务 ID，来自 xxl-job-admin

    @Schema(description = "定时发送人群的文件路径")
    private String cronCrowdPath; // 定时发送的目标人群文件路径

    @Schema(description = "期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式")
    private String expectPushTime; // 期望的发送时间，立即发送或 cron 表达式

    @Schema(description = "消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人不能为空")
    // 发送渠道不能为空的验证
    private Integer sendChannel; // 消息发送的渠道

    @Schema(description = "消息内容 占位符用${var}表示", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "消息内容 占位符用${var}表示不能为空") // 消息内容不能为空的验证
    private String msgContent; // 消息内容，支持占位符格式（如 ${var}）

    @Schema(description = "发送账号 一个渠道下可存在多个账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "24337")
    @NotNull(message = "发送账号 一个渠道下可存在多个账号不能为空") // 发送账号不能为空的验证
    private Integer sendAccount; // 发送账号，支持一个渠道下多个账号

    @Schema(description = "10.通知类消息 20.营销类消息 30.验证码类消息", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "10.通知类消息 20.营销类消息 30.验证码类消息不能为空") // 消息类型不能为空的验证
    private Integer msgType; // 消息类型：通知类、营销类或验证码类

    @Schema(description = "当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝不能为空") // 审核状态不能为空的验证
    private Integer auditStatus; // 当前消息的审核状态：待审核、审核成功或被拒绝

    @Schema(description = "当前定时模板使用用户id", example = "11734")
    private Long currentId; // 当前使用该定时模板的用户 ID
}
