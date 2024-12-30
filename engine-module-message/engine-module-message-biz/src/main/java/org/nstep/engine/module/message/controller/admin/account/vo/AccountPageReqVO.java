package org.nstep.engine.module.message.controller.admin.account.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static org.nstep.engine.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 管理后台 - 渠道配置信息分页请求参数对象
 * 用于分页查询渠道配置信息，包含了渠道信息、账号名称、创建时间等筛选条件。
 */
@Schema(description = "管理后台 - 渠道配置信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true) // 继承 PageParam 并比较父类属性
@ToString(callSuper = true) // 输出时包括父类的属性
public class AccountPageReqVO extends PageParam {

    /**
     * 账号名称
     * 用于根据账号名称进行查询，支持模糊匹配
     * 示例：赵六
     */
    @Schema(description = "账号名称", example = "赵六")
    private String name;

    /**
     * 消息发送渠道
     * 该字段用于指定消息发送渠道的类型，取值如下：
     * 10 - Email
     * 20 - 短信
     * 30 - 钉钉机器人
     * 40 - 微信服务号
     * 50 - push通知栏
     * 60 - 飞书机器人
     */
    @Schema(description = "消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人")
    private Integer sendChannel;

    /**
     * 渠道配置的编码
     * 用于标识不同渠道的配置代码
     */
    @Schema(description = "渠道code配置")
    private String accountConfig;

    /**
     * 创建时间范围
     * 用于根据创建时间进行查询，支持时间区间查询
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND) // 格式化日期时间
    private LocalDateTime[] createTime;

}
