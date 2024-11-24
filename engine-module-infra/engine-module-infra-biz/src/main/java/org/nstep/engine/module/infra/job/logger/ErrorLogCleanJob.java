package org.nstep.engine.module.infra.job.logger;

import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.tenant.core.aop.TenantIgnore;
import org.nstep.engine.module.infra.service.logger.ApiErrorLogService;
import org.springframework.stereotype.Component;

/**
 * 物理删除 N 天前的错误日志的 Job
 */
@Slf4j
@Component
public class ErrorLogCleanJob {

    /**
     * 清理超过（14）天的日志
     */
    private static final Integer JOB_CLEAN_RETAIN_DAY = 14;
    /**
     * 每次删除间隔的条数，如果值太高可能会造成数据库的压力过大
     */
    private static final Integer DELETE_LIMIT = 100;
    @Resource
    private ApiErrorLogService apiErrorLogService;

    @XxlJob("errorLogCleanJob")
    @TenantIgnore
    public void execute() {
        Integer count = apiErrorLogService.cleanErrorLog(JOB_CLEAN_RETAIN_DAY, DELETE_LIMIT);
        log.info("[execute][定时执行清理错误日志数量 ({}) 个]", count);
    }

}
