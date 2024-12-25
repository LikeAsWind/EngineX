package org.nstep.engine.module.infra.job.logger;

import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.tenant.core.aop.TenantIgnore;
import org.nstep.engine.module.infra.service.logger.ApiAccessLogService;
import org.springframework.stereotype.Component;

/**
 * 物理删除 N 天前的访问日志的 Job
 * <p>
 * 该定时任务用于清理超过指定天数的访问日志，避免日志文件积压导致存储压力。
 * 默认清理超过 14 天的日志，并且每次清理的日志条数有限制，以防止清理过程中对数据库造成过大压力。
 * 通过注解 @XxlJob 定义为一个定时任务，并使用 @TenantIgnore 忽略租户相关的逻辑。
 */
@Component
@Slf4j
public class AccessLogCleanJob {

    /**
     * 清理超过（14）天的日志
     * <p>
     * 定义日志保留的天数，超过该天数的日志将会被删除。
     */
    private static final Integer JOB_CLEAN_RETAIN_DAY = 14;

    /**
     * 每次删除间隔的条数，如果值太高可能会造成数据库的压力过大
     * <p>
     * 该限制控制每次删除的日志条数，避免单次删除操作对数据库性能造成过大影响。
     */
    private static final Integer DELETE_LIMIT = 100;

    /**
     * 注入 ApiAccessLogService，用于执行访问日志的清理操作
     */
    @Resource
    private ApiAccessLogService apiAccessLogService;

    /**
     * 定时任务执行方法，清理超过指定天数的访问日志
     * <p>
     * 该方法每次执行时，会调用 ApiAccessLogService 的清理方法，删除超过 14 天的日志。
     * 执行完毕后，日志会记录清理的日志数量。
     */
    @XxlJob("accessLogCleanJob")
    @TenantIgnore  // 忽略租户相关的逻辑
    public void execute() {
        // 调用服务方法，清理日志
        Integer count = apiAccessLogService.cleanAccessLog(JOB_CLEAN_RETAIN_DAY, DELETE_LIMIT);
        // 记录清理日志的数量
        log.info("[execute][定时执行清理访问日志数量 ({}) 个]", count);
    }

}
