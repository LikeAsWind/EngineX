package org.nstep.engine.module.message.rabbitmq.consumer;


import org.nstep.engine.module.message.dto.content.SendContent;

/**
 * 消费者服务接口
 * <p>
 * 该接口定义了消费者服务的核心方法，主要用于消费发送消息的任务和撤回消息。具体的业务逻辑由实现类来完成。
 */
public interface ConsumerService {

    /**
     * 消费要发送的消息
     * <p>
     * 该方法用于处理和消费待发送的消息。实现类需要根据消息的内容进行相应的发送操作。
     *
     * @param sendContext 发送任务的上下文信息，包含了待发送的消息内容以及相关的元数据
     */
    void consumerSend(SendContent sendContext);

    /**
     * 撤回消息
     * <p>
     * 该方法用于撤回已经发送的消息。此功能尚未实现，因此需要在实现类中补充相应的逻辑。
     */
    void consumerRecall();
}
