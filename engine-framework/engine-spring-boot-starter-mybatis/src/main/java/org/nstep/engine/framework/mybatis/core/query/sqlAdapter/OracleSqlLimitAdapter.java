package org.nstep.engine.framework.mybatis.core.query.sqlAdapter;

/**
 * Oracle 限制适配器
 */
public class OracleSqlLimitAdapter implements SqlLimitAdapter {
    @Override
    public String getLimitSql(int n) {
        return "ROWNUM <= " + n;
    }
}