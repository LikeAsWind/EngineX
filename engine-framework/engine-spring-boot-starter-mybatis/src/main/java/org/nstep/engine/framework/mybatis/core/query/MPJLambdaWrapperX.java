package org.nstep.engine.framework.mybatis.core.query;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.nstep.engine.framework.common.util.collection.ArrayUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * 拓展 MyBatis Plus Join QueryWrapper 类，主要增加如下功能：
 * <p>
 * 1. 拼接条件的方法，增加 xxxIfPresent 方法，用于判断值不存在的时候，不要拼接到条件中。
 *
 * @param <T> 数据类型
 */
public class MPJLambdaWrapperX<T> extends MPJLambdaWrapper<T> {

    /**
     * 拼接 like 条件，只有在 val 不为空时才拼接。
     *
     * @param column 要查询的列
     * @param val    要匹配的值
     * @return 当前对象
     */
    public MPJLambdaWrapperX<T> likeIfPresent(SFunction<T, ?> column, String val) {
        // 如果 val 不为空，则拼接 like 条件
        if (StringUtils.hasText(val)) {
            return (MPJLambdaWrapperX<T>) super.like(column, val);
        }
        return this;
    }

    /**
     * 拼接 in 条件，只有在 values 不为空时才拼接。
     *
     * @param column 要查询的列
     * @param values 要匹配的值集合
     * @return 当前对象
     */
    public MPJLambdaWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        // 如果 values 不为空，则拼接 in 条件
        if (ObjectUtil.isAllNotEmpty(values) && !ArrayUtil.isEmpty(values)) {
            return (MPJLambdaWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    /**
     * 拼接 in 条件，只有在 values 不为空时才拼接。
     *
     * @param column 要查询的列
     * @param values 要匹配的值数组
     * @return 当前对象
     */
    public MPJLambdaWrapperX<T> inIfPresent(SFunction<T, ?> column, Object... values) {
        // 如果 values 不为空，则拼接 in 条件
        if (ObjectUtil.isAllNotEmpty(values) && !ArrayUtil.isEmpty(values)) {
            return (MPJLambdaWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    /**
     * 拼接 eq 条件，只有在 val 不为空时才拼接。
     *
     * @param column 要查询的列
     * @param val    要匹配的值
     * @return 当前对象
     */
    public MPJLambdaWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        // 如果 val 不为空，则拼接 eq 条件
        if (ObjectUtil.isNotEmpty(val)) {
            return (MPJLambdaWrapperX<T>) super.eq(column, val);
        }
        return this;
    }

    /**
     * 拼接 ne 条件，只有在 val 不为空时才拼接。
     *
     * @param column 要查询的列
     * @param val    要匹配的值
     * @return 当前对象
     */
    public MPJLambdaWrapperX<T> neIfPresent(SFunction<T, ?> column, Object val) {
        // 如果 val 不为空，则拼接 ne 条件
        if (ObjectUtil.isNotEmpty(val)) {
            return (MPJLambdaWrapperX<T>) super.ne(column, val);
        }
        return this;
    }

    /**
     * 拼接 gt 条件，只有在 val 不为空时才拼接。
     *
     * @param column 要查询的列
     * @param val    要匹配的值
     * @return 当前对象
     */
    public MPJLambdaWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        // 如果 val 不为空，则拼接 gt 条件
        if (val != null) {
            return (MPJLambdaWrapperX<T>) super.gt(column, val);
        }
        return this;
    }

    /**
     * 拼接 ge 条件，只有在 val 不为空时才拼接。
     *
     * @param column 要查询的列
     * @param val    要匹配的值
     * @return 当前对象
     */
    public MPJLambdaWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
        // 如果 val 不为空，则拼接 ge 条件
        if (val != null) {
            return (MPJLambdaWrapperX<T>) super.ge(column, val);
        }
        return this;
    }

    /**
     * 拼接 lt 条件，只有在 val 不为空时才拼接。
     *
     * @param column 要查询的列
     * @param val    要匹配的值
     * @return 当前对象
     */
    public MPJLambdaWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        // 如果 val 不为空，则拼接 lt 条件
        if (val != null) {
            return (MPJLambdaWrapperX<T>) super.lt(column, val);
        }
        return this;
    }

    /**
     * 拼接 le 条件，只有在 val 不为空时才拼接。
     *
     * @param column 要查询的列
     * @param val    要匹配的值
     * @return 当前对象
     */
    public MPJLambdaWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
        // 如果 val 不为空，则拼接 le 条件
        if (val != null) {
            return (MPJLambdaWrapperX<T>) super.le(column, val);
        }
        return this;
    }

