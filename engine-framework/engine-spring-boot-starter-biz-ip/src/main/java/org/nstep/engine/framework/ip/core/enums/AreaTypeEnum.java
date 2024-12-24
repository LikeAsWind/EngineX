package org.nstep.engine.framework.ip.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nstep.engine.framework.common.core.IntArrayValuable;

import java.util.Arrays;

/**
 * 区域类型枚举，实现了IntArrayValuable接口，用于表示不同级别的行政区划。
 */
@AllArgsConstructor
@Getter
public enum AreaTypeEnum implements IntArrayValuable {

    COUNTRY(1, "国家"),
    PROVINCE(2, "省份"),
    CITY(3, "城市"),
    DISTRICT(4, "地区"), // 县、镇、区等
    ;

    /**
     * 将枚举值转换为数组，包含所有枚举实例的type值。
     */
    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(AreaTypeEnum::getType).toArray();

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名字
     */
    private final String name;

    /**
     * 实现IntArrayValuable接口的方法，返回包含所有枚举实例type值的数组。
     *
     * @return 包含所有枚举实例type值的数组。
     */
    @Override
    public int[] array() {
        return ARRAYS;
    }
}
