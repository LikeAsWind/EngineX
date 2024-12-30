package org.nstep.engine.module.message.controller.admin.template.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 消息模板信息响应对象
 * 用于返回消息模板的详细信息，包括模板编号、标题、状态、推送类型等。
 */
@Schema(description = "管理后台 - 消息模板信息 Response VO")
@Data // 自动生成 getter, setter, toString, equals, hashCode 方法
@ExcelIgnoreUnannotated // 忽略未注解的字段，防止它们被导出到 Excel 中
public class TemplateRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "24546")
    @ExcelProperty("编号") // Excel 导出时的字段名称
    private Long id; // 模板的唯一编号

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("标题")
    private String name; // 消息模板的标题

    @Schema(description = "当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败")
    private Integer msgStatus; // 当前消息的状态

    @Schema(description = "推送类型：10.实时 20.定时", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("推送类型：10.实时 20.定时")
    private Integer pushType; // 推送类型，实时或定时

    @Schema(description = "定时发送人群的文件路径")
    @ExcelProperty("定时发送人群的文件路径")
    private String cronCrowdPath; // 定时发送人群的文件路径

    @Schema(description = "期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式")
    @ExcelProperty("期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式")
    private String expectPushTime; // 期望的发送时间，立即发送或 cron 表达式

    @Schema(description = "消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人")
    private Integer sendChannel; // 消息发送的渠道

    @Schema(description = "消息内容 占位符用${var}表示", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("消息内容 占位符用${var}表示")
    private String msgContent; // 消息内容，支持占位符格式（如 ${var}）

    @Schema(description = "发送账号 一个渠道下可存在多个账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "24337")
    @ExcelProperty("发送账号 一个渠道下可存在多个账号")
    private Integer sendAccount; // 发送账号，支持一个渠道下多个账号

    @Schema(description = "创建者")
    @ExcelProperty("创建者")
    private String creator; // 创建者信息

    @Schema(description = "更新者")
    @ExcelProperty("更新者")
    private String updator; // 更新者信息

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime; // 消息模板的创建时间

    @Schema(description = "10.通知类消息 20.营销类消息 30.验证码类消息", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("10.通知类消息 20.营销类消息 30.验证码类消息")
    private Integer msgType; // 消息类型：通知类、营销类或验证码类

    @Schema(description = "当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝")
    private Integer auditStatus; // 当前消息的审核状态：待审核、审核成功或被拒绝

    @Schema(description = "当前定时模板使用用户id", example = "11734")
    @ExcelProperty("当前定时模板使用用户id")
    private Long currentId; // 当前使用该定时模板的用户 ID
}
