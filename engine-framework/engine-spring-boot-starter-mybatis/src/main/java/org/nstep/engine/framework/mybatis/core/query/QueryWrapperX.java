package org.nstep.engine.framework.mybatis.core.query;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.nstep.engine.framework.mybatis.core.query.sqlAdapter.SqlLimitAdapter;
import org.nstep.engine.framework.mybatis.core.query.sqlAdapter.SqlLimitAdapterFactory;
import org.nstep.engine.framework.mybatis.core.util.JdbcUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 拓展 MyBatis Plus QueryWrapper 类，主要增加如下功能：
 * <p>
 * 1. 拼接条件的方法，增加 xxxIfPresent 方法，用于判断值不存在的时候，不要拼接到条件中。
 *
 * @param <T> 数据类型
 */
public class QueryWrapperX<T> extends QueryWrapper<T> {

    /**
     * 拼接 like 条件，只有在 val 不为空时才拼接。
     *
     * @param column 要查询的列
     * @param val    要匹配的值
     * @return 当前对象
     */
    public QueryWrapperX<T> likeIfPresent(String column, String val) {
        if (StringUtils.hasText(val)) {
            return (QueryWrapperX<T>) super.like(column, val);
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
    public QueryWrapperX<T> inIfPresent(String column, Collection<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            return (QueryWrapperX<T>) super.in(column, values);
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
    public QueryWrapperX<T> inIfPresent(String column, Object... values) {
        if (!ArrayUtils.isEmpty(values)) {
            return (QueryWrapperX<T>) super.in(column, values);
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
    public QueryWrapperX<T> eqIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.eq(column, val);
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
    public QueryWrapperX<T> neIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.ne(column, val);
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
    public QueryWrapperX<T> gtIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.gt(column, val);
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
    public QueryWrapperX<T> geIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.ge(column, val);
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
    public QueryWrapperX<T> ltIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.lt(column, val);
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
    public QueryWrapperX<T> leIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.le(column, val);
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
    public QueryWrapperX<T> betweenIfPresent(String column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (QueryWrapperX<T>) super.between(column, val1, val2);
        }
        if (val1 != null) {
            return (QueryWrapperX<T>) ge(column, val1);
        }
        if (val2 != null) {
            return (QueryWrapperX<T>) le(column, val2);
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
    public QueryWrapperX<T> betweenIfPresent(String column, Object[] values) {
        if (values != null && values.length != 0 && values[0] != null && values[1] != null) {
            return (QueryWrapperX<T>) super.between(column, values[0], values[1]);
        }
        if (values != null && values.length != 0 && values[0] != null) {
            return (QueryWrapperX<T>) ge(column, values[0]);
        }
        if (values != null && values.length != 0 && values[1] != null) {
            return (QueryWrapperX<T>) le(column, values[1]);
        }
        return this;
    }

    // ========== 重写父类方法，方便链式调用 ==========

    /**
     * 条件性地添加等于（=）条件。
     * 如果condition为true，则添加条件column = val。
     *
     * @param condition 条件是否成立的标志。
     * @param column    要比较的列名。
     * @param val       列的值。
     * @return 返回当前QueryWrapperX对象，用于链式调用。
     */
    @Override
    public QueryWrapperX<T> eq(boolean condition, String column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    /**
     * 添加等于（=）条件。
     * 总是添加条件column = val。
     *
     * @param column 要比较的列名。
     * @param val    列的值。
     * @return 返回当前QueryWrapperX对象，用于链式调用。
     */
    @Override
    public QueryWrapperX<T> eq(String column, Object val) {
        super.eq(column, val);
        return this;
    }


    /**
     * 添加降序排序条件。
     *
     * @param column 要排序的列名。
     * @return 返回当前QueryWrapperX对象，用于链式调用。
     */
    @Override
    public QueryWrapperX<T> orderByDesc(String column) {
        super.orderByDesc(true, column);
        return this;
    }

    /**
     * 添加原生SQL语句到查询末尾。
     *
     * @param lastSql 要添加的原生SQL语句。
     * @return 返回当前QueryWrapperX对象，用于链式调用。
     */
    @Override
    public QueryWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    /**
     * 添加IN条件。
     *
     * @param column 要比较的列名。
     * @param coll   包含可能值的集合。
     * @return 返回当前QueryWrapperX对象，用于链式调用。
     */
    @Override
    public QueryWrapperX<T> in(String column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }

    /**
     * 设置只返回最后一条
     * <p>
     * 如果使用多数据源，并且数据源是多种类型时，可能会存在问题：实现之返回一条的语法不同
     *
     * @return 当前对象
     */
    public QueryWrapperX<T> limitN(int n) {
        DbType dbType = JdbcUtils.getDbType();  // 假设 JdbcUtils.getDbType() 返回当前数据库类型
        SqlLimitAdapter adapter = SqlLimitAdapterFactory.getAdapter(dbType);
        String limitSql = adapter.getLimitSql(n);  // 获取相应的 SQL 限制语句
        super.last(limitSql);  // 使用适配器返回的 SQL
        return this;
    }

}
