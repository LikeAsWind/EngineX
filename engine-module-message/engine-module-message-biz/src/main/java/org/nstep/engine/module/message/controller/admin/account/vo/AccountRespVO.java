package org.nstep.engine.module.message.controller.admin.account.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 渠道配置信息响应结果对象
 * 用于返回渠道配置信息的响应数据，包括主键、账号名称、消息发送渠道、渠道配置和创建时间等字段。
 */
@Schema(description = "管理后台 - 渠道配置信息 Response VO")
@Data
@ExcelIgnoreUnannotated // 忽略未注解的字段，防止导出 Excel 时包含不需要的字段
public class AccountRespVO {

    /**
     * 主键
     * 用于唯一标识渠道配置信息记录
     * 示例：32578
     */
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32578")
    @ExcelProperty("主键") // 用于导出 Excel 时的列标题
    private Long id;

    /**
     * 账号名称
     * 用于标识渠道配置的账号名称
     * 示例：赵六
     */
    @Schema(description = "账号名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("账号名称") // 用于导出 Excel 时的列标题
    private String name;

    /**
     * 消息发送渠道
     * 用于指定消息发送渠道的类型，取值如下：
     * 10 - Email
     * 20 - 短信
     * 30 - 钉钉机器人
     * 40 - 微信服务号
     * 50 - push通知栏
     * 60 - 飞书机器人
     */
    @Schema(description = "消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人")
    // 用于导出 Excel 时的列标题
    private Integer sendChannel;

    /**
     * 渠道配置编码
     * 用于标识不同渠道的配置代码
     */
    @Schema(description = "渠道code配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("渠道code配置") // 用于导出 Excel 时的列标题
    private String accountConfig;

    /**
     * 创建时间
     * 用于标识渠道配置信息的创建时间
     */
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间") // 用于导出 Excel 时的列标题
    private LocalDateTime createTime;

}
