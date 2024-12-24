package org.nstep.engine.framework.tenant.core.mq.redis;

import cn.hutool.core.util.StrUtil;
import org.nstep.engine.framework.mq.redis.core.interceptor.RedisMessageInterceptor;
import org.nstep.engine.framework.mq.redis.core.message.AbstractRedisMessage;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;

import static org.nstep.engine.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * 多租户 {@link AbstractRedisMessage} 拦截器
 * <p>
 * 该类实现了 {@link RedisMessageInterceptor} 接口，用于在 Redis 消息发送和消费过程中处理租户上下文。
 * 它确保在消息的发送和消费过程中正确地携带和设置租户信息，支持多租户环境下的消息传递。
 * <p>
 * 1. 在生产者发送消息时，将 {@link TenantContextHolder} 中的租户编号添加到消息的 Header 中。
 * 2. 在消费者消费消息时，从消息的 Header 中获取租户编号，并将其设置到 {@link TenantContextHolder} 中。
 */
public class TenantRedisMessageInterceptor implements RedisMessageInterceptor {

    /**
     * 在发送消息之前，向消息的 Header 中添加租户编号。
     *
     * @param message 要发送的 Redis 消息
     */
    @Override
    public void sendMessageBefore(AbstractRedisMessage message) {
        // 获取当前租户的租户编号
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            // 将租户编号添加到消息的 Header 中
            message.addHeader(HEADER_TENANT_ID, tenantId.toString());
        }
    }

    /**
     * 在消费消息之前，从消息的 Header 中获取租户编号，并设置到 TenantContextHolder 中。
     *
     * @param message 被消费的 Redis 消息
     */
    @Override
    public void consumeMessageBefore(AbstractRedisMessage message) {
        // 获取消息中的租户编号
        String tenantIdStr = message.getHeader(HEADER_TENANT_ID);
        if (StrUtil.isNotEmpty(tenantIdStr)) {
            // 将租户编号设置到 TenantContextHolder 中
            TenantContextHolder.setTenantId(Long.valueOf(tenantIdStr));
        }
    }

    /**
     * 在消费消息之后，清除租户上下文。
     * <p>
     * 这是为了确保每个消息的处理是独立的，避免租户上下文泄漏到后续的消息处理中。
     *
     * @param message 被消费的 Redis 消息
     */
    @Override
    public void consumeMessageAfter(AbstractRedisMessage message) {
        // 清除租户上下文
        TenantContextHolder.clear();
    }
}
