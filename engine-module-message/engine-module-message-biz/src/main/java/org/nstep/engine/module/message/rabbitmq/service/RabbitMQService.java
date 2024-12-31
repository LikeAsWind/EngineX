package org.nstep.engine.module.message.rabbitmq.service;

/**
 * RabbitMQ 服务接口
 * <p>
 * 该接口定义了消息发送的核心方法，用于通过 RabbitMQ 发送任务上下文消息。实现类需要根据任务的上下文和发送指令（发送或撤回）来执行具体的消息发送逻辑。
 * 消息发送的方式、内容和类型（如发送或撤回）由实现类具体定义。
 */
public interface RabbitMQService {

    /**
     * 发送任务消息到 RabbitMQ
     * <p>
     * 该方法接受任务上下文（JSON 格式）和发送指令（发送还是撤回）作为参数，将消息发送到 RabbitMQ 中。
     * 具体发送行为由实现类根据 `sendCode` 来决定。如果是发送任务，消息将被放入队列；如果是撤回任务，则会从队列中移除对应的消息。
     *
     * @param json     发送任务的上下文，通常为 JSON 字符串，包含任务的详细信息。
     * @param sendCode 发送还是撤回的标识符。可以是一个简单的字符串（例如 `"send"` 或 `"revoke"`），用来标识任务的操作类型。
     */
    void send(String json, String sendCode);
}
