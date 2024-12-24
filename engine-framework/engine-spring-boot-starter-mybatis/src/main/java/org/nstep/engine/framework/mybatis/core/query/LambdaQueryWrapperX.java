package org.nstep.engine.framework.mybatis.core.query;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.nstep.engine.framework.common.util.collection.ArrayUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 拓展 MyBatis Plus QueryWrapper 类，主要增加如下功能：
 * <p>
 * 1. 拼接条件的方法，增加 xxxIfPresent 方法，用于判断值不存在的时候，不要拼接到条件中。
 *
 * @param <T> 数据类型
 */
public class LambdaQueryWrapperX<T> extends LambdaQueryWrapper<T> {

    /**
     * 如果值存在，则拼接 LIKE 条件。
     *
     * @param column 字段
     * @param val    查询值
     * @return 当前 LambdaQueryWrapperX 实例
     */
    public LambdaQueryWrapperX<T> likeIfPresent(SFunction<T, ?> column, String val) {
        if (StringUtils.hasText(val)) {
            return (LambdaQueryWrapperX<T>) super.like(column, val);
        }
        return this;
    }

    /**
     * 如果值集合存在，则拼接 IN 条件。
     *
     * @param column 字段
     * @param values 查询值集合
     * @return 当前 LambdaQueryWrapperX 实例
     */
    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        if (ObjectUtil.isAllNotEmpty(values) && !ArrayUtil.isEmpty(values)) {
            return (LambdaQueryWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    /**
     * 如果值数组存在，则拼接 IN 条件。
     *
     * @param column 字段
     * @param values 查询值数组
     * @return 当前 LambdaQueryWrapperX 实例
     */
    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Object... values) {
        if (ObjectUtil.isAllNotEmpty(values) && !ArrayUtil.isEmpty(values)) {
            return (LambdaQueryWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    /**
     * 如果值存在，则拼接 = 条件。
     *
     * @param column 字段
     * @param val    查询值
     * @return 当前 LambdaQueryWrapperX 实例
     */
    public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return (LambdaQueryWrapperX<T>) super.eq(column, val);
        }
        return this;
    }

    /**
     * 如果值存在，则拼接 != 条件。
     *
     * @param column 字段
     * @param val    查询值
     * @return 当前 LambdaQueryWrapperX 实例
     */
    public LambdaQueryWrapperX<T> neIfPresent(SFunction<T, ?> column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return (LambdaQueryWrapperX<T>) super.ne(column, val);
        }
        return this;
    }

    /**
     * 如果值存在，则拼接 > 条件。
     *
     * @param column 字段
     * @param val    查询值
     * @return 当前 LambdaQueryWrapperX 实例
     */
    public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX<T>) super.gt(column, val);
        }
        return this;
    }

    /**
     * 如果值存在，则拼接 >= 条件。
     *
     * @param column 字段
     * @param val    查询值
     * @return 当前 LambdaQueryWrapperX 实例
     */
    public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX<T>) super.ge(column, val);
        }
        return this;
    }

    /**
     * 如果值存在，则拼接 < 条件。
     *
     * @param column 字段
     * @param val    查询值
     * @return 当前 LambdaQueryWrapperX 实例
     */
    public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX<T>) super.lt(column, val);
        }
        return this;
    }

    /**
     * 如果值存在，则拼接 <= 条件。
     *
     * @param column 字段
     * @param val    查询值
     * @return 当前 LambdaQueryWrapperX 实例
     */
    public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX<T>) super.le(column, val);
        }
        return this;
    }

    /**
     * 如果值存在，则拼接 BETWEEN 条件；如果只有一个值存在，则拼接 >= 或 <= 条件。
     *
     * @param column 字段
     * @param val1   第一个值
     * @param val2   第二个值
     * @return 当前 LambdaQueryWrapperX 实例
     */
    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (LambdaQueryWrapperX<T>) super.between(column, val1, val2);
        }
        if (val1 != null) {
            return (LambdaQueryWrapperX<T>) ge(column, val1);
        }
        if (val2 != null) {
            return (LambdaQueryWrapperX<T>) le(column, val2);
        }
        return this;
    }

    /**
     * 如果值数组存在，则拼接 BETWEEN 条件；如果只有一个值存在，则拼接 >= 或 <= 条件。
     *
     * @param column 字段
     * @param values 查询值数组
     * @return 当前 LambdaQueryWrapperX 实例
     */
    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object[] values) {
        Object val1 = ArrayUtils.get(values, 0);
        Object val2 = ArrayUtils.get(values, 1);
        return betweenIfPresent(column, val1, val2);
    }

    /**
     * 重写父类方法，当条件为真时，添加等于条件的查询。
     *
     * @param condition 添加条件的布尔值，只有当此值为true时，才会添加条件。
     * @param column    列的函数式引用，指向实体类中对应的字段。
     * @param val       要比较的值。
     * @return 返回当前对象，以支持链式调用。
     */
    @Override
    public LambdaQueryWrapperX<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    /**
     * 重写父类方法，添加等于条件的查询。
     *
     * @param column 列的函数式引用，指向实体类中对应的字段。
     * @param val    要比较的值。
     * @return 返回当前对象，以支持链式调用。
     */
    @Override
    public LambdaQueryWrapperX<T> eq(SFunction<T, ?> column, Object val) {
        super.eq(column, val);
        return this;
    }

    /**
     * 重写父类方法，添加降序排序条件。
     *
     * @param column 列的函数式引用，指向实体类中对应的字段。
     * @return 返回当前对象，以支持链式调用。
     */
    @Override
    public LambdaQueryWrapperX<T> orderByDesc(SFunction<T, ?> column) {
        super.orderByDesc(true, column);
        return this;
    }

    /**
     * 重写父类方法，添加原生SQL语句到查询末尾。
     *
     * @param lastSql 要添加的原生SQL语句。
     * @return 返回当前对象，以支持链式调用。
     */
    @Override
    public LambdaQueryWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    /**
     * 重写父类方法，添加IN条件的查询。
     *
     * @param column 列的函数式引用，指向实体类中对应的字段。
     * @param coll   要匹配的值集合。
     * @return 返回当前对象，以支持链式调用。
     */
    @Override
    public LambdaQueryWrapperX<T> in(SFunction<T, ?> column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }
}
