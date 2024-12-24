package org.nstep.engine.framework.mybatis.core.query.sqlAdapter;

/**
 * DM 限制适配器
 */
public class DmSqlLimitAdapter implements SqlLimitAdapter {
    @Override
    public String getLimitSql(int n) {
        return "LIMIT " + n;
    }
}