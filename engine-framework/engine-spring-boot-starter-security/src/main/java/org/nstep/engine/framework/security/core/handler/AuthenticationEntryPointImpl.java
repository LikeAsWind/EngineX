package org.nstep.engine.framework.security.core.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import static org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;

/**
 * 访问一个需要认证的 URL 资源，但是此时自己尚未认证（登录）的情况下，返回 {@link GlobalErrorCodeConstants#UNAUTHORIZED} 错误码，从而使前端重定向到登录页
 * <p>
 * 补充：Spring Security 通过 {@link ExceptionTranslationFilter#sendStartAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, AuthenticationException)} 方法，调用当前类
 */
@Slf4j
@SuppressWarnings("JavadocReference") // 忽略文档引用报错
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    /**
     * 处理未认证（未登录）访问的情况，当用户访问需要认证的 URL 资源，但未登录时，调用此方法。
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param e        认证异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        // 打印调试日志，记录访问的 URL 和异常信息
        log.debug("[commence][访问 URL({}) 时，没有登录]", request.getRequestURI(), e);

        // 返回 401 错误，表示未认证，前端可以根据此错误码跳转到登录页
        ServletUtils.writeJSON(response, CommonResult.error(UNAUTHORIZED));
    }

}
