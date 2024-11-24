package org.nstep.engine.module.infra.job.logger;

import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.tenant.core.aop.TenantIgnore;
import org.nstep.engine.module.infra.service.logger.ApiAccessLogService;
import org.springframework.stereotype.Component;

/**
 * 物理删除 N 天前的访问日志的 Job
 */
@Component
@Slf4j
public class AccessLogCleanJob {

    /**
     * 清理超过（14）天的日志
     */
    private static final Integer JOB_CLEAN_RETAIN_DAY = 14;
    /**
     * 每次删除间隔的条数，如果值太高可能会造成数据库的压力过大
     */
    private static final Integer DELETE_LIMIT = 100;
    @Resource
    private ApiAccessLogService apiAccessLogService;

    @XxlJob("accessLogCleanJob")
    @TenantIgnore
    public void execute() {
        Integer count = apiAccessLogService.cleanAccessLog(JOB_CLEAN_RETAIN_DAY, DELETE_LIMIT);
        log.info("[execute][定时执行清理访问日志数量 ({}) 个]", count);
    }

}
