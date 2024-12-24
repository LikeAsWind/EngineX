package org.nstep.engine.framework.apilog.core.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.nstep.engine.framework.common.util.spring.SpringUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * API 访问日志拦截器
 * <p>
 * 目的：在非生产环境时，打印请求（request）和响应（response）日志到控制台（或日志文件）。
 * 主要用于调试和分析 API 请求的处理过程。
 */
@Slf4j
public class ApiAccessLogInterceptor implements HandlerInterceptor {

    // 存储 HandlerMethod 的常量，用于在后续处理中获取具体的处理方法
    public static final String ATTRIBUTE_HANDLER_METHOD = "HANDLER_METHOD";

    // 存储 StopWatch 的常量，用于记录请求处理的时间
    private static final String ATTRIBUTE_STOP_WATCH = "ApiAccessLogInterceptor.StopWatch";

    /**
     * 在请求处理前执行，记录请求的相关信息。
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler  当前请求的处理器
     * @return true 继续执行后续的处理，false 则中断请求处理
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // 获取当前处理方法（HandlerMethod），并设置为请求的属性，供后续使用
        HandlerMethod handlerMethod = handler instanceof HandlerMethod ? (HandlerMethod) handler : null;
        if (handlerMethod != null) {
            request.setAttribute(ATTRIBUTE_HANDLER_METHOD, handlerMethod);
        }

        // 在非生产环境下，打印请求日志
        if (!SpringUtils.isNonProductionEnvironment()) {
            // 获取请求参数（查询字符串）和请求体（如果是 JSON 格式）
            Map<String, String> queryString = ServletUtils.getParamMap(request);
            String requestBody = ServletUtils.isJsonRequest(request) ? ServletUtils.getBody(request) : null;

            // 打印请求的 URL 和参数（如果有的话）
            if (CollUtil.isEmpty(queryString) && StrUtil.isEmpty(requestBody)) {
                log.info("[preHandle][开始请求 URL({}) 无参数]", request.getRequestURI());
            } else {
                log.info("[preHandle][开始请求 URL({}) 参数({})]", request.getRequestURI(),
                        StrUtil.blankToDefault(requestBody, queryString.toString()));
            }

            // 开始计时，记录请求处理的耗时
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            request.setAttribute(ATTRIBUTE_STOP_WATCH, stopWatch);

            // 打印当前 Controller 方法的路径和行号
            printHandlerMethodPosition(handlerMethod);
        }

        // 返回 true，表示继续处理请求
        return true;
    }

    /**
     * 请求处理完成后执行，记录响应的相关信息。
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler  当前请求的处理器
     * @param ex       异常信息（如果有的话）
     */
    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        // 在非生产环境下，打印响应日志
        if (!SpringUtils.isNonProductionEnvironment()) {
            // 获取请求处理的耗时
            StopWatch stopWatch = (StopWatch) request.getAttribute(ATTRIBUTE_STOP_WATCH);
            stopWatch.stop();

            // 打印请求的 URL 和耗时
            log.info("[afterCompletion][完成请求 URL({}) 耗时({} ms)]",
                    request.getRequestURI(), stopWatch.getTotalTimeMillis());
        }
    }

    /**
     * 打印当前 Controller 方法的路径和行号
     * 通过读取源代码文件，查找方法所在的行号。
     *
     * @param handlerMethod 当前请求的 HandlerMethod
     */
    private void printHandlerMethodPosition(HandlerMethod handlerMethod) {
        if (handlerMethod == null) {
            return;
        }

        // 获取方法和类的信息
        Method method = handlerMethod.getMethod();
        Class<?> clazz = method.getDeclaringClass();

        try {
            // 获取源代码文件的路径，并读取该文件的内容
            List<String> clazzContents = FileUtil.readUtf8Lines(
                    ResourceUtil.getResource(null, clazz).getPath().replace("/target/classes/", "/src/main/java/")
                            + clazz.getSimpleName() + ".java");

            // 查找方法所在的行号
            Optional<Integer> lineNumber = IntStream.range(0, clazzContents.size())
                    .filter(i -> clazzContents.get(i).contains(" " + method.getName() + "(")) // 简单匹配方法名
                    .mapToObj(i -> i + 1) // 行号从 1 开始
                    .findFirst();

            // 如果找到了行号，打印方法路径
            lineNumber.ifPresent(integer -> System.out.printf("\tController 方法路径：%s(%s.java:%d)\n", clazz.getName(),
                    clazz.getSimpleName(), integer));
        } catch (Exception ignore) {
            // 如果读取源代码文件失败或发生异常，忽略该异常，因为这只是日志打印
        }
    }

}
