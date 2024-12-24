package org.nstep.engine.framework.web.core.filter;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.nstep.engine.framework.web.config.WebProperties;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 过滤 /admin-api、/app-api 等 API 请求的过滤器。
 * <p>
 * 该过滤器继承自 OncePerRequestFilter，用于在每个请求处理前进行过滤，确保只对 API 请求进行过滤。
 * 它会根据配置的 API 前缀（如 `/admin-api` 和 `/app-api`）判断是否需要对请求进行处理。
 * 如果请求的 URI 以这些前缀开头，过滤器将会被触发，否则请求将直接通过。
 */
@RequiredArgsConstructor
public abstract class ApiRequestFilter extends OncePerRequestFilter {

    /**
     * Web 配置属性，包含 API 前缀等信息。
     */
    protected final WebProperties webProperties;

    /**
     * 判断是否不需要过滤该请求。
     * <p>
     * 只对以指定 API 前缀（如 `/admin-api` 和 `/app-api`）开头的请求进行过滤。
     *
     * @param request 当前的 HTTP 请求
     * @return 如果请求的 URI 不以指定的 API 前缀开头，则返回 true，表示不需要过滤；否则返回 false，表示需要过滤
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 只过滤 API 请求的地址
        return !StrUtil.startWithAny(request.getRequestURI(), webProperties.getAdminApi().getPrefix(),
                webProperties.getAppApi().getPrefix());
    }

}
