package org.nstep.engine.module.message.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static org.nstep.engine.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 消息模板信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TemplatePageReqVO extends PageParam {

    @Schema(description = "标题", example = "李四")
    private String name;

    @Schema(description = "当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败", example = "1")
    private Integer msgStatus;

    @Schema(description = "推送类型：10.实时 20.定时", example = "1")
    private Integer pushType;

    @Schema(description = "定时任务Id (xxl-job-admin返回)", example = "15129")
    private Long cronTaskId;

    @Schema(description = "定时发送人群的文件路径")
    private String cronCrowdPath;

    @Schema(description = "期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private String[] expectPushTime;

    @Schema(description = "消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人")
    private Integer sendChannel;

    @Schema(description = "消息内容 占位符用${var}表示")
    private String msgContent;

    @Schema(description = "发送账号 一个渠道下可存在多个账号", example = "24337")
    private Integer sendAccount;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "10.通知类消息 20.营销类消息 30.验证码类消息", example = "1")
    private Integer msgType;

    @Schema(description = "当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝", example = "2")
    private Integer auditStatus;

    @Schema(description = "当前定时模板使用用户id", example = "11734")
    private Long currentId;

}