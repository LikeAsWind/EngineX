package org.nstep.engine.framework.tenant.core.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.framework.tenant.core.service.TenantFrameworkService;
import org.nstep.engine.framework.tenant.core.util.TenantUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 多租户 JobHandler AOP
 * 任务执行时，会按照租户逐个执行 Job 的逻辑
 * <p>
 * 注意，需要保证 JobHandler 的幂等性。因为 Job 因为某个租户执行失败重试时，之前执行成功的租户也会再次执行。
 */
@Aspect // 标记这是一个切面类，用于拦截方法
@RequiredArgsConstructor // 使用 Lombok 自动生成构造函数
@Slf4j // 使用 Lombok 自动生成日志记录功能
public class TenantJobAspect {

    private final TenantFrameworkService tenantFrameworkService; // 注入多租户框架服务

    /**
     * 环绕通知，拦截带有 {@link TenantJob} 注解的方法。
     * <p>
     * 任务执行时，按照租户逐个执行 Job 的逻辑。每个租户的执行结果都会被记录下来，
     * 如果某个租户执行失败，整个任务会被标记为失败。
     *
     * @param joinPoint 当前执行的方法的连接点
     */
    @Around("@annotation(TenantJob)") // 拦截所有带有 @TenantJob 注解的方法
    public void around(ProceedingJoinPoint joinPoint) {
        // 获得所有租户的租户 ID
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (CollUtil.isEmpty(tenantIds)) {
            return; // 如果没有租户，直接返回
        }

        // 逐个租户执行 Job
        Map<Long, String> results = new ConcurrentHashMap<>(); // 存储每个租户的执行结果
        AtomicBoolean success = new AtomicBoolean(true); // 标记是否存在失败的情况

        // 使用并行流对每个租户进行处理
        tenantIds.parallelStream().forEach(tenantId -> {
            // 执行租户上下文环境下的任务
            TenantUtils.execute(tenantId, () -> {
                try {
                    // 执行方法逻辑
                    Object result = joinPoint.proceed();
                    // 记录每个租户的执行结果
                    results.put(tenantId, StrUtil.toStringOrNull(result));
                } catch (Throwable e) {
                    // 捕获异常并记录
                    results.put(tenantId, ExceptionUtil.getRootCauseMessage(e));
                    success.set(false); // 如果有异常，标记为失败
                    // 打印异常日志
                    XxlJobHelper.log(StrUtil.format("[多租户({}) 执行任务({})，发生异常：{}]",
                            tenantId, joinPoint.getSignature(), ExceptionUtils.getStackTrace(e)));
                }
            });
        });

        // 记录任务执行结果
        if (success.get()) {
            // 如果所有租户执行成功，标记任务成功
            XxlJobHelper.handleSuccess(JsonUtils.toJsonString(results));
        } else {
            // 如果有失败，标记任务失败
            XxlJobHelper.handleFail(JsonUtils.toJsonString(results));
        }
    }
}
