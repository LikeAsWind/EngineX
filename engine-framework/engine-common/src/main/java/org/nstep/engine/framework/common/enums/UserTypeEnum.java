package org.nstep.engine.framework.common.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nstep.engine.framework.common.core.IntArrayValuable;

import java.util.Arrays;

/**
 * 全局用户类型枚举
 */
@AllArgsConstructor
@Getter
public enum UserTypeEnum implements IntArrayValuable {

    MEMBER(1, "会员"), // 面向 c 端，普通用户
    ADMIN(2, "管理员"); // 面向 b 端，管理后台

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(UserTypeEnum::getValue).toArray();

    /**
     * 类型
     */
    private final Integer value;
    /**
     * 类型名
     */
    private final String name;

    /**
     * 根据给定的整数类型值返回对应的枚举常量
     *
     * @param value 要查找的整数类型值
     * @return 如果找到对应的枚举常量，则返回该枚举常量；否则返回 null
     */
    public static UserTypeEnum valueOf(Integer value) {
        // 使用 ArrayUtil.firstMatch 方法查找第一个满足条件的枚举常量
        return ArrayUtil.firstMatch(userType -> userType.getValue().equals(value), UserTypeEnum.values());
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
