package org.nstep.engine.framework.common.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nstep.engine.framework.common.core.IntArrayValuable;

import java.util.Arrays;

/**
 * 通用状态枚举
 */
@Getter
@AllArgsConstructor
public enum CommonStatusEnum implements IntArrayValuable {

    ENABLE(0, "开启"),
    DISABLE(1, "关闭");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CommonStatusEnum::getStatus).toArray();

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    /**
     * 判断给定的状态是否为启用状态
     *
     * @param status 要检查的状态
     * @return 如果状态为启用状态，则返回 true；否则返回 false
     */
    public static boolean isEnable(Integer status) {
        // 检查给定的状态是否与 ENABLE 枚举常量的状态值相等
        return ObjUtil.equal(ENABLE.status, status);
    }


    /**
     * 判断给定的状态是否为禁用状态
     *
     * @param status 要检查的状态
     * @return 如果状态为禁用状态，则返回 true；否则返回 false
     */
    public static boolean isDisable(Integer status) {
        // 检查给定的状态是否与 DISABLE 枚举常量的状态值相等
        return ObjUtil.equal(DISABLE.status, status);
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
