package org.nstep.engine.module.message.domain.content;

import lombok.*;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;

/**
 * 定时任务上下文类，继承自ProcessContent。
 * 该类用于表示定时任务的上下文信息，包含与任务相关的模板和发送方信息。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CronTaskContent extends ProcessContent {

    /**
     * 任务消息的模板对象，用于生成定时任务相关的消息内容。
     * 该字段保存了与定时任务相关的模板信息，可能用于消息发送等操作。
     */
    private TemplateDO messageTemplate;

    /**
     * 发送方的ID，用于标识任务的发送者。
     * 发送方通常是系统中负责发起定时任务的用户或系统组件的标识。
     */
    private Long sender;
}
