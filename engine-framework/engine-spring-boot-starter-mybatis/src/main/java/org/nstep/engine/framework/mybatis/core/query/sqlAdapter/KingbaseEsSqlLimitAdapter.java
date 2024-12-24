package org.nstep.engine.framework.mybatis.core.query.sqlAdapter;

/**
 * KingbaseES 限制适配器
 */
public class KingbaseEsSqlLimitAdapter implements SqlLimitAdapter {
    @Override
    public String getLimitSql(int n) {
        return "LIMIT " + n;
    }
}