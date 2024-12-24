package org.nstep.engine.framework.tenant.core.security;

import cn.hutool.core.collection.CollUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.nstep.engine.framework.security.core.LoginUser;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.framework.tenant.config.TenantProperties;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;
import org.nstep.engine.framework.tenant.core.service.TenantFrameworkService;
import org.nstep.engine.framework.web.config.WebProperties;
import org.nstep.engine.framework.web.core.filter.ApiRequestFilter;
import org.nstep.engine.framework.web.core.handler.GlobalExceptionHandler;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Objects;

/**
 * 多租户 Security Web 过滤器
 * 1. 如果是登陆的用户，校验是否有权限访问该租户，避免越权问题。
 * 2. 如果请求未带租户的编号，检查是否是忽略的 URL，否则也不允许访问。
 * 3. 校验租户是合法，例如说被禁用、到期
 */
@Slf4j
public class TenantSecurityWebFilter extends ApiRequestFilter {

    private final TenantProperties tenantProperties; // 存储租户相关配置
    private final AntPathMatcher pathMatcher; // 用于匹配 URL 路径
    private final GlobalExceptionHandler globalExceptionHandler; // 处理全局异常
    private final TenantFrameworkService tenantFrameworkService; // 租户框架服务，用于校验租户合法性

    // 构造函数，初始化相关依赖
    public TenantSecurityWebFilter(TenantProperties tenantProperties,
                                   WebProperties webProperties,
                                   GlobalExceptionHandler globalExceptionHandler,
                                   TenantFrameworkService tenantFrameworkService) {
        super(webProperties);
        this.tenantProperties = tenantProperties;
        this.pathMatcher = new AntPathMatcher();
        this.globalExceptionHandler = globalExceptionHandler;
        this.tenantFrameworkService = tenantFrameworkService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
            throws ServletException, IOException {
        Long tenantId = TenantContextHolder.getTenantId(); // 获取当前请求的租户 ID

        // 1. 登陆的用户，校验是否有权限访问该租户，避免越权问题。
        LoginUser user = SecurityFrameworkUtils.getLoginUser(); // 获取当前登录用户
        if (user != null) {
            // 如果请求中没有传递租户 ID，则尝试使用登录用户的租户 ID
            if (tenantId == null) {
                tenantId = user.getTenantId();
                TenantContextHolder.setTenantId(tenantId);
            } else if (!Objects.equals(user.getTenantId(), TenantContextHolder.getTenantId())) {
                // 如果请求中的租户 ID 与登录用户的租户 ID 不一致，说明存在越权访问
                log.error("[doFilterInternal][租户({}) User({}/{}) 越权访问租户({}) URL({}/{})]",
                        user.getTenantId(), user.getId(), user.getUserType(),
                        TenantContextHolder.getTenantId(), request.getRequestURI(), request.getMethod());
                ServletUtils.writeJSON(response, CommonResult.error(GlobalErrorCodeConstants.FORBIDDEN.getCode(),
                        "您无权访问该租户的数据"));
                return;
            }
        }

        // 如果请求的 URL 不在忽略列表中，则校验租户是否合法
        if (!isIgnoreUrl(request)) {
            // 2. 如果请求未带租户的编号，不允许访问。
            if (tenantId == null) {
                log.error("[doFilterInternal][URL({}/{}) 未传递租户编号]", request.getRequestURI(), request.getMethod());
                ServletUtils.writeJSON(response, CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(),
                        "请求的租户标识未传递，请进行排查"));
                return;
            }

            // 3. 校验租户是否合法，例如说被禁用、到期
            try {
                tenantFrameworkService.validTenant(tenantId); // 校验租户的有效性
            } catch (Throwable ex) {
                // 如果校验失败，返回异常信息
                CommonResult<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
                ServletUtils.writeJSON(response, result);
                return;
            }
        } else { // 如果是允许忽略租户的 URL，若未传递租户编号，则默认忽略租户编号，避免报错
            if (tenantId == null) {
                TenantContextHolder.setIgnore(true);
            }
        }

        // 继续执行过滤链
        chain.doFilter(request, response);
    }

    // 判断请求 URL 是否在忽略列表中
    private boolean isIgnoreUrl(HttpServletRequest request) {
        // 快速匹配，保证性能
        if (CollUtil.contains(tenantProperties.getIgnoreUrls(), request.getRequestURI())) {
            return true;
        }
        // 逐个 Ant 路径匹配
        for (String url : tenantProperties.getIgnoreUrls()) {
            if (pathMatcher.match(url, request.getRequestURI())) {
                return true;
            }
        }
        return false;
    }
}
