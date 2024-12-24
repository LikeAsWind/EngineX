package org.nstep.engine.framework.tenant.core.rpc;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;

import static org.nstep.engine.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Tenant 的 RequestInterceptor 实现类：Feign 请求时，将 {@link TenantContextHolder} 设置到 header 中，继续透传给被调用的服务
 */
public class TenantRequestInterceptor implements RequestInterceptor {

    // 重写 apply 方法，Feign 在发起请求时会调用该方法
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 从 TenantContextHolder 获取当前租户的租户编号
        Long tenantId = TenantContextHolder.getTenantId();

        // 如果租户 ID 不为空，则将其添加到请求头中
        if (tenantId != null) {
            // 在请求头中添加租户 ID，header 的名称为 HEADER_TENANT_ID
            requestTemplate.header(HEADER_TENANT_ID, String.valueOf(tenantId));
        }
    }
}
