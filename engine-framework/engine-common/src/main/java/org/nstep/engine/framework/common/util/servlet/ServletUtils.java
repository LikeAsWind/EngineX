package org.nstep.engine.framework.common.util.servlet;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * 客户端工具类
 * <p>
 * 提供了一些与 Servlet 请求和响应相关的工具方法，主要用于处理 JSON 响应、获取请求头、客户端 IP、请求体等。
 */
public class ServletUtils {

    /**
     * 返回 JSON 字符串
     * <p>
     * 将对象序列化为 JSON 字符串并写入响应中，响应的内容类型为 `application/json;charset=UTF-8`。
     *
     * @param response 响应对象
     * @param object   要序列化的对象
     */
    @SuppressWarnings("deprecation") // 必须使用 APPLICATION_JSON_UTF8_VALUE，否则会乱码
    public static void writeJSON(HttpServletResponse response, Object object) {
        // 将对象转换为 JSON 字符串
        String content = JsonUtils.toJsonString(object);
        // 写入响应并设置内容类型为 JSON
        JakartaServletUtil.write(response, content, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    /**
     * 获取请求头中的 User-Agent
     * <p>
     * 返回请求头中的 User-Agent 字段，表示客户端的浏览器信息。
     *
     * @param request 请求对象
     * @return User-Agent 字符串
     */
    public static String getUserAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua != null ? ua : "";
    }

    /**
     * 获取当前请求的 HttpServletRequest 对象
     * <p>
     * 通过 `RequestContextHolder` 获取当前线程绑定的请求对象。
     *
     * @return 当前请求对象
     */
    public static HttpServletRequest getRequest() {
        // 获取当前请求的 RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 判断请求是否为 Servlet 请求
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }
        // 返回当前的 HttpServletRequest
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取当前请求的 User-Agent
     * <p>
     * 获取当前请求的 User-Agent 字符串，若请求为 null，则返回 null。
     *
     * @return User-Agent 字符串
     */
    public static String getUserAgent() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return getUserAgent(request);
    }

    /**
     * 获取客户端 IP 地址
     * <p>
     * 获取当前请求的客户端 IP 地址。
     *
     * @return 客户端 IP 地址
     */
    public static String getClientIP() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return JakartaServletUtil.getClientIP(request);
    }

    /**
     * 判断请求是否为 JSON 格式
     * <p>
     * 判断请求的 `Content-Type` 是否为 `application/json`。
     *
     * @param request 请求对象
     * @return 如果是 JSON 请求，返回 true；否则返回 false
     */
    public static boolean isJsonRequest(ServletRequest request) {
        return StrUtil.startWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 获取请求体的字符串内容
     * <p>
     * 如果请求是 JSON 格式，则返回请求体的字符串内容。
     *
     * @param request 请求对象
     * @return 请求体的字符串内容
     */
    public static String getBody(HttpServletRequest request) {
        // 只有在 JSON 请求时才读取请求体
        if (isJsonRequest(request)) {
            return JakartaServletUtil.getBody(request);
        }
        return null;
    }

    /**
     * 获取请求体的字节内容
     * <p>
     * 如果请求是 JSON 格式，则返回请求体的字节内容。
     *
     * @param request 请求对象
     * @return 请求体的字节内容
     */
    public static byte[] getBodyBytes(HttpServletRequest request) {
        // 只有在 JSON 请求时才读取请求体
        if (isJsonRequest(request)) {
            return JakartaServletUtil.getBodyBytes(request);
        }
        return null;
    }

    /**
     * 获取客户端 IP 地址
     * <p>
     * 获取请求中的客户端 IP 地址。
     *
     * @param request 请求对象
     * @return 客户端 IP 地址
     */
    public static String getClientIP(HttpServletRequest request) {
        return JakartaServletUtil.getClientIP(request);
    }

    /**
     * 获取请求的参数映射
     * <p>
     * 获取请求中的所有参数，并将其作为键值对返回。
     *
     * @param request 请求对象
     * @return 请求参数的映射
     */
    public static Map<String, String> getParamMap(HttpServletRequest request) {
        return JakartaServletUtil.getParamMap(request);
    }

}
