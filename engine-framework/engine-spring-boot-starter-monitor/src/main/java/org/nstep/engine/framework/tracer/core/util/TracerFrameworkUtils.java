package org.nstep.engine.framework.tracer.core.util;

import io.opentracing.Span;
import io.opentracing.tag.Tags;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 链路追踪工具类
 * 该工具类用于将异常信息记录到链路追踪的 Span 中，帮助进行链路追踪时的错误分析。
 */
public class TracerFrameworkUtils {

    /**
     * 将异常记录到 Span 中，参考自 com.aliyuncs.utils.TraceUtils
     *
     * @param throwable 异常
     * @param span      当前的 Span 对象
     */
    public static void onError(Throwable throwable, Span span) {
        // 在 Span 中设置错误标志
        Tags.ERROR.set(span, Boolean.TRUE);

        // 如果异常不为空，将异常信息记录到 Span 中
        if (throwable != null) {
            span.log(errorLogs(throwable));
        }
    }

    /**
     * 生成包含异常详细信息的日志
     *
     * @param throwable 异常对象
     * @return 包含异常详细信息的日志 Map
     */
    private static Map<String, Object> errorLogs(Throwable throwable) {
        // 创建一个存储异常信息的 Map
        Map<String, Object> errorLogs = new HashMap<>(10);

        // 记录事件类型为错误
        errorLogs.put("event", Tags.ERROR.getKey());

        // 记录异常对象
        errorLogs.put("error.object", throwable);

        // 记录异常类型
        errorLogs.put("error.kind", throwable.getClass().getName());

        // 获取异常的消息，如果有 cause 则获取 cause 的消息
        String message = throwable.getCause() != null ? throwable.getCause().getMessage() : throwable.getMessage();
        if (message != null) {
            errorLogs.put("message", message);
        }

        // 获取异常的堆栈信息
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        errorLogs.put("stack", sw.toString());

        return errorLogs;
    }

}
