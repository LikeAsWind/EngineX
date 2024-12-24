package org.nstep.engine.framework.mybatis.core.query.sqlAdapter;

/**
 * OpenGauss 限制适配器
 */
public class OpenGaussSqlLimitAdapter implements SqlLimitAdapter {
    @Override
    public String getLimitSql(int n) {
        return "LIMIT " + n;
    }
}