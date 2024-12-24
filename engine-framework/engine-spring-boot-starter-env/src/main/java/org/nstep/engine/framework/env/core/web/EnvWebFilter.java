package org.nstep.engine.framework.env.core.web;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nstep.engine.framework.env.core.context.EnvContextHolder;
import org.nstep.engine.framework.env.core.util.EnvUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 环境的 {@link jakarta.servlet.Filter} 实现类
 * 当有 tag 请求头时，设置到 {@link EnvContextHolder} 的标签上下文
 * 该过滤器用于从 HTTP 请求中提取 tag，并将其设置到当前线程的上下文中，以便后续的服务调用能够使用该标签进行负载均衡等操作。
 */
public class EnvWebFilter extends OncePerRequestFilter {

    /**
     * 处理每个 HTTP 请求。
     * 该方法会检查请求头中是否包含 tag，如果包含，则将其设置到 {@link EnvContextHolder} 中。
     * 如果没有 tag，则继续执行过滤链的下一个过滤器或目标资源。
     *
     * @param request  当前的 HTTP 请求
     * @param response 当前的 HTTP 响应
     * @param chain    过滤链，用于继续执行后续的过滤器或目标资源
     * @throws ServletException 如果处理请求时发生错误
     * @throws IOException      如果处理请求时发生 I/O 错误
     */
    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable FilterChain chain)
            throws ServletException, IOException {
        // 获取请求头中的 tag 值
        assert request != null;
        String tag = EnvUtils.getTag(request);

        // 如果请求头中没有 tag，则直接执行过滤链的后续操作
        if (StrUtil.isEmpty(tag)) {
            assert chain != null;
            chain.doFilter(request, response);
            return;
        }

        // 如果请求头中有 tag，则将其设置到 EnvContextHolder 中
        EnvContextHolder.setTag(tag);

        try {
            // 执行过滤链的后续操作
            assert chain != null;
            chain.doFilter(request, response);
        } finally {
            // 清理上下文中的 tag，确保不会影响后续的请求
            EnvContextHolder.removeTag();
        }
    }
}
