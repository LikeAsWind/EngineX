package org.nstep.engine.module.message.dto.message;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 最后真正需要发送的任务信息
 * <p>
 * 该类封装了最终需要发送的任务信息。每个任务包含了消息 ID、接收者、消息模板以及发送相关的时间信息。
 * 如果多个接收者的消息内容完全相同，则会将其封装为一个 `TemplateSendTask` 对象。
 * </p>
 */
@Data  // 自动生成 getter、setter、toString、equals 和 hashCode 方法
@Builder  // 提供构建者模式，方便创建对象
@AllArgsConstructor  // 自动生成带有所有字段的构造函数
@NoArgsConstructor  // 自动生成无参构造函数
public class TemplateSendTask {

    /**
     * 消息id(不是模板id)
     * <p>
     * 这是唯一标识消息的 ID，用于区分不同的消息任务。
     * </p>
     */
    private Long messageId;

    /**
     * 接受者
     * <p>
     * 这是一个 Set 集合，包含所有接收该消息的接收者的标识。
     * </p>
     */
    private Set<String> receivers;

    /**
     * 共同消息模板(占位符数据已填充完毕)
     * <p>
     * 这是用于构建消息内容的模板对象，所有接收者将使用相同的模板来生成消息内容。
     * 占位符数据已在此模板中填充完毕。
     * </p>
     */
    private TemplateDO messageTemplate;

    /**
     * 用于查询redis中的发送任务
     * <p>
     * 这是一个字符串，作为查询 Redis 中发送任务的唯一标识。
     * </p>
     */
    public String sendMessageKey;

    /**
     * 用于查询发送任务中的子任务
     * <p>
     * 这是发送任务的子任务 ID，用于区分不同的子任务。
     * </p>
     */
    public Long sendTaskId;

    /**
     * 发送阶段开始时间
     * <p>
     * 记录发送任务开始的时间，用于计算任务的执行时长。
     * </p>
     */
    private LocalDateTime sendStartTime;

    /**
     * 结束时间
     * <p>
     * 记录发送任务结束的时间，用于计算任务的执行时长。
     * </p>
     */
    private LocalDateTime sendEndTime;

    /**
     * 发送阶段总耗时
     * <p>
     * 记录发送任务从开始到结束所消耗的总时间（单位：毫秒）。
     * </p>
     */
    private long takeTime;

}
