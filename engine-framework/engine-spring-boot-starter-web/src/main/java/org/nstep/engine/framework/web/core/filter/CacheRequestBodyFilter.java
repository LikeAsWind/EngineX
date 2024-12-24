package org.nstep.engine.framework.web.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Request Body 缓存 Filter，实现请求体的可重复读取。
 * <p>
 * 该过滤器继承自 OncePerRequestFilter，用于缓存请求体内容，使得请求体可以在多个地方重复读取。
 * 在 Spring MVC 中，RequestBody 只能读取一次，读取后会被消耗掉，无法再次访问。该过滤器通过包装请求对象，
 * 将请求体缓存到内存中，允许多次读取请求体。
 */
public class CacheRequestBodyFilter extends OncePerRequestFilter {

    /**
     * 过滤器的核心方法，处理请求并包装请求体。
     * <p>
     * 在此方法中，过滤器通过 `CacheRequestBodyWrapper` 包装请求对象，确保请求体内容可以被多次读取。
     * 然后，过滤器将请求交给下一个过滤器链继续处理。
     *
     * @param request     当前的 HTTP 请求
     * @param response    当前的 HTTP 响应
     * @param filterChain 过滤器链
     * @throws IOException      如果处理请求时发生 I/O 错误
     * @throws ServletException 如果处理请求时发生 Servlet 异常
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        filterChain.doFilter(new CacheRequestBodyWrapper(request), response);
    }

    /**
     * 判断是否需要过滤该请求。
     * <p>
     * 该方法判断请求是否为 JSON 请求，仅处理 JSON 格式的请求体。如果请求的内容类型不是 JSON，则不需要过滤。
     *
     * @param request 当前的 HTTP 请求
     * @return 如果请求是 JSON 请求，则返回 false，表示需要过滤；否则返回 true，表示不需要过滤
     */
    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        // 只处理 json 请求内容
        return !ServletUtils.isJsonRequest(request);
    }

}
