package org.nstep.engine.framework.operatelog.config;

import com.mzt.logapi.service.ILogRecordService;
import com.mzt.logapi.starter.annotation.EnableLogRecord;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.operatelog.core.service.LogRecordServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 操作日志配置类
 * <p>
 * 该类用于配置操作日志服务。通过 `@EnableLogRecord` 注解启用日志记录功能，并配置默认的日志记录服务实现。
 * </p>
 */
@EnableLogRecord(tenant = "") // 这里的 tenant 参数为空，表示不指定租户信息
@AutoConfiguration // 标记为自动配置类，Spring Boot 会自动扫描并加载该配置
@Slf4j // 自动生成日志记录功能
public class EngineOperateLogConfiguration {

    /**
     * 创建并返回一个默认的日志记录服务实现
     * <p>
     * 该方法返回一个 `LogRecordServiceImpl` 实例，作为操作日志记录服务的实现。
     * </p>
     *
     * @return ILogRecordService 实现类
     */
    @Bean
    @Primary // 标记为首选的 Bean，当有多个 Bean 时，Spring 会优先使用该 Bean
    public ILogRecordService iLogRecordServiceImpl() {
        return new LogRecordServiceImpl(); // 返回默认的日志记录服务实现
    }

}
