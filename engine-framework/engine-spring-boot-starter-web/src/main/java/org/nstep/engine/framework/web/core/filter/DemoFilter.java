package org.nstep.engine.framework.web.core.filter;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.nstep.engine.framework.web.core.util.WebFrameworkUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import static org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants.DEMO_DENY;

/**
 * 演示 Filter，用于禁止用户发起写操作（如 POST、PUT、DELETE），避免影响测试数据。
 * <p>
 * 该过滤器主要用于演示模式下，防止用户在测试环境中进行数据修改操作。它会拦截所有写操作请求（POST、PUT、DELETE），
 * 如果请求是写操作且用户未登录，则直接返回错误信息，阻止请求继续执行。
 */
public class DemoFilter extends OncePerRequestFilter {

    /**
     * 判断是否需要过滤当前请求。
     * <p>
     * 该方法判断请求的 HTTP 方法是否为写操作（POST、PUT、DELETE），并且用户是否已登录。
     * 如果是写操作且用户未登录，则该请求将被过滤。
     *
     * @param request 当前的 HTTP 请求
     * @return 如果需要过滤请求，返回 true；否则返回 false
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();
        return !StrUtil.equalsAnyIgnoreCase(method, "POST", "PUT", "DELETE")  // 写操作时，不进行过滤
                || WebFrameworkUtils.getLoginUserId(request) == null; // 非登录用户时，不进行过滤
    }

    /**
     * 执行过滤操作，直接返回错误响应，禁止写操作请求继续执行。
     * <p>
     * 如果请求是写操作并且用户未登录，该方法将直接返回一个错误响应，阻止请求继续执行。
     *
     * @param request  当前的 HTTP 请求
     * @param response 当前的 HTTP 响应
     * @param chain    过滤器链
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain) {
        // 直接返回 DEMO_DENY 的结果，即请求不继续
        ServletUtils.writeJSON(response, CommonResult.error(DEMO_DENY));
    }

}
