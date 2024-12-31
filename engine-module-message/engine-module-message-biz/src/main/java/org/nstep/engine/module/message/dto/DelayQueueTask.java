package org.nstep.engine.module.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 延迟队列任务
 * <p>
 * 该类表示延迟队列中的任务对象，通常用于存储需要延迟处理的消息或任务。通过该对象，
 * 我们可以在延迟队列中对任务进行管理。任务的属性包括消息的 Redis 键、发送任务 ID 和消息 ID。
 * <p>
 * 使用 Lombok 注解简化了类的创建过程，包括构造函数、Getter/Setter 方法、Builder 模式等。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DelayQueueTask {

    /**
     * 消息在 Redis 中的键
     * <p>
     * 该属性用于表示该消息在 Redis 中的唯一标识符，通常作为 Redis 存储的键。
     * 使用该键可以方便地从 Redis 中获取或删除相应的消息数据。
     */
    private String messageRedisKey;

    /**
     * 发送任务的 ID
     * <p>
     * 该属性用于标识发送任务的唯一 ID。通常，每个任务都有一个唯一的 ID，
     * 用于追踪和管理发送过程中的状态，确保任务按顺序执行。
     */
    private Long sendTaskId;

    /**
     * 消息的 ID
     * <p>
     * 该属性表示消息的唯一标识符。每个消息都会有一个唯一的 ID，用于区分不同的消息，
     * 并且通常与实际的消息内容或其他相关数据一一对应。
     */
    private Long messageId;
}
