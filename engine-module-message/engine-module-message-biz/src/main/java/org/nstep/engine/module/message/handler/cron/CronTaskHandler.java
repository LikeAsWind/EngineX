package org.nstep.engine.module.message.handler.cron;

/**
 * 定时任务处理器接口
 * 该接口定义了一个处理定时任务的统一方法，所有实现该接口的类需提供具体的处理逻辑。
 */
public interface CronTaskHandler {

    /**
     * 处理定时任务
     *
     * @param messageId 定时任务的消息ID，用于标识该任务
     * @param sender    定时任务的发送者ID，用于标识任务的来源
     */
    void Handler(Long messageId, Long sender);
}
