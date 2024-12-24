package org.nstep.engine.framework.mybatis.core.query.sqlAdapter;

/**
 * MySQL 限制适配器
 */
public class MySqlSqlLimitAdapter implements SqlLimitAdapter {
    @Override
    public String getLimitSql(int n) {
        return "LIMIT " + n;
    }
}