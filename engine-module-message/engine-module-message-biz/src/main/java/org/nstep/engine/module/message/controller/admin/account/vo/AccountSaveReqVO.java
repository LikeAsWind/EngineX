package org.nstep.engine.module.message.controller.admin.account.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 渠道配置信息新增/修改请求对象
 * 用于接收新增或修改渠道配置信息时的请求数据，包含主键、账号名称、消息发送渠道、渠道配置等字段。
 */
@Schema(description = "管理后台 - 渠道配置信息新增/修改 Request VO")
@Data
public class AccountSaveReqVO {

    /**
     * 主键
     * 用于唯一标识渠道配置信息记录，在新增时可以为空，修改时必填
     * 示例：32578
     */
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32578")
    private Long id;

    /**
     * 账号名称
     * 用于标识渠道配置的账号名称
     * 示例：赵六
     * 此字段不能为空
     */
    @Schema(description = "账号名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "账号名称不能为空") // 校验字段不能为空
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
     * 此字段不能为空
     */
    @Schema(description = "消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人不能为空")
    // 校验字段不能为空
    private Integer sendChannel;

    /**
     * 渠道配置编码
     * 用于标识不同渠道的配置代码
     * 此字段不能为空
     */
    @Schema(description = "渠道code配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "渠道code配置不能为空") // 校验字段不能为空
    private String accountConfig;

}
