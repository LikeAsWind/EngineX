package org.nstep.engine.framework.tenant.core.mq.rabbitmq;

import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

import static org.nstep.engine.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * RabbitMQ 消息队列的多租户 {@link MessagePostProcessor} 实现类
 * <p>
 * 该类实现了 {@link MessagePostProcessor} 接口，用于在消息发送前处理消息。
 * 它的作用是将当前租户的租户编号（tenantId）添加到消息的 Header 中，以支持多租户环境下的消息传递。
 * <p>
 * 1. 在生产者发送消息时，将 {@link TenantContextHolder} 中的租户编号添加到消息的 Header 中。
 * 2. 在消费者消费消息时，通过消息的 Header 获取租户编号，并将其设置到 {@link TenantContextHolder} 中，
 * 以确保消费端能够正确处理不同租户的数据。
 */
public class TenantRabbitMQMessagePostProcessor implements MessagePostProcessor {

    /**
     * 处理消息，在消息发送前将租户编号添加到消息的 Header 中。
     *
     * @param message 要发送的消息
     * @return 处理后的消息，包含租户编号的 Header
     * @throws AmqpException 如果处理消息时发生异常
     */
    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        // 获取当前租户的租户编号
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            // 将租户编号添加到消息的 Header 中
            message.getMessageProperties().getHeaders().put(HEADER_TENANT_ID, tenantId);
        }
        return message; // 返回处理后的消息
    }
}
