package org.nstep.engine.module.infra.enums;

/**
 * Infra 字典类型的枚举类
 * <p>
 * 该接口定义了多个字典类型常量，这些常量用于在系统中表示不同类型的字典数据，
 * 例如定时任务状态、API 错误日志处理状态等。通过统一管理这些字典类型，便于在系统中进行引用和维护。
 */
public interface DictTypeConstants {

    /**
     * 定时任务状态的字典类型
     * <p>
     * 用于表示定时任务的状态，如：正在运行、已完成、失败等。
     */
    String JOB_STATUS = "infra_job_status"; // 定时任务状态的枚举

    /**
     * 定时任务日志状态的字典类型
     * <p>
     * 用于表示定时任务日志的状态，如：已记录、已处理等。
     */
    String JOB_LOG_STATUS = "infra_job_log_status"; // 定时任务日志状态的枚举

    /**
     * API 错误日志处理状态的字典类型
     * <p>
     * 用于表示 API 错误日志的处理状态，如：待处理、已处理等。
     */
    String API_ERROR_LOG_PROCESS_STATUS = "infra_api_error_log_process_status"; // API 错误日志的处理状态的枚举

    /**
     * 参数配置类型的字典类型
     * <p>
     * 用于表示系统中不同的参数配置类型。
     */
    String CONFIG_TYPE = "infra_config_type"; // 参数配置类型

    /**
     * Boolean 类型的字典类型
     * <p>
     * 用于表示布尔值类型的字典，如：true 或 false。
     */
    String BOOLEAN_STRING = "infra_boolean_string"; // Boolean 是否类型

    /**
     * 操作类型的字典类型
     * <p>
     * 用于表示不同的操作类型，如：创建、删除、更新等。
     */
    String OPERATE_TYPE = "infra_operate_type"; // 操作类型
}
