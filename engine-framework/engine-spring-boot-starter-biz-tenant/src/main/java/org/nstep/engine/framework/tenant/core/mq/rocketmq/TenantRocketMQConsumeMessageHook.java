package org.nstep.engine.framework.tenant.core.mq.rocketmq;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.common.message.MessageExt;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;

import java.util.List;

import static org.nstep.engine.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * RocketMQ 消息队列的多租户 {@link ConsumeMessageHook} 实现类
 * <p>
 * 该类实现了 RocketMQ 的 {@link ConsumeMessageHook} 接口，用于在消费消息时处理租户上下文。
 * 它确保在消费消息时，从消息的 Header 中获取租户编号，并将其设置到 {@link TenantContextHolder} 中，
 * 使得消费者能够在多租户环境下正确处理每个租户的数据。
 * <p>
 * 1. 在消费者消费消息之前，从消息的 Header 中获取租户编号，并设置到 {@link TenantContextHolder} 中。
 * 2. 在消费完成后，清理租户上下文，避免影响后续的消息处理。
 */
public class TenantRocketMQConsumeMessageHook implements ConsumeMessageHook {

    /**
     * 返回当前 Hook 的名称
     *
     * @return Hook 名称
     */
    @Override
    public String hookName() {
        return getClass().getSimpleName();
    }

    /**
     * 消费消息之前，设置租户编号到 {@link TenantContextHolder} 中。
     * <p>
     * 消息必须是单条，否则无法正确设置租户信息。
     *
     * @param context 消费消息的上下文
     */
    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {
        // 校验消息条数，确保是单条消息
        List<MessageExt> messages = context.getMsgList();
        Assert.isTrue(messages.size() == 1, "消息条数({})不正确", messages.size());

        // 获取消息的租户编号并设置到 TenantContextHolder 中
        String tenantId = messages.get(0).getUserProperty(HEADER_TENANT_ID);
        if (StrUtil.isNotEmpty(tenantId)) {
            TenantContextHolder.setTenantId(Long.parseLong(tenantId));
        }
    }

    /**
     * 消费消息之后，清理租户上下文。
     * <p>
     * 这是为了确保每个消息的处理是独立的，避免租户上下文泄漏到后续的消息处理中。
     *
     * @param context 消费消息的上下文
     */
    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {
        // 清理租户上下文
        TenantContextHolder.clear();
    }
}
