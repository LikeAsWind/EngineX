package org.nstep.engine.framework.security.core.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.exception.ServiceException;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.nstep.engine.framework.security.config.SecurityProperties;
import org.nstep.engine.framework.security.core.LoginUser;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.framework.web.core.handler.GlobalExceptionHandler;
import org.nstep.engine.framework.web.core.util.WebFrameworkUtils;
import org.nstep.engine.module.system.api.oauth2.OAuth2TokenApi;
import org.nstep.engine.module.system.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Token 过滤器，验证 token 的有效性
 * 验证通过后，获得 {@link LoginUser} 信息，并加入到 Spring Security 上下文
 */
@RequiredArgsConstructor
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;

    private final GlobalExceptionHandler globalExceptionHandler;

    private final OAuth2TokenApi oauth2TokenApi;

    /**
     * 过滤请求并验证 Token 的有效性
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param chain    过滤器链
     * @throws ServletException 处理过程中发生的异常
     * @throws IOException      I/O 异常
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 情况一，基于 header[login-user] 获得用户信息，例如来自 Gateway 或其它服务透传
        LoginUser loginUser = buildLoginUserByHeader(request);

        // 情况二，基于 Token 获取用户信息
        // 主要用于 Nginx 直接转发到 Spring Cloud 服务的场景
        if (loginUser == null) {
            // 获取请求中的 Token（从 Header 或 参数中）
            String token = SecurityFrameworkUtils.obtainAuthorization(request, securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
            if (StrUtil.isNotEmpty(token)) {
                Integer userType = WebFrameworkUtils.getLoginUserType(request);
                try {
                    // 1.1 基于 Token 构建登录用户
                    loginUser = buildLoginUserByToken(token, userType);
                    // 1.2 如果 Token 校验失败，尝试模拟登录用户（仅用于开发调试）
                    if (loginUser == null) {
                        loginUser = mockLoginUser(request, token, userType);
                    }
                } catch (Throwable ex) {
                    // 捕获异常并返回错误信息
                    CommonResult<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
                    ServletUtils.writeJSON(response, result);
                    return;
                }
            }
        }

        // 设置当前用户到 Spring Security 上下文中
        if (loginUser != null) {
            SecurityFrameworkUtils.setLoginUser(loginUser, request);
        }
        // 继续过滤链
        chain.doFilter(request, response);
    }

    /**
     * 基于 Token 获取登录用户信息
     *
     * @param token    访问令牌
     * @param userType 用户类型
     * @return 登录用户信息
     */
    private LoginUser buildLoginUserByToken(String token, Integer userType) {
        try {
            // 校验访问令牌的有效性
            OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.checkAccessToken(token).getCheckedData();
            if (accessToken == null) {
                return null;
            }
            // 用户类型不匹配时抛出异常
            if (userType != null && ObjectUtil.notEqual(accessToken.getUserType(), userType)) {
                throw new AccessDeniedException("错误的用户类型");
            }
            // 构建登录用户信息
            LoginUser loginUser = new LoginUser();
            loginUser.setId(accessToken.getUserId());
            loginUser.setUserType(accessToken.getUserType());
            loginUser.setInfo(accessToken.getUserInfo()); // 额外的用户信息
            loginUser.setTenantId(accessToken.getTenantId());
            loginUser.setScopes(accessToken.getScopes());
            loginUser.setExpiresTime(accessToken.getExpiresTime());
            return loginUser;
        } catch (ServiceException serviceException) {
            // 校验 Token 不通过时，部分接口允许不登录，直接返回 null
            return null;
        }
    }

    /**
     * 模拟登录用户，方便日常开发调试
     * 注意：线上环境下一定要关闭该功能
     *
     * @param request  请求对象
     * @param token    模拟的 token，格式为 {@link SecurityProperties#getMockSecret()} + 用户编号
     * @param userType 用户类型
     * @return 模拟的 LoginUser 对象
     */
    private LoginUser mockLoginUser(HttpServletRequest request, String token, Integer userType) {
        if (!securityProperties.getMockEnable()) {
            return null;
        }
        // 必须以 mockSecret 开头
        if (!token.startsWith(securityProperties.getMockSecret())) {
            return null;
        }
        // 构建模拟用户
        Long userId = Long.valueOf(token.substring(securityProperties.getMockSecret().length()));
        LoginUser loginUser = new LoginUser();
        loginUser.setId(userId);
        loginUser.setUserType(userType);
        loginUser.setTenantId(WebFrameworkUtils.getTenantId(request));
        return loginUser;
    }

    /**
     * 基于 Header 获取登录用户信息
     *
     * @param request 请求对象
     * @return 登录用户信息
     */
    private LoginUser buildLoginUserByHeader(HttpServletRequest request) {
        // 从 Header 中获取登录用户信息
        String loginUserStr = request.getHeader(SecurityFrameworkUtils.LOGIN_USER_HEADER);
        if (StrUtil.isEmpty(loginUserStr)) {
            return null;
        }
        try {
            // 解码登录用户信息，解决中文乱码问题
            loginUserStr = URLDecoder.decode(loginUserStr, StandardCharsets.UTF_8);
            // 解析为 LoginUser 对象
            return JsonUtils.parseObject(loginUserStr, LoginUser.class);
        } catch (Exception ex) {
            log.error("[buildLoginUserByHeader][解析 LoginUser({}) 发生异常]", loginUserStr, ex);
            throw ex; // 抛出异常
        }
    }

}
