package org.nstep.engine.framework.mybatis.core.query.sqlAdapter;

public interface SqlLimitAdapter {
    /**
     * 获取 SQL 限制语句
     *
     * @param n 限制的记录数
     * @return SQL 限制语句
     */
    String getLimitSql(int n);
}