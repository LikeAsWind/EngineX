package org.nstep.engine.module.message.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static org.nstep.engine.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 管理后台 - 消息模板信息分页请求对象
 * 用于分页查询消息模板信息，包含多个查询条件如标题、消息状态、推送类型等。
 */
@Schema(description = "管理后台 - 消息模板信息分页 Request VO")
@Data // 自动生成 getter, setter, toString, equals, hashCode 方法
@EqualsAndHashCode(callSuper = true) // 生成 equals 和 hashCode 方法，继承父类的实现
@ToString(callSuper = true) // 生成 toString 方法，继承父类的实现
public class TemplatePageReqVO extends PageParam {

    @Schema(description = "标题", example = "李四") // 描述字段和示例值
    private String name;

    @Schema(description = "当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败", example = "1")
    private Integer msgStatus; // 消息的当前状态

    @Schema(description = "推送类型：10.实时 20.定时", example = "1")
    private Integer pushType; // 推送类型，实时或定时

    @Schema(description = "定时任务Id (xxl-job-admin返回)", example = "15129")
    private Long cronTaskId; // 定时任务的 ID，来自 xxl-job-admin

    @Schema(description = "定时发送人群的文件路径")
    private String cronCrowdPath; // 定时发送的目标人群文件路径

    @Schema(description = "期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND) // 日期时间格式化
    private String[] expectPushTime; // 期望的发送时间，可以是立即发送或定时任务的 cron 表达式

    @Schema(description = "消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人")
    private Integer sendChannel; // 消息发送的渠道

    @Schema(description = "消息内容 占位符用${var}表示")
    private String msgContent; // 消息的内容，支持占位符

    @Schema(description = "发送账号 一个渠道下可存在多个账号", example = "24337")
    private Integer sendAccount; // 发送账号，支持一个渠道下多个账号

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND) // 日期时间格式化
    private LocalDateTime[] createTime; // 创建时间范围，用于筛选

    @Schema(description = "10.通知类消息 20.营销类消息 30.验证码类消息", example = "1")
    private Integer msgType; // 消息类型，通知类、营销类或验证码类

    @Schema(description = "当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝", example = "2")
    private Integer auditStatus; // 消息的审核状态，待审核、审核成功或被拒绝

    @Schema(description = "当前定时模板使用用户id", example = "11734")
    private Long currentId; // 当前使用该定时模板的用户 ID
}
