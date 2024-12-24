package org.nstep.engine.framework.tenant.core.mq.rocketmq;

import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.hook.SendMessageHook;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;

import static org.nstep.engine.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * RocketMQ 消息队列的多租户 {@link SendMessageHook} 实现类
 * <p>
 * 该类在 Producer 发送消息时，将 {@link TenantContextHolder} 中的租户编号，添加到消息的 Header 中。
 */
public class TenantRocketMQSendMessageHook implements SendMessageHook {

    /**
     * 获取当前钩子的名称
     *
     * @return 钩子名称
     */
    @Override
    public String hookName() {
        return getClass().getSimpleName();
    }

    /**
     * 在发送消息之前，将租户编号添加到消息的 Header 中
     *
     * @param sendMessageContext 发送消息的上下文
     */
    @Override
    public void sendMessageBefore(SendMessageContext sendMessageContext) {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            // 将租户编号添加到消息的 Header 中
            sendMessageContext.getMessage().putUserProperty(HEADER_TENANT_ID, tenantId.toString());
        }
    }

    /**
     * 发送消息之后的操作（此处不需要做任何操作）
     *
     * @param sendMessageContext 发送消息的上下文
     */
    @Override
    public void sendMessageAfter(SendMessageContext sendMessageContext) {
        // 目前不需要处理发送后的逻辑
    }
}
