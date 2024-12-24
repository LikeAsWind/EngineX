package org.nstep.engine.framework.common.util.collection;

import cn.hutool.core.collection.CollUtil;

import java.util.Set;

/**
 * Set 工具类
 */
public class SetUtils {

    /**
     * 将一个可变参数列表转换为一个 Set 对象
     *
     * @param objs 可变参数列表
     * @return 转换后的 Set 对象
     */
    @SafeVarargs
    public static <T> Set<T> asSet(T... objs) {
        return CollUtil.newHashSet(objs);
    }

}
