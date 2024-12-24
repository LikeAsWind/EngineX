package org.nstep.engine.framework.web.core.util;

import cn.hutool.core.util.NumberUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.nstep.engine.framework.common.enums.RpcConstants;
import org.nstep.engine.framework.common.enums.TerminalEnum;
import org.nstep.engine.framework.common.enums.UserTypeEnum;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.web.config.WebProperties;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 专属于 web 包的工具类
 * <p>
 * 该类包含了一些与 Web 框架相关的常用工具方法，主要用于从请求中获取相关信息、设置请求属性以及判断请求类型等操作。
 * 这些方法帮助简化了 Web 框架中的常见操作，如获取租户编号、用户信息等。
 */
public class WebFrameworkUtils {

    // 常量定义
    public static final String HEADER_TENANT_ID = "tenant-id"; // 租户 ID 的请求头
    public static final String HEADER_TERMINAL = "terminal";   // 终端的请求头

    // 请求属性常量
    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_ID = "login_user_id"; // 用户 ID 请求属性
    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_TYPE = "login_user_type"; // 用户类型请求属性
    private static final String REQUEST_ATTRIBUTE_COMMON_RESULT = "common_result"; // CommonResult 请求属性

    // Web 配置属性
    private static WebProperties properties;

    /**
     * 构造方法，初始化 WebProperties 配置
     *
     * @param webProperties Web 配置属性
     */
    public WebFrameworkUtils(WebProperties webProperties) {
        WebFrameworkUtils.properties = webProperties;
    }

    /**
     * 从请求中获取租户编号
     * <p>
     * 该方法从 HTTP 请求头中获取租户 ID。租户 ID 是多租户系统中的一个重要信息。
     *
     * @param request 请求对象
     * @return 租户编号，如果未找到或无效则返回 null
     */
    public static Long getTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader(HEADER_TENANT_ID);
        return NumberUtil.isNumber(tenantId) ? Long.valueOf(tenantId) : null;
    }

    /**
     * 设置当前登录用户的 ID
     *
     * @param request 请求对象
     * @param userId  用户 ID
     */
    public static void setLoginUserId(ServletRequest request, Long userId) {
        request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID, userId);
    }

    /**
     * 设置当前登录用户的类型
     *
     * @param request  请求对象
     * @param userType 用户类型
     */
    public static void setLoginUserType(ServletRequest request, Integer userType) {
        request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_TYPE, userType);
    }

    /**
     * 获取当前登录用户的 ID
     * <p>
     * 该方法从请求的属性中获取当前登录用户的 ID。仅限于 framework 框架使用。
     *
     * @param request 请求对象
     * @return 用户 ID，如果未找到则返回 null
     */
    public static Long getLoginUserId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return (Long) request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID);
    }

    /**
     * 获取当前登录用户的类型
     * <p>
     * 该方法从请求的属性中获取当前登录用户的类型。根据请求的路径前缀来判断用户类型。
     *
     * @param request 请求对象
     * @return 用户类型，如果未找到则返回 null
     */
    public static Integer getLoginUserType(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        // 优先从请求属性中获取用户类型
        Integer userType = (Integer) request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_TYPE);
        if (userType != null) {
            return userType;
        }
        // 其次，根据 URL 前缀的约定判断用户类型
        if (request.getServletPath().startsWith(properties.getAdminApi().getPrefix())) {
            return UserTypeEnum.ADMIN.getValue(); // 管理员用户
        }
        if (request.getServletPath().startsWith(properties.getAppApi().getPrefix())) {
            return UserTypeEnum.MEMBER.getValue(); // 普通用户
        }
        return null;
    }

    /**
     * 获取当前登录用户的类型（从当前请求中）
     *
     * @return 用户类型
     */
    public static Integer getLoginUserType() {
        HttpServletRequest request = getRequest();
        return getLoginUserType(request);
    }

    /**
     * 获取当前登录用户的 ID（从当前请求中）
     *
     * @return 用户 ID
     */
    public static Long getLoginUserId() {
        HttpServletRequest request = getRequest();
        return getLoginUserId(request);
    }

    /**
     * 获取当前请求的终端类型
     *
     * @return 终端类型，默认为 UNKNOWN
     */
    public static Integer getTerminal() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return TerminalEnum.UNKNOWN.getTerminal();
        }
        String terminalValue = request.getHeader(HEADER_TERMINAL);
        return NumberUtil.parseInt(terminalValue, TerminalEnum.UNKNOWN.getTerminal());
    }

    /**
     * 设置 CommonResult 到请求属性中
     * <p>
     * 该方法将 Controller 返回的 {@link CommonResult} 对象保存到请求属性中，供后续使用。
     *
     * @param request 请求对象
     * @param result  CommonResult 对象
     */
    public static void setCommonResult(ServletRequest request, CommonResult<?> result) {
        request.setAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT, result);
    }

    /**
     * 获取请求中的 CommonResult 对象
     *
     * @param request 请求对象
     * @return CommonResult 对象，如果没有设置则返回 null
     */
    public static CommonResult<?> getCommonResult(ServletRequest request) {
        return (CommonResult<?>) request.getAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT);
    }

    /**
     * 获取当前请求对象
     *
     * @return 当前请求对象，如果没有请求上下文则返回 null
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getRequest();
    }

    /**
     * 判断是否为 RPC 请求
     *
     * @param request 请求对象
     * @return 如果是 RPC 请求，返回 true；否则返回 false
     */
    public static boolean isRpcRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith(RpcConstants.RPC_API_PREFIX);
    }

    /**
     * 判断类名是否表示 RPC 请求
     * <p>
     * 通过判断类名是否以 "Api" 结尾来判断是否为 RPC 请求。
     *
     * @param className 类名
     * @return 如果类名以 "Api" 结尾，返回 true；否则返回 false
     */
    public static boolean isRpcRequest(String className) {
        return className.endsWith("Api");
    }
}
