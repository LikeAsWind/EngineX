package org.nstep.engine.framework.xxljob.enums;

/**
 * 调度过期策略枚举
 * <p>
 * 该枚举定义了任务调度中处理过期任务的策略。任务可能由于某些原因未能按时执行，过期策略决定了如何处理这些任务。
 * <p>
 * 调度过期策略包括：
 * - DO_NOTHING: 不做任何处理，即跳过过期任务。
 * - FIRE_ONCE_NOW: 立即执行一次过期任务，即当任务错过执行时间时，立即执行一次任务。
 * </p>
 * <p>
 * 选择何种策略取决于具体业务场景及任务的紧急程度。
 * </p>
 *
 * @author hanabi
 */
public enum MisfireStrategyEnum {

    /**
     * DO_NOTHING: 不做任何处理，跳过过期任务
     */
    DO_NOTHING,

    /**
     * FIRE_ONCE_NOW: 立即执行一次过期任务
     */
    FIRE_ONCE_NOW;

    // 枚举构造函数，默认无参
    MisfireStrategyEnum() {
    }
}
