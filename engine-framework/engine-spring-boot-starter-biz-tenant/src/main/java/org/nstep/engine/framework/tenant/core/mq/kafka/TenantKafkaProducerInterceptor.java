package org.nstep.engine.framework.tenant.core.mq.kafka;

import cn.hutool.core.util.ReflectUtil;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.util.Map;

/**
 * Kafka 消息队列的多租户 {@link ProducerInterceptor} 实现类
 * <p>
 * 该拦截器用于在 Kafka Producer 发送消息时，自动将当前租户的租户编号（tenantId）添加到消息的 Header 中，
 * 从而实现多租户的 Kafka 消息传递。
 * <p>
 * 1. 在 Producer 发送消息时，拦截器将 {@link TenantContextHolder} 中的租户编号添加到消息的 Header 中。
 * 2. 在 Consumer 消费消息时，租户编号会从消息的 Header 中提取，并设置到 {@link TenantContextHolder} 中，
 * 以便在消费过程中使用租户信息。该过程通过 {@link InvocableHandlerMethod} 实现。
 */
public class TenantKafkaProducerInterceptor implements ProducerInterceptor<Object, Object> {

    // 租户 ID 在 Kafka 消息头中的键
    private static final String HEADER_TENANT_ID = "tenant-id";

    /**
     * 在消息发送之前，拦截并添加租户信息到消息的 Header 中。
     * <p>
     * 获取当前租户 ID，如果存在，则将其添加到消息的 Header 中。
     * 这样，消费者在消费消息时可以通过消息头获取租户 ID，从而设置到 {@link TenantContextHolder} 中。
     *
     * @param record 要发送的 Kafka 消息
     * @return 修改后的 Kafka 消息
     */
    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> record) {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            // 使用反射获取 Kafka 消息的 headers 字段
            Headers headers = (Headers) ReflectUtil.getFieldValue(record, "headers");
            // 将租户 ID 添加到消息头部
            headers.add(HEADER_TENANT_ID, tenantId.toString().getBytes());
        }
        return record;
    }

    /**
     * 消息发送确认时调用的方法，当前实现不做任何处理。
     *
     * @param metadata  消息元数据
     * @param exception 消息发送过程中可能发生的异常
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        // 当前实现不需要对消息确认进行处理
    }

    /**
     * 关闭拦截器时调用的方法，当前实现不做任何处理。
     */
    @Override
    public void close() {
        // 当前实现不需要关闭任何资源
    }

    /**
     * 配置拦截器的相关设置，当前实现不需要进行任何配置。
     *
     * @param configs 配置项
     */
    @Override
    public void configure(Map<String, ?> configs) {
        // 当前实现不需要配置
    }
}

