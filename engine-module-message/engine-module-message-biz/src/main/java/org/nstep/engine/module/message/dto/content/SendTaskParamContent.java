package org.nstep.engine.module.message.dto.content;

import lombok.*;

import java.util.Map;
import java.util.Set;

/**
 * SendTaskParamContent 过渡对象
 * <p>
 * 该类用于存储发送任务的参数内容，包括消息模板ID、发送渠道、占位符数据、发送方等信息。
 * 它将用于将发送任务所需的数据封装起来，方便进行任务的管理与执行。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendTaskParamContent extends ProcessContent {

    /**
     * 消息模板ID
     * <p>
     * 该字段用于标识要使用的消息模板，通常与数据库中的模板表关联。
     * 通过该模板ID，可以确定发送的内容格式和占位符信息。
     */
    private Long messageTemplateId;

    /**
     * 渠道类型
     * <p>
     * 该字段表示消息的发送渠道类型，如短信、邮件、推送通知等。
     * 可以是一个整数值，代表不同的渠道。例如：1 - 短信，2 - 邮件，3 - 推送。
     */
    private Integer sendChannel;

    /**
     * 占位符数据与接受者集合
     * <p>
     * 这是一个键值对的映射，键是占位符的名称（例如 "{{name}}"），值是该占位符所需的数据。
     * 值是一个 Set 集合，表示这个占位符对应的多个接受者（如手机号或邮箱地址等）。
     * 例如：{"{{name}}": {"user1", "user2"}} 表示占位符 "{{name}}" 会被替换为 "user1" 和 "user2"。
     */
    private Map<String, Set<String>> sendTaskParams;

    /**
     * 是否带有占位符数据
     * <p>
     * 该字段用于指示消息内容中是否包含占位符数据。
     * 0 表示没有占位符数据，其他值表示占位符的数量。
     * 例如：如果有 2 个占位符，则该字段的值为 2。
     */
    private Integer isExitVariables;

    /**
     * 发送方ID
     * <p>
     * 该字段用于标识消息的发送方。发送方通常是指发送任务的用户、系统或者服务。
     * 例如：发送方可能是某个管理员、系统后台，或者是一个自动化脚本。
     */
    private Long sender;
}
