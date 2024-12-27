package org.nstep.engine.module.message.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
    import org.springframework.format.annotation.DateTimeFormat;
    import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 消息模板信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TemplateRespVO {

            @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "24546")
                @ExcelProperty("编号")
            private Long id;

            @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
                @ExcelProperty("标题")
            private String name;

            @Schema(description = "当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
                @ExcelProperty("当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败")
            private Integer msgStatus;

            @Schema(description = "推送类型：10.实时 20.定时", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
                @ExcelProperty("推送类型：10.实时 20.定时")
            private Integer pushType;

            @Schema(description = "定时任务Id (xxl-job-admin返回)", example = "15129")
                @ExcelProperty("定时任务Id (xxl-job-admin返回)")
            private Long cronTaskId;

            @Schema(description = "定时发送人群的文件路径")
                @ExcelProperty("定时发送人群的文件路径")
            private String cronCrowdPath;

            @Schema(description = "期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式")
                @ExcelProperty("期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式")
            private String expectPushTime;

            @Schema(description = "消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人", requiredMode = Schema.RequiredMode.REQUIRED)
                @ExcelProperty("消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人")
            private Integer sendChannel;

            @Schema(description = "消息内容 占位符用${var}表示", requiredMode = Schema.RequiredMode.REQUIRED)
                @ExcelProperty("消息内容 占位符用${var}表示")
            private String msgContent;

            @Schema(description = "发送账号 一个渠道下可存在多个账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "24337")
                @ExcelProperty("发送账号 一个渠道下可存在多个账号")
            private Integer sendAccount;

            @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
                @ExcelProperty("创建时间")
            private LocalDateTime createTime;

            @Schema(description = "10.通知类消息 20.营销类消息 30.验证码类消息", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
                @ExcelProperty("10.通知类消息 20.营销类消息 30.验证码类消息")
            private Integer msgType;

            @Schema(description = "当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
                @ExcelProperty("当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝")
            private Integer auditStatus;

            @Schema(description = "当前定时模板使用用户id", example = "11734")
                @ExcelProperty("当前定时模板使用用户id")
            private Long currentId;

}