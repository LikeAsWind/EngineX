package org.nstep.engine.framework.tenant.core.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;
import org.nstep.engine.framework.web.core.util.WebFrameworkUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 多租户 Context Web 过滤器
 * <p>
 * 该过滤器会从 HTTP 请求的 Header 中解析出租户编号（tenant-id），并将其设置到 {@link TenantContextHolder} 中。
 * 这样后续的数据库操作等就能获取到租户编号，从而实现多租户隔离。
 */
public class TenantContextWebFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
            throws ServletException, IOException {
        // 从请求的 Header 中获取租户编号
        Long tenantId = WebFrameworkUtils.getTenantId(request);
        if (tenantId != null) {
            // 设置到 TenantContextHolder 中，供后续操作使用
            TenantContextHolder.setTenantId(tenantId);
        }
        try {
            // 继续执行请求链
            chain.doFilter(request, response);
        } finally {
            // 清理租户上下文，防止跨请求污染
            TenantContextHolder.clear();
        }
    }

}
