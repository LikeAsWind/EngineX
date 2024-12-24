package org.nstep.engine.framework.mybatis.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.util.collection.SetUtils;
import org.nstep.engine.framework.mybatis.core.util.JdbcUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Set;

/**
 * 用于根据数据库类型自动设置 MyBatis Plus 的 ID 类型。
 * <p>
 * 当 IdType 为 {@link IdType#NONE} 时，根据主数据源的数据库类型来自动设置 ID 类型。
 * 支持多种数据库类型，如 Oracle、PostgreSQL、MySQL 等，自动选择合适的主键策略。
 */
@Slf4j
public class IdTypeEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String ID_TYPE_KEY = "mybatis-plus.global-config.db-config.id-type"; // MyBatis Plus 配置 ID 类型的键
    private static final String DATASOURCE_DYNAMIC_KEY = "spring.datasource.dynamic"; // 动态数据源配置的键
    private static final String QUARTZ_JOB_STORE_DRIVER_KEY = "spring.quartz.properties.org.quartz.jobStore.driverDelegateClass"; // Quartz JobStore 驱动类配置键

    // 支持使用输入 ID 类型的数据库类型集合（例如 Oracle、PostgreSQL 等）
    private static final Set<DbType> INPUT_ID_TYPES = SetUtils.asSet(DbType.ORACLE, DbType.ORACLE_12C,
            DbType.POSTGRE_SQL, DbType.KINGBASE_ES, DbType.DB2, DbType.H2);

    /**
     * 获取当前主数据源的数据库类型。
     *
     * @param environment Spring 环境配置
     * @return DbType 数据库类型
     */
    public static DbType getDbType(ConfigurableEnvironment environment) {
        // 获取主数据源的名称
        String primary = environment.getProperty(DATASOURCE_DYNAMIC_KEY + "." + "primary");
        if (StrUtil.isEmpty(primary)) {
            return null;
        }
        // 获取主数据源的 URL
        String url = environment.getProperty(DATASOURCE_DYNAMIC_KEY + ".datasource." + primary + ".url");
        if (StrUtil.isEmpty(url)) {
            return null;
        }
        // 根据 URL 获取数据库类型
        return JdbcUtils.getDbType(url);
    }

    /**
     * 在 Spring 环境初始化后处理环境配置。
     * <p>
     * 该方法会根据数据库类型自动设置 MyBatis Plus 的 ID 类型，确保 ID 类型与数据库兼容。
     *
     * @param environment Spring 环境配置
     * @param application Spring 应用
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 获取当前数据库类型
        DbType dbType = getDbType(environment);
        if (dbType == null) {
            return; // 如果无法获取数据库类型，则不做任何处理
        }

        // 设置 Quartz JobStore 对应的 Driver
        // 暂时没有找到特别合适的地方，先放在这里
        setJobStoreDriverIfPresent(environment, dbType);

        // 获取当前的 ID 类型配置
        IdType idType = getIdType(environment);
        if (idType != IdType.NONE) {
            return; // 如果 ID 类型已经设置为非 NONE，则不做任何修改
        }

        // 根据数据库类型设置 ID 类型
        // 情况一：适合使用输入 ID（例如 Oracle、PostgreSQL 等）
        if (INPUT_ID_TYPES.contains(dbType)) {
            setIdType(environment, IdType.INPUT); // 设置为 INPUT 类型
            return;
        }

        // 情况二：适合使用自增 ID（例如 MySQL、DM 达梦等）
        setIdType(environment, IdType.AUTO); // 设置为 AUTO 类型
    }

    /**
     * 获取当前的 ID 类型配置。
     *
     * @param environment Spring 环境配置
     * @return IdType 当前的 ID 类型
     */
    public IdType getIdType(ConfigurableEnvironment environment) {
        return environment.getProperty(ID_TYPE_KEY, IdType.class);
    }

    /**
     * 设置 MyBatis Plus 的 ID 类型。
     * <p>
     * 将 ID 类型设置为给定的值，并记录日志。
     *
     * @param environment Spring 环境配置
     * @param idType      要设置的 ID 类型
     */
    public void setIdType(ConfigurableEnvironment environment, IdType idType) {
        environment.getSystemProperties().put(ID_TYPE_KEY, idType);
        log.info("[setIdType][修改 MyBatis Plus 的 idType 为({})]", idType);
    }

    /**
     * 如果 Quartz 的 JobStore 配置中没有设置 driverClass，则根据数据库类型自动设置。
     *
     * @param environment Spring 环境配置
     * @param dbType      数据库类型
     */
    public void setJobStoreDriverIfPresent(ConfigurableEnvironment environment, DbType dbType) {
        // 获取 Quartz JobStore 驱动类的配置
        String driverClass = environment.getProperty(QUARTZ_JOB_STORE_DRIVER_KEY);
        if (StrUtil.isNotEmpty(driverClass)) {
            return; // 如果已经配置了 driverClass，则不做修改
        }

        // 根据数据库类型选择对应的 driverClass
        driverClass = switch (dbType) {
            case POSTGRE_SQL -> "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate";
            case ORACLE, ORACLE_12C -> "org.quartz.impl.jdbcjobstore.oracle.OracleDelegate";
            case SQL_SERVER, SQL_SERVER2005 -> "org.quartz.impl.jdbcjobstore.MSSQLDelegate";
            case DM, KINGBASE_ES -> "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
            default -> driverClass;
        };

        // 如果有对应的 driverClass，则设置到环境配置中
        if (StrUtil.isNotEmpty(driverClass)) {
            environment.getSystemProperties().put(QUARTZ_JOB_STORE_DRIVER_KEY, driverClass);
        }
    }
}
