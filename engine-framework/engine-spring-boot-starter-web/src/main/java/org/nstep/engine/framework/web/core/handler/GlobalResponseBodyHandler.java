package org.nstep.engine.framework.web.core.handler;

import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.web.core.util.WebFrameworkUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应结果（ResponseBody）处理器
 * <p>
 * 该处理器用于拦截并处理 Controller 返回的响应体，确保响应体符合统一的格式。
 * <p>
 * 不同于在网上看到的很多文章，会选择自动将 Controller 返回结果包上 {@link CommonResult}，
 * 在 onemall 中，是 Controller 在返回时，主动自己包上 {@link CommonResult}。
 * 原因是，GlobalResponseBodyHandler 本质上是 AOP，它不应该改变 Controller 返回的数据结构。
 * <p>
 * 目前，GlobalResponseBodyHandler 的主要作用是，记录 Controller 的返回结果，方便 {@link org.nstep.engine.framework.apilog.core.filter.ApiAccessLogFilter} 记录访问日志。
 * 它通过拦截返回结果，确保日志中能够记录到实际的响应数据。
 */
@ControllerAdvice
public class GlobalResponseBodyHandler implements ResponseBodyAdvice {

    /**
     * 判断是否支持拦截当前返回类型。
     * <p>
     * 该方法用于确定是否需要拦截当前 Controller 方法的返回结果。
     * 只有当返回类型为 {@link CommonResult} 时，才会进行处理。
     *
     * @param returnType    当前方法的返回类型
     * @param converterType 转换器类型
     * @return 如果返回类型是 {@link CommonResult}，则返回 true，表示需要拦截；否则返回 false。
     */
    @Override
    @SuppressWarnings("NullableProblems") // 避免 IDEA 警告
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 如果方法没有返回类型，直接返回 false
        if (returnType.getMethod() == null) {
            return false;
        }
        // 只拦截返回类型为 CommonResult 的方法
        return returnType.getMethod().getReturnType() == CommonResult.class;
    }

    /**
     * 在响应体写入之前进行处理。
     * <p>
     * 该方法会在 Controller 返回结果后执行，允许我们对响应体进行修改或记录等操作。
     * 在这里，我们主要的操作是记录 Controller 返回的 {@link CommonResult} 对象，以便访问日志记录。
     *
     * @param body                  Controller 返回的响应体
     * @param returnType            当前方法的返回类型
     * @param selectedContentType   选中的内容类型（如 JSON）
     * @param selectedConverterType 选中的转换器类型
     * @param request               当前请求
     * @param response              当前响应
     * @return 返回原始的响应体（没有修改）
     */
    @Override
    @SuppressWarnings("NullableProblems") // 避免 IDEA 警告
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 记录 Controller 的返回结果到日志中
        WebFrameworkUtils.setCommonResult(((ServletServerHttpRequest) request).getServletRequest(), (CommonResult<?>) body);
        // 返回原始的响应体，不做修改
        return body;
    }

}
