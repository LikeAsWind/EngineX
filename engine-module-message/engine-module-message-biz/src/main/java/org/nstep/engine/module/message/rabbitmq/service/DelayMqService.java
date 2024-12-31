package org.nstep.engine.module.message.rabbitmq.service;

/**
 * 延迟消息队列服务接口
 * <p>
 * 该接口定义了延迟消息队列相关的操作。实现类应提供将消息发送到延迟队列的功能。
 * 延迟队列是一种消息队列，消息会在指定的时间后才会被消费。通常用于定时任务、超时任务等场景。
 */
public interface DelayMqService {

    /**
     * 将消息发送到延迟队列
     *
     * @param json    需要发送的消息内容，通常是 JSON 格式的字符串
     * @param expTime 消息的延迟时间，通常表示为一个过期时间，决定消息在队列中等待的时间
     */
    void send(String json, String expTime);
}
