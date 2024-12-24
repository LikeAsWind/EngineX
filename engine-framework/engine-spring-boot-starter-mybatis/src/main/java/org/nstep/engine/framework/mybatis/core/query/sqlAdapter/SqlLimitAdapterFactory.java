package org.nstep.engine.framework.mybatis.core.query.sqlAdapter;

import com.baomidou.mybatisplus.annotation.DbType;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL 限制适配器工厂
 */
public class SqlLimitAdapterFactory {

    // 使用 Map 存储数据库类型和适配器的映射关系
    private static final Map<DbType, SqlLimitAdapter> adapterMap = new HashMap<>();

    static {
        // 初始化数据库类型和适配器的映射
        adapterMap.put(DbType.ORACLE, new OracleSqlLimitAdapter());
        adapterMap.put(DbType.ORACLE_12C, new OracleSqlLimitAdapter());
        adapterMap.put(DbType.SQL_SERVER, new SqlServerSqlLimitAdapter());
        adapterMap.put(DbType.SQL_SERVER2005, new SqlServerSqlLimitAdapter());
        adapterMap.put(DbType.MYSQL, new MySqlSqlLimitAdapter());
        adapterMap.put(DbType.POSTGRE_SQL, new PostgreSqlSqlLimitAdapter());
        adapterMap.put(DbType.DM, new DmSqlLimitAdapter());
        adapterMap.put(DbType.KINGBASE_ES, new KingbaseEsSqlLimitAdapter());
        adapterMap.put(DbType.OPENGAUSS, new OpenGaussSqlLimitAdapter());
    }

    /**
     * 根据数据库类型获取相应的 SQL 限制适配器
     *
     * @param dbType 数据库类型
     * @return 相应的 SQL 限制适配器
     */
    public static SqlLimitAdapter getAdapter(DbType dbType) {
        // 使用 Map 获取适配器，避免 switch 语句
        SqlLimitAdapter adapter = adapterMap.get(dbType);
        if (adapter == null) {
            throw new IllegalArgumentException("Unsupported DbType: " + dbType);
        }
        return adapter;
    }
}
