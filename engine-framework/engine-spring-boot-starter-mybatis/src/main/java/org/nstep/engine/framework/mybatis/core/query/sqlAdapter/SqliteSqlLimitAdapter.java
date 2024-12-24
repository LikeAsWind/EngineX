package org.nstep.engine.framework.mybatis.core.query.sqlAdapter;

public class SqliteSqlLimitAdapter implements SqlLimitAdapter {
    @Override
    public String getLimitSql(int n) {
        return "LIMIT " + n;
    }
}