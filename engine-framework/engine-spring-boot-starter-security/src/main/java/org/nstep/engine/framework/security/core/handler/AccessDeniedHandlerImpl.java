package org.nstep.engine.framework.security.core.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import java.io.IOException;

import static org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants.FORBIDDEN;

/**
 * 访问一个需要认证的 URL 资源，已经认证（登录）但是没有权限的情况下，返回 {@link GlobalErrorCodeConstants#FORBIDDEN} 错误码。
 * <p>
 * 补充：Spring Security 通过 {@link ExceptionTranslationFilter#handleAccessDeniedException(HttpServletRequest, HttpServletResponse, FilterChain, AccessDeniedException)} 方法，调用当前类
 */
@Slf4j
@SuppressWarnings("JavadocReference")
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    /**
     * 处理权限不足的情况，当用户已经认证（登录），但没有足够权限访问某个资源时，调用此方法。
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param e        访问被拒绝的异常
     * @throws IOException      IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
            throws IOException, ServletException {
        // 打印警告日志，记录访问 URL 和用户信息，以便定期检查是否有恶意破坏行为
        log.warn("[commence][访问 URL({}) 时，用户({}) 权限不够]", request.getRequestURI(),
                SecurityFrameworkUtils.getLoginUserId(), e);

        // 返回 403 错误，表示权限不足
        ServletUtils.writeJSON(response, CommonResult.error(FORBIDDEN));
    }

}
