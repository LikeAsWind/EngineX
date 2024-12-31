package org.nstep.engine.module.message.service.template;

import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.message.dto.content.ProcessContent;

/**
 * 消息管理服务接口
 * <p>
 * 该接口定义了消息管理的核心功能，提供消息发送及任务启动、停止的服务。
 * 不同的实现类可以根据业务需求实现具体的逻辑。
 */
public interface MessageManagementService {

    /**
     * 发送消息
     *
     * @param sendForm 包含消息发送所需的上下文信息，包含消息内容、发送目标等。
     * @return 返回消息发送的处理结果，包含成功或失败的信息。
     */
    CommonResult<?> send(ProcessContent sendForm);

    /**
     * 启动任务
     * <p>
     * 根据任务 ID 启动对应的消息任务。
     *
     * @param id 消息任务的唯一标识 ID。
     */
    void start(Long id);

    /**
     * 停止任务
     * <p>
     * 根据任务 ID 停止对应的消息任务。
     *
     * @param id 消息任务的唯一标识 ID。
     */
    void stop(Long id);
}
