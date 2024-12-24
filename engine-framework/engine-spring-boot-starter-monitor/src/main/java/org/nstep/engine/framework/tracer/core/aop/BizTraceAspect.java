package org.nstep.engine.framework.tracer.core.aop;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nstep.engine.framework.common.util.spring.SpringExpressionUtils;
import org.nstep.engine.framework.tracer.core.annotation.BizTrace;
import org.nstep.engine.framework.tracer.core.util.TracerFrameworkUtils;

import java.util.Map;

import static java.util.Arrays.asList;

/**
 * {@link BizTrace} 切面，记录业务链路
 * 这个切面用于记录标注了 {@link BizTrace} 注解的方法的业务链路信息。
 */
@Aspect // 标记此类为切面，用于面向切面编程（AOP）。
@AllArgsConstructor // 自动生成一个构造函数，用于初始化所有的 final 字段，在这里是初始化 `tracer`。
@Slf4j // 提供日志功能，使用 SLF4J。
public class BizTraceAspect {

    // 业务操作名称的前缀
    private static final String BIZ_OPERATION_NAME_PREFIX = "Biz/";

    private final Tracer tracer; // Tracer 实例，用于创建和管理 Span。

    /**
     * 环绕通知，拦截所有标注了 @BizTrace 注解的方法。
     *
     * @param joinPoint 连接点，表示被拦截的方法。
     * @param trace     业务追踪注解实例，包含业务编号和类型等信息。
     * @return 方法执行的结果。
     * @throws Throwable 如果方法执行过程中抛出异常，会被继续抛出。
     */
    @Around(value = "@annotation(trace)")
    public Object around(ProceedingJoinPoint joinPoint, BizTrace trace) throws Throwable {
        // 创建一个新的 Span，并设置操作名称
        String operationName = getOperationName(joinPoint, trace);
        Span span = tracer.buildSpan(operationName) // 创建一个 Span，设置操作名称。
                .withTag(Tags.COMPONENT.getKey(), "biz") // 给 Span 打上 "biz" 组件标签。
                .start(); // 启动 Span。

        try {
            // 执行原方法并返回结果。
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            // 如果发生异常，在 Span 中记录异常信息。
            TracerFrameworkUtils.onError(throwable, span);
            throw throwable; // 继续抛出异常。
        } finally {
            // 设置业务相关的标签（如 biz.id 和 biz.type）到 Span 中。
            setBizTag(span, joinPoint, trace);
            // 完成 Span，标记追踪结束。
            span.finish();
        }
    }

    /**
     * 根据方法名或自定义操作名，获取 Span 的操作名称。
     *
     * @param joinPoint 连接点，表示被拦截的方法。
     * @param trace     业务追踪注解实例，可能包含自定义的操作名称。
     * @return Span 的操作名称。
     */
    private String getOperationName(ProceedingJoinPoint joinPoint, BizTrace trace) {
        // 如果注解中有自定义的操作名称，则使用自定义名称。
        if (StrUtil.isNotEmpty(trace.operationName())) {
            return BIZ_OPERATION_NAME_PREFIX + trace.operationName();
        }
        // 默认操作名称：使用类名和方法名。
        return BIZ_OPERATION_NAME_PREFIX
                + joinPoint.getSignature().getDeclaringType().getSimpleName() // 类名
                + "/" + joinPoint.getSignature().getName(); // 方法名
    }

    /**
     * 设置业务相关的标签（如 biz.id 和 biz.type）到 Span 中。
     * 这些标签通过 Spring 表达式语言（SpEL）从方法参数中解析。
     *
     * @param span      要设置标签的 Span。
     * @param joinPoint 连接点，表示被拦截的方法。
     * @param trace     业务追踪注解实例，包含业务编号和类型的字段。
     */
    private void setBizTag(Span span, ProceedingJoinPoint joinPoint, BizTrace trace) {
        try {
            // 使用 Spring 表达式语言（SpEL）解析方法参数，获取业务类型和业务编号。
            Map<String, Object> result = SpringExpressionUtils.parseExpressions(joinPoint, asList(trace.type(), trace.id()));
            // 将解析到的业务类型和业务编号作为标签设置到 Span 中。
            span.setTag(BizTrace.TYPE_TAG, MapUtil.getStr(result, trace.type())); // 设置 biz.type 标签。
            span.setTag(BizTrace.ID_TAG, MapUtil.getStr(result, trace.id())); // 设置 biz.id 标签。
        } catch (Exception ex) {
            // 如果在解析标签时发生异常，记录错误日志。
            log.error("[setBizTag][解析 bizType 与 bizId 发生异常]", ex);
        }
    }

}
