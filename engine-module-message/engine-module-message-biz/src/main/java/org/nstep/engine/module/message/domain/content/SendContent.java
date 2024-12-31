package org.nstep.engine.module.message.domain.content;


import lombok.*;
import org.nstep.engine.module.message.domain.SendTaskInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 真正用于发送的上下文对象
 * <p>
 * 该类封装了发送任务的上下文信息，继承自 `ProcessContent` 类。它包含了任务 ID、发送标识、渠道类型、发送任务集合、发送时间、日志等信息。
 * 主要用于在任务执行过程中传递和存储与发送相关的上下文数据。
 * </p>
 */
@EqualsAndHashCode(callSuper = true)  // 继承父类的 equals 和 hashCode 方法
@Data  // 自动生成 getter、setter、toString、equals 和 hashCode 方法
@Builder  // 提供构建者模式，方便创建对象
@AllArgsConstructor  // 自动生成带有所有字段的构造函数
@NoArgsConstructor  // 自动生成无参构造函数
public class SendContent extends ProcessContent {

    /**
     * 任务id
     * <p>
     * 这是该发送任务的唯一标识符，用于区分不同的发送任务。
     * </p>
     */
    private Long sendTaskId;

    /**
     * 发送标识码 1.发送 2.撤回
     * <p>
     * 该字段用于标识发送操作的类型，值为 1 表示发送任务，值为 2 表示撤回任务。
     * </p>
     */
    private String sendCode;

    /**
     * 渠道类型
     * <p>
     * 该字段表示消息发送的渠道类型，通过该值可以确定是通过哪种渠道（如短信、邮件等）发送消息。
     * </p>
     */
    private Integer sendChannel;

    /**
     * 发送任务集合
     * <p>
     * 该字段包含了所有需要发送的任务信息，每个任务包含了具体的发送内容、接收者等信息。
     * </p>
     */
    private List<SendTaskInfo> sendTasks;

    /**
     * 发送时间
     * <p>
     * 该字段记录任务发送的时间，用于追踪任务的发送时刻。
     * </p>
     */
    private LocalDateTime sendTime;

    /**
     * 发送日志 如发送失败可存储报错信息
     * <p>
     * 该字段用于存储发送过程中的日志信息。如果发送失败，可以记录错误信息，方便后续排查问题。
     * </p>
     */
    private String sendLogs;

    /**
     * 发送方
     * <p>
     * 该字段记录发送任务的发送者的标识符，通常是用户 ID 或者系统 ID。
     * </p>
     */
    private Long sender;

}
