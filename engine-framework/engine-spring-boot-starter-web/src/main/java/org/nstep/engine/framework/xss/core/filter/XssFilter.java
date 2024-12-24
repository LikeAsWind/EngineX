package org.nstep.engine.framework.xss.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.xss.config.XssProperties;
import org.nstep.engine.framework.xss.core.clean.XssCleaner;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * XSS 过滤器，用于拦截 HTTP 请求并对请求中的 HTML 内容进行 XSS 清理。
 * <p>
 * 该过滤器继承自 Spring 的 `OncePerRequestFilter`，确保每个请求只执行一次过滤操作。
 * 它会检查请求中的 HTML 内容，利用 `XssCleaner` 清理潜在的 XSS 风险，确保请求数据的安全性。
 */
@AllArgsConstructor
public class XssFilter extends OncePerRequestFilter {

    /**
     * XSS 配置属性，包含是否启用过滤、排除的 URL 等设置。
     */
    private final XssProperties properties;

    /**
     * 路径匹配器，用于匹配请求的 URL 是否符合排除过滤的条件。
     */
    private final PathMatcher pathMatcher;

    /**
     * XSS 清理器，用于清理请求中的 HTML 内容，防止 XSS 攻击。
     */
    private final XssCleaner xssCleaner;

    /**
     * 执行过滤操作，检查请求中的 HTML 内容并进行 XSS 清理。
     * <p>
     * 该方法通过 `XssRequestWrapper` 将原始请求包装起来，在请求中包含 XSS 清理后的数据。
     *
     * @param request     当前 HTTP 请求
     * @param response    当前 HTTP 响应
     * @param filterChain 过滤器链，用于继续执行后续的过滤器或请求处理
     * @throws IOException      如果发生 I/O 错误
     * @throws ServletException 如果发生 Servlet 异常
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        // 使用 XssRequestWrapper 包装请求，进行 XSS 清理
        filterChain.doFilter(new XssRequestWrapper(request, xssCleaner), response);
    }

    /**
     * 判断当前请求是否需要过滤。
     * <p>
     * 如果 XSS 过滤未启用，或请求的 URL 匹配到排除的路径，则跳过过滤。
     *
     * @param request 当前 HTTP 请求
     * @return 如果需要过滤返回 false，若不需要过滤则返回 true
     */
    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        // 如果 XSS 过滤未启用，直接跳过
        if (!properties.isEnable()) {
            return true;
        }

        // 如果请求的 URL 匹配到排除的路径，跳过过滤
        String uri = request.getRequestURI();
        return properties.getExcludeUrls().stream().anyMatch(excludeUrl -> pathMatcher.match(excludeUrl, uri));
    }
}
