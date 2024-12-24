package org.nstep.engine.framework.common.util.object;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Object 工具类
 * <p>
 * 提供一些常用的 Object 操作工具方法，例如：
 * - 对象的克隆，忽略特定字段（如 id）
 * - 比较两个对象的最大值
 * - 返回非空的第一个值
 * - 判断对象是否在指定数组中
 */
public class ObjectUtils {

    /**
     * 复制对象，并忽略 Id 编号
     * <p>
     * 使用 Hutool 的 ObjectUtil.clone 方法克隆对象，并将 "id" 字段的值设为 null，
     * 同时支持通过消费者（Consumer）对克隆后的对象进行二次编辑。
     *
     * @param object   被复制的对象
     * @param consumer 对克隆后的对象进行二次编辑的消费者
     * @param <T>      对象类型
     * @return 复制后的对象
     */
    public static <T> T cloneIgnoreId(T object, Consumer<T> consumer) {
        // 克隆对象
        T result = ObjectUtil.clone(object);
        // 忽略 id 字段
        Field field = ReflectUtil.getField(object.getClass(), "id");
        if (field != null) {
            ReflectUtil.setFieldValue(result, field, null);  // 将 id 字段的值设为 null
        }
        // 二次编辑
        if (result != null) {
            consumer.accept(result);  // 对克隆后的对象执行额外操作
        }
        return result;
    }

    /**
     * 比较两个对象的大小，返回较大的对象
     * <p>
     * 如果一个对象为 null，返回另一个对象；如果两个对象都不为 null，则比较它们的大小。
     *
     * @param obj1 第一个对象
     * @param obj2 第二个对象
     * @param <T>  对象类型，必须实现 Comparable 接口
     * @return 较大的对象
     */
    public static <T extends Comparable<T>> T max(T obj1, T obj2) {
        if (obj1 == null) {
            return obj2;
        }
        if (obj2 == null) {
            return obj1;
        }
        return obj1.compareTo(obj2) > 0 ? obj1 : obj2;  // 比较并返回较大的对象
    }

    /**
     * 返回第一个非 null 的值
     * <p>
     * 遍历传入的参数，返回第一个非 null 的值。如果所有值都为 null，则返回 null。
     *
     * @param array 可变参数，包含多个对象
     * @param <T>   对象类型
     * @return 第一个非 null 的值，若所有值都为 null 则返回 null
     */
    @SafeVarargs
    public static <T> T defaultIfNull(T... array) {
        for (T item : array) {
            if (item != null) {
                return item;  // 返回第一个非 null 的值
            }
        }
        return null;  // 如果所有值都为 null，返回 null
    }

    /**
     * 判断对象是否在指定数组中
     * <p>
     * 判断传入的对象是否存在于指定的数组中，返回布尔值。
     *
     * @param obj   要判断的对象
     * @param array 对象数组
     * @param <T>   对象类型
     * @return 如果对象在数组中，则返回 true；否则返回 false
     */
    @SafeVarargs
    public static <T> boolean equalsAny(T obj, T... array) {
        return Arrays.asList(array).contains(obj);  // 判断对象是否在数组中
    }

}
