package org.nstep.engine.framework.common.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nstep.engine.framework.common.core.IntArrayValuable;

import java.util.Arrays;

/**
 * 时间间隔的枚举
 */
@Getter
@AllArgsConstructor
public enum DateIntervalEnum implements IntArrayValuable {

    DAY(1, "天"),
    WEEK(2, "周"),
    MONTH(3, "月"),
    QUARTER(4, "季度"),
    YEAR(5, "年");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(DateIntervalEnum::getInterval).toArray();

    /**
     * 类型
     */
    private final Integer interval;
    /**
     * 名称
     */
    private final String name;

    /**
     * 根据给定的整数间隔值返回对应的枚举常量
     *
     * @param interval 要查找的整数间隔值
     * @return 如果找到对应的枚举常量，则返回该枚举常量；否则返回 null
     */
    public static DateIntervalEnum valueOf(Integer interval) {
        // 使用 ArrayUtil.firstMatch 方法查找第一个满足条件的枚举常量
        return ArrayUtil.firstMatch(item -> item.getInterval().equals(interval), DateIntervalEnum.values());
    }


    /**
     * 重写 IntArrayValuable 接口的 array 方法
     * 返回一个整数数组，该数组包含了枚举类中所有状态值
     *
     * @return 整数数组，包含了枚举类中所有状态值
     */
    @Override
    public int[] array() {
        return ARRAYS;
    }


}