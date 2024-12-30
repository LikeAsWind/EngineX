package org.nstep.engine.framework.xxljob.enums;

/**
 * 调度类型枚举
 * <p>
 * 该枚举定义了不同的调度类型，用于描述任务调度的方式。每种类型对应不同的调度策略，适用于不同的业务需求。
 * <p>
 * 调度类型包括：
 * - NONE: 无调度，表示不进行任何调度。
 * - CRON: 使用 Cron 表达式进行调度，适用于复杂的时间表达式配置。
 * - FIX_RATE: 固定速率调度，按固定时间间隔（秒）执行任务。
 * </p>
 * <p>
 * 选择何种调度类型取决于任务的执行频率和时间要求。
 * </p>
 *
 * @author hanabi
 */
public enum ScheduleTypeEnum {

    /**
     * NONE: 无调度
     */
    NONE,

    /**
     * CRON: 使用 Cron 表达式调度
     */
    CRON,

    /**
     * FIX_RATE: 固定速率调度，按秒执行
     */
    FIX_RATE;

    // 枚举构造函数，默认无参
    ScheduleTypeEnum() {
    }

}
