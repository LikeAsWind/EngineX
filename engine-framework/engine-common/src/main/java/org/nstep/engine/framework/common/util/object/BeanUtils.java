package org.nstep.engine.framework.common.util.object;

import cn.hutool.core.bean.BeanUtil;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.collection.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * Bean 工具类
 * <p>
 * 1. 默认使用 {@link cn.hutool.core.bean.BeanUtil} 作为实现类，虽然不同 bean 工具的性能有差别，
 * 但是对绝大多数同学的项目，不用在意这点性能。
 * 2. 针对复杂的对象转换，可以参考 AuthConvert 实现，通过 mapstruct + default 配合实现。
 * <p>
 * 该工具类提供了常见的 Bean 转换操作，包括：
 * - 单个对象的转换
 * - 列表对象的转换
 * - 分页结果的转换
 * - 属性复制
 */
public class BeanUtils {

    /**
     * 将源对象转换为目标类型的对象
     * 使用 HuTool 的 {@link BeanUtil#toBean(Object, Class)} 方法将源对象转换为目标类型。
     *
     * @param source      源对象
     * @param targetClass 目标类型的 Class 对象
     * @param <T>         目标类型
     * @return 转换后的目标类型对象
     */
    public static <T> T toBean(Object source, Class<T> targetClass) {
        return BeanUtil.toBean(source, targetClass);
    }

    /**
     * 将源对象转换为目标类型的对象，并对目标对象执行额外操作
     * 在转换对象的同时，执行给定的 Consumer 操作（例如设置某些属性或执行额外的逻辑）。
     *
     * @param source      源对象
     * @param targetClass 目标类型的 Class 对象
     * @param peek        对目标对象执行的操作
     * @param <T>         目标类型
     * @return 转换后的目标类型对象
     */
    public static <T> T toBean(Object source, Class<T> targetClass, Consumer<T> peek) {
        T target = toBean(source, targetClass);
        if (target != null) {
            peek.accept(target);  // 执行额外的操作
        }
        return target;
    }

    /**
     * 将源对象列表转换为目标类型的对象列表
     * 将源列表中的每个对象转换为目标类型的对象，并返回转换后的列表。
     *
     * @param source     源对象列表
     * @param targetType 目标类型的 Class 对象
     * @param <S>        源类型
     * @param <T>        目标类型
     * @return 转换后的目标类型对象列表
     */
    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        return CollectionUtils.convertList(source, s -> toBean(s, targetType));  // 执行列表转换
    }

    /**
     * 将源对象列表转换为目标类型的对象列表，并对每个目标对象执行额外操作
     * 在转换对象的同时，执行给定的 Consumer 操作（例如设置某些属性或执行额外的逻辑）对每个目标对象进行处理。
     *
     * @param source     源对象列表
     * @param targetType 目标类型的 Class 对象
     * @param peek       对每个目标对象执行的操作
     * @param <S>        源类型
     * @param <T>        目标类型
     * @return 转换后的目标类型对象列表
     */
    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType, Consumer<T> peek) {
        List<T> list = toBean(source, targetType);
        if (list != null) {
            list.forEach(peek);  // 对每个目标对象执行额外操作
        }
        return list;
    }

    /**
     * 将分页结果中的源对象列表转换为目标类型的对象列表
     * 将分页结果中的每个源对象转换为目标类型的对象，并返回转换后的分页结果。
     *
     * @param source     源分页结果
     * @param targetType 目标类型的 Class 对象
     * @param <S>        源类型
     * @param <T>        目标类型
     * @return 转换后的目标类型分页结果
     */
    public static <S, T> PageResult<T> toBean(PageResult<S> source, Class<T> targetType) {
        return toBean(source, targetType, null);
    }

    /**
     * 将分页结果中的源对象列表转换为目标类型的对象列表，并对每个目标对象执行额外操作
     * 将分页结果中的每个源对象转换为目标类型的对象，并对每个目标对象执行给定的 Consumer 操作。
     *
     * @param source     源分页结果
     * @param targetType 目标类型的 Class 对象
     * @param peek       对每个目标对象执行的操作
     * @param <S>        源类型
     * @param <T>        目标类型
     * @return 转换后的目标类型分页结果
     */
    public static <S, T> PageResult<T> toBean(PageResult<S> source, Class<T> targetType, Consumer<T> peek) {
        if (source == null) {
            return null;
        }
        List<T> list = toBean(source.getList(), targetType);  // 转换分页结果中的列表
        if (peek != null) {
            list.forEach(peek);  // 对每个目标对象执行额外操作
        }
        return new PageResult<>(list, source.getTotal());  // 返回新的分页结果
    }

    /**
     * 将源对象的属性复制到目标对象
     * 使用 HuTool 的 {@link BeanUtil#copyProperties(Object, Object, boolean)} 方法将源对象的属性复制到目标对象。
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtil.copyProperties(source, target, false);  // 复制属性，忽略空值属性
    }

}
