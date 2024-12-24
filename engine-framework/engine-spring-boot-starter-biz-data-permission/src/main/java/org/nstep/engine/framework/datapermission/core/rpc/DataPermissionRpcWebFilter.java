package org.nstep.engine.framework.datapermission.core.rpc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.datapermission.core.aop.DataPermissionContextHolder;
import org.nstep.engine.framework.datapermission.core.util.DataPermissionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * 用于处理RPC请求中的数据权限的Web过滤器。
 * 针对 {@link DataPermissionRequestInterceptor} 的 RPC 调用，设置 {@link DataPermissionContextHolder} 的上下文
 */

public class DataPermissionRpcWebFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
            throws ServletException, IOException {
        // 从请求头中获取数据权限启用状态。
        String enable = request.getHeader(DataPermissionRequestInterceptor.ENABLE_HEADER_NAME);
        // 如果请求头中指定数据权限为禁用（"false"）。
        if (Objects.equals(enable, Boolean.FALSE.toString())) {
            // 使用DataPermissionUtils.executeIgnore执行请求，忽略数据权限检查。
            DataPermissionUtils.executeIgnore(() -> {
                try {
                    // 继续执行过滤器链。
                    chain.doFilter(request, response);
                } catch (IOException | ServletException e) {
                    // 如果在执行过滤器链时发生异常，抛出运行时异常。
                    throw new RuntimeException(e);
                }
            });
        } else {
            // 如果请求头中没有指定数据权限为禁用，或者指定为启用，直接执行过滤器链。
            chain.doFilter(request, response);
        }
    }

}