package org.nstep.engine.framework.mybatis.core.util;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import org.nstep.engine.framework.common.util.object.ObjectUtils;
import org.nstep.engine.framework.common.util.spring.SpringUtils;
import org.nstep.engine.framework.mybatis.core.enums.DbTypeEnum;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC 工具类
 * 提供了一些数据库连接的检查和数据库类型判断的方法。
 */
public class JdbcUtils {

    /**
     * 判断连接是否正确
     * <p>
     * 通过传入的 URL、用户名和密码，尝试建立数据库连接，并判断连接是否成功。
     *
     * @param url      数据源连接 URL
     * @param username 数据库账号
     * @param password 数据库密码
     * @return 是否正确，如果连接成功返回 true，失败返回 false
     */
    public static boolean isConnectionOK(String url, String username, String password) {
        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            // 如果能成功建立连接，返回 true
            return true;
        } catch (Exception ex) {
            // 如果连接失败，捕获异常并返回 false
            return false;
        }
    }

    /**
     * 获得 URL 对应的 DB 类型
     * <p>
     * 通过传入数据库连接 URL，判断并返回对应的数据库类型。
     *
     * @param url 数据源连接 URL
     * @return DB 类型，返回数据库的类型（如 MySQL, Oracle 等）
     */
    public static DbType getDbType(String url) {
        // 使用 MyBatis-Plus 提供的工具类获取数据库类型
        return com.baomidou.mybatisplus.extension.toolkit.JdbcUtils.getDbType(url);
    }

    /**
     * 通过当前数据库连接获得对应的 DB 类型
     * <p>
     * 通过 Spring 上下文获取当前的数据库连接，进而获取数据库类型。
     *
     * @return DB 类型，返回当前连接的数据库类型（如 MySQL, Oracle 等）
     */
    public static DbType getDbType() {
        DataSource dataSource;
        try {
            // 从 Spring 上下文中获取 DynamicRoutingDataSource（动态数据源）
            DynamicRoutingDataSource dynamicRoutingDataSource = SpringUtils.getBean(DynamicRoutingDataSource.class);
            dataSource = dynamicRoutingDataSource.determineDataSource(); // 获取当前数据源
        } catch (NoSuchBeanDefinitionException e) {
            // 如果没有找到 DynamicRoutingDataSource，则直接从 Spring 上下文中获取 DataSource
            dataSource = SpringUtils.getBean(DataSource.class);
        }
        try (Connection conn = dataSource.getConnection()) {
            // 获取连接并通过元数据获取数据库产品名称，找到对应的数据库类型
            return DbTypeEnum.find(conn.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            // 如果获取数据库类型时发生异常，抛出非法参数异常
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * 判断 JDBC 连接是否为 SQLServer 数据库
     * <p>
     * 根据传入的 JDBC 连接 URL 判断该连接是否指向 SQLServer 数据库。
     *
     * @param url JDBC 连接 URL
     * @return 是否为 SQLServer 数据库，如果是 SQLServer 返回 true，否则返回 false
     */
    public static boolean isSQLServer(String url) {
        // 获取 URL 对应的数据库类型
        DbType dbType = getDbType(url);
        // 判断是否为 SQLServer 类型
        return isSQLServer(dbType);
    }

    /**
     * 判断 JDBC 连接是否为 SQLServer 数据库
     * <p>
     * 根据传入的数据库类型判断是否为 SQLServer 数据库。
     *
     * @param dbType DB 类型
     * @return 是否为 SQLServer 数据库，如果是 SQLServer 返回 true，否则返回 false
     */
    public static boolean isSQLServer(DbType dbType) {
        // 判断数据库类型是否为 SQLServer 或 SQLServer2005
        return ObjectUtils.equalsAny(dbType, DbType.SQL_SERVER, DbType.SQL_SERVER2005);
    }

}
