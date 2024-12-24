package org.nstep.engine.framework.mybatis.core.query.sqlAdapter;

/**
 * PostgreSQL 限制适配器
 */
public class PostgreSqlSqlLimitAdapter implements SqlLimitAdapter {
    @Override
    public String getLimitSql(int n) {
        return "LIMIT " + n;
    }
}