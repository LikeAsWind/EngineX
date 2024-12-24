package org.nstep.engine.framework.common.util.collection;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.util.ArrayUtil;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.nstep.engine.framework.common.util.collection.CollectionUtils.convertList;

/**
 * Array 工具类
 */
public class ArrayUtils {

    /**
     * 将 object 和 newElements 合并成一个数组
     *
     * @param object      对象
     * @param newElements 数组
     * @param <T>         泛型
     * @return 结果数组
     */
    @SafeVarargs
    public static <T> Consumer<T>[] append(Consumer<T> object, Consumer<T>... newElements) {
        // 如果 object 为 null，则返回 newElements
        if (object == null) {
            return newElements;
        }
        // 创建一个新的 Consumer<T> 数组，长度为 1 + newElements.length
        Consumer<T>[] result = ArrayUtil.newArray(Consumer.class, 1 + newElements.length);
        // 将 object 放入结果数组的第一个位置
        result[0] = object;
        // 将 newElements 中的元素复制到结果数组中，从第二个位置开始
        System.arraycopy(newElements, 0, result, 1, newElements.length);
        // 返回结果数组
        return result;
    }

    /**
     * 将集合转换为数组
     *
     * @param from   集合
     * @param mapper 转换函数
     * @param <T>    集合元素类型
     * @param <V>    数组元素类型
     * @return 数组
     */
    public static <T, V> V[] toArray(Collection<T> from, Function<T, V> mapper) {
        // 调用 convertList 方法将集合转换为列表，然后再调用 toArray 方法将列表转换为数组
        return toArray(convertList(from, mapper));
    }

    /**
     * 将集合转换为数组
     *
     * @param from 集合
     * @param <T>  集合元素类型
     * @return 数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> from) {
        // 如果集合为空，则返回一个空数组
        if (CollectionUtil.isEmpty(from)) {
            return (T[]) (new Object[0]);
        }
        // 使用 ArrayUtil.toArray 方法将集合转换为数组，并指定数组元素类型为迭代器返回的元素类型
        return ArrayUtil.toArray(from, (Class<T>) IterUtil.getElementType(from.iterator()));
    }

    /**
     * 获取数组中指定索引的元素
     *
     * @param array 数组
     * @param index 索引
     * @param <T>   数组元素类型
     * @return 元素
     */
    public static <T> T get(T[] array, int index) {
        // 如果数组为 null 或索引超出数组长度，则返回 null
        if (null == array || index >= array.length) {
            return null;
        }
        // 返回数组中指定索引的元素
        return array[index];
    }

}
