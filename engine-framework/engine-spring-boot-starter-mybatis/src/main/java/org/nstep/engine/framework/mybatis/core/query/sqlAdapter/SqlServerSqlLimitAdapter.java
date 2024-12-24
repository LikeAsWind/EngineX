package org.nstep.engine.framework.mybatis.core.query.sqlAdapter;

/**
 * SQL Server 限制适配器
 */
public class SqlServerSqlLimitAdapter implements SqlLimitAdapter {
    @Override
    public String getLimitSql(int n) {
        return "TOP " + n + " *";
    }
}