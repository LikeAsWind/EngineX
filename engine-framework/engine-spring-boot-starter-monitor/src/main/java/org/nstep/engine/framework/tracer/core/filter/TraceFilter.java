package org.nstep.engine.framework.tracer.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nstep.engine.framework.common.util.monitor.TracerUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Trace 过滤器，打印 traceId 到 header 中返回
 * 该过滤器会在每个请求的响应头中添加一个 traceId，用于链路追踪。
 */
public class TraceFilter extends OncePerRequestFilter {

    /**
     * Header 名 - 链路追踪编号
     * 用于在响应头中设置 traceId 的字段名
     */
    private static final String HEADER_NAME_TRACE_ID = "trace-id";

    /**
     * 在每次请求时执行的过滤操作
     *
     * @param request  当前请求
     * @param response 当前响应
     * @param chain    过滤器链
     * @throws IOException      可能会抛出的 IO 异常
     * @throws ServletException 可能会抛出的 Servlet 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 从 TracerUtils 获取当前的 traceId，并将其添加到响应头中
        response.addHeader(HEADER_NAME_TRACE_ID, TracerUtils.getTraceId());

        // 继续执行过滤器链中的下一个过滤器或目标资源
        chain.doFilter(request, response);
    }

}