    /**
     * 拼接 between 条件，只有在 val1 和 val2 都不为空时才拼接，或者根据 val1 或 val2 拼接 ge 或 le 条件。
     *
     * @param column 要查询的列
     * @param val1   范围的开始值
     * @param val2   范围的结束值
     * @return 当前对象
     */
    public MPJLambdaWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        // 如果 val1 和 val2 都不为空，则拼接 between 条件
        if (val1 != null && val2 != null) {
            return (MPJLambdaWrapperX<T>) super.between(column, val1, val2);
        }
        // 如果只有 val1 不为空，则拼接 ge 条件
        if (val1 != null) {
            return (MPJLambdaWrapperX<T>) ge(column, val1);
        }
        // 如果只有 val2 不为空，则拼接 le 条件
        if (val2 != null) {
            return (MPJLambdaWrapperX<T>) le(column, val2);
        }
        return this;
    }

    /**
     * 拼接 between 条件，传入数组作为范围值，只有在数组中的值不为空时才拼接。
     *
     * @param column 要查询的列
     * @param values 范围值数组
     * @return 当前对象
     */
    public MPJLambdaWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object[] values) {
        // 从数组中获取 val1 和 val2
        Object val1 = ArrayUtils.get(values, 0);
        Object val2 = ArrayUtils.get(values, 1);
        return betweenIfPresent(column, val1, val2);
    }

    // ========== 重写父类方法，方便链式调用 ==========

    /**
     * 条件性地添加等于条件的查询。
     * 如果condition为true，则添加字段等于值的条件。
     *
     * @param <X>       字段所在的实体类类型。
     * @param condition 条件判断，当为true时添加条件。
     * @param column    字段的函数式引用。
     * @param val       要比较的值。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public <X> MPJLambdaWrapperX<T> eq(boolean condition, SFunction<X, ?> column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    /**
     * 添加字段等于值的条件。
     *
     * @param <X>    字段所在的实体类类型。
     * @param column 字段的函数式引用。
     * @param val    要比较的值。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public <X> MPJLambdaWrapperX<T> eq(SFunction<X, ?> column, Object val) {
        super.eq(column, val);
        return this;
    }

    /**
     * 添加降序排序条件。
     *
     * @param <X>    字段所在的实体类类型。
     * @param column 字段的函数式引用。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public <X> MPJLambdaWrapperX<T> orderByDesc(SFunction<X, ?> column) {
        super.orderByDesc(true, column);
        return this;
    }

    /**
     * 添加原生SQL语句到查询末尾。
     *
     * @param lastSql 要添加的原生SQL语句。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public MPJLambdaWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    /**
     * 添加字段在集合中的查询条件。
     *
     * @param <X>    字段所在的实体类类型。
     * @param column 字段的函数式引用。
     * @param coll   要匹配的值集合。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public <X> MPJLambdaWrapperX<T> in(SFunction<X, ?> column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }

    /**
     * 选择所有字段。
     *
     * @param clazz 实体类。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public MPJLambdaWrapperX<T> selectAll(Class<?> clazz) {
        super.selectAll(clazz);
        return this;
    }

    /**
     * 选择所有字段，并指定前缀。
     *
     * @param clazz  实体类。
     * @param prefix 字段前缀。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public MPJLambdaWrapperX<T> selectAll(Class<?> clazz, String prefix) {
        super.selectAll(clazz, prefix);
        return this;
    }

    /**
     * 选择字段，并指定别名。
     *
     * @param <S>    字段所在的实体类类型。
     * @param column 字段的函数式引用。
     * @param alias  字段的别名。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectAs(SFunction<S, ?> column, String alias) {
        super.selectAs(column, alias);
        return this;
    }

    /**
     * 选择字段，并指定别名（通过字符串指定字段名）。
     *
     * @param <E>    字段所在的实体类类型。
     * @param column 字段名的字符串表示。
     * @param alias  字段的函数式引用别名。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public <E> MPJLambdaWrapperX<T> selectAs(String column, SFunction<E, ?> alias) {
        super.selectAs(column, alias);
        return this;
    }

    /**
     * 选择字段，并指定函数式引用别名。
     *
     * @param <S>    字段所在的实体类类型。
     * @param <X>    别名所在的实体类类型。
     * @param column 字段的函数式引用。
     * @param alias  字段的函数式引用别名。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectAs(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectAs(column, alias);
        return this;
    }

    /**
     * 选择字段，并指定索引和别名。
     *
     * @param <E>    字段所在的实体类类型。
     * @param index  字段索引。
     * @param column 字段的函数式引用。
     * @param alias  字段的函数式引用别名。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public <E, X> MPJLambdaWrapperX<T> selectAs(String index, SFunction<E, ?> column, SFunction<X, ?> alias) {
        super.selectAs(index, column, alias);
        return this;
    }

    /**
     * 选择字段作为特定类的字段。
     *
     * @param <E>    源字段所在的实体类类型。
     * @param source 源字段的实体类。
     * @param tag    目标字段的实体类。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public <E> MPJLambdaWrapperX<T> selectAsClass(Class<E> source, Class<?> tag) {
        super.selectAsClass(source, tag);
        return this;
    }

    /**
     * 选择子查询字段，并指定别名。
     *
     * @param <E>      子查询实体类类型。
     * @param <F>      别名所在的实体类类型。
     * @param clazz    子查询实体类。
     * @param consumer 子查询构建器。
     * @param alias    字段的函数式引用别名。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public <E, F> MPJLambdaWrapperX<T> selectSub(Class<E> clazz, Consumer<MPJLambdaWrapper<E>> consumer, SFunction<F, ?> alias) {
        super.selectSub(clazz, consumer, alias);
        return this;
    }

    /**
     * 选择子查询字段，并指定字符串和别名。
     *
     * @param <E>      子查询实体类类型。
     * @param <F>      别名所在的实体类类型。
     * @param clazz    子查询实体类。
     * @param st       子查询字符串。
     * @param consumer 子查询构建器。
     * @param alias    字段的函数式引用别名。
     * @return 返回当前MPJLambdaWrapperX对象，以支持链式调用。
     */
    @Override
    public <E, F> MPJLambdaWrapperX<T> selectSub(Class<E> clazz, String st, Consumer<MPJLambdaWrapper<E>> consumer, SFunction<F, ?> alias) {
        super.selectSub(clazz, st, consumer, alias);
        return this;
    }

    /**
     * 选择计数（COUNT）查询，对指定列进行计数。
     *
     * @param column 要计数的列。
     * @param <S>    泛型参数，代表列的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectCount(SFunction<S, ?> column) {
        super.selectCount(column);
        return this;
    }

    /**
     * 选择计数（COUNT）查询，对指定列进行计数，并为结果设置别名。
     *
     * @param column 要计数的列。
     * @param alias  结果的别名。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public MPJLambdaWrapperX<T> selectCount(Object column, String alias) {
        super.selectCount(column, alias);
        return this;
    }

    /**
     * 选择计数（COUNT）查询，对指定列进行计数，并为结果设置泛型别名。
     *
     * @param column 要计数的列。
     * @param alias  泛型别名。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <X> MPJLambdaWrapperX<T> selectCount(Object column, SFunction<X, ?> alias) {
        super.selectCount(column, alias);
        return this;
    }

    /**
     * 选择计数（COUNT）查询，对指定列进行计数，并为结果设置别名。
     *
     * @param column 要计数的列。
     * @param alias  结果的别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectCount(SFunction<S, ?> column, String alias) {
        super.selectCount(column, alias);
        return this;
    }

    /**
     * 选择计数（COUNT）查询，对指定列进行计数，并为结果设置泛型别名。
     *
     * @param column 要计数的列。
     * @param alias  泛型别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectCount(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectCount(column, alias);
        return this;
    }

    /**
     * 选择求和（SUM）查询，对指定列进行求和。
     *
     * @param column 要进行求和的列。
     * @param <S>    泛型参数，代表列的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectSum(SFunction<S, ?> column) {
        super.selectSum(column);
        return this;
    }

    /**
     * 选择求和（SUM）查询，对指定列进行求和，并为结果设置别名。
     *
     * @param column 要进行求和的列。
     * @param alias  结果的别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectSum(SFunction<S, ?> column, String alias) {
        super.selectSum(column, alias);
        return this;
    }

    /**
     * 选择求和（SUM）查询，对指定列进行求和，并为结果设置泛型别名。
     *
     * @param column 要进行求和的列。
     * @param alias  泛型别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectSum(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectSum(column, alias);
        return this;
    }

    /**
     * 选择最大值（MAX）查询，对指定列求最大值。
     *
     * @param column 要进行最大值查询的列。
     * @param <S>    泛型参数，代表列的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectMax(SFunction<S, ?> column) {
        super.selectMax(column);
        return this;
    }

    /**
     * 选择最大值（MAX）查询，对指定列求最大值，并为结果设置别名。
     *
     * @param column 要进行最大值查询的列。
     * @param alias  结果的别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectMax(SFunction<S, ?> column, String alias) {
        super.selectMax(column, alias);
        return this;
    }

    /**
     * 选择最大值（MAX）查询，对指定列求最大值，并为结果设置泛型别名。
     *
     * @param column 要进行最大值查询的列。
     * @param alias  泛型别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectMax(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectMax(column, alias);
        return this;
    }

    /**
     * 选择最小值（MIN）查询，对指定列求最小值。
     *
     * @param column 要进行最小值查询的列。
     * @param <S>    泛型参数，代表列的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectMin(SFunction<S, ?> column) {
        super.selectMin(column);
        return this;
    }

    /**
     * 选择最小值（MIN）查询，对指定列求最小值，并为结果设置别名。
     *
     * @param column 要进行最小值查询的列。
     * @param alias  结果的别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectMin(SFunction<S, ?> column, String alias) {
        super.selectMin(column, alias);
        return this;
    }

    /**
     * 选择最小值（MIN）查询，对指定列求最小值，并为结果设置泛型别名。
     *
     * @param column 要进行最小值查询的列。
     * @param alias  泛型别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectMin(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectMin(column, alias);
        return this;
    }

    /**
     * 选择平均值（AVG）查询，对指定列求平均值。
     *
     * @param column 要进行平均值查询的列。
     * @param <S>    泛型参数，代表列的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectAvg(SFunction<S, ?> column) {
        super.selectAvg(column);
        return this;
    }

    /**
     * 选择平均值（AVG）查询，对指定列求平均值，并为结果设置别名。
     *
     * @param column 要进行平均值查询的列。
     * @param alias  结果的别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectAvg(SFunction<S, ?> column, String alias) {
        super.selectAvg(column, alias);
        return this;
    }

    /**
     * 选择平均值（AVG）查询，对指定列求平均值，并为结果设置泛型别名。
     *
     * @param column 要进行平均值查询的列。
     * @param alias  泛型别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectAvg(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectAvg(column, alias);
        return this;
    }

    /**
     * 选择长度（LEN）查询，对指定列求长度。
     *
     * @param column 要进行长度查询的列。
     * @param <S>    泛型参数，代表列的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectLen(SFunction<S, ?> column) {
        super.selectLen(column);
        return this;
    }

    /**
     * 选择长度（LEN）查询，对指定列求长度，并为结果设置别名。
     *
     * @param column 要进行长度查询的列。
     * @param alias  结果的别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectLen(SFunction<S, ?> column, String alias) {
        super.selectLen(column, alias);
        return this;
    }

    /**
     * 选择长度（LEN）查询，对指定列求长度，并为结果设置泛型别名。
     *
     * @param column 要进行长度查询的列。
     * @param alias  泛型别名。
     * @param <S>    泛型参数，代表列的类型。
     * @param <X>    泛型参数，代表别名的类型。
     * @return 返回当前MPJLambdaWrapperX对象，用于链式调用。
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectLen(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectLen(column, alias);
        return this;
    }
}
