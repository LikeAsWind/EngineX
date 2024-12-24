package org.nstep.engine.framework.mybatis.core.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 针对 MyBatis Plus 的 {@link DbType} 增强，补充更多信息
 * <p>
 * 该枚举类扩展了 MyBatis Plus 中的 {@link DbType}，为不同的数据库类型提供了更详细的信息，
 * 包括数据库产品名和对应的 `FIND_IN_SET` SQL 模板。该模板用于判断某个值是否在某个列中出现。
 * <p>
 * 通过此枚举，能够方便地获取不同数据库类型的特定 SQL 语法。
 */
@Getter
@AllArgsConstructor
public enum DbTypeEnum {

    /**
     * H2 数据库
     * <p>
     * 注意：H2 不支持 find_in_set 函数。
     */
    H2(DbType.H2, "H2", ""),

    /**
     * MySQL 数据库
     * <p>
     * MySQL 使用 `FIND_IN_SET` 函数来检查某个值是否在一个字符串列表中。
     */
    MY_SQL(DbType.MYSQL, "MySQL", "FIND_IN_SET('#{value}', #{column}) <> 0"),

    /**
     * Oracle 数据库
     * <p>
     * Oracle 使用 `FIND_IN_SET` 函数来检查某个值是否在一个字符串列表中。
     */
    ORACLE(DbType.ORACLE, "Oracle", "FIND_IN_SET('#{value}', #{column}) <> 0"),

    /**
     * PostgreSQL 数据库
     * <p>
     * 华为 openGauss 使用与 PostgreSQL 相同的产品名。
     * PostgreSQL 使用 `POSITION` 函数来检查某个值是否在一个字符串列中。
     */
    POSTGRE_SQL(DbType.POSTGRE_SQL, "PostgreSQL", "POSITION('#{value}' IN #{column}) <> 0"),

    /**
     * SQL Server 数据库
     * <p>
     * SQL Server 使用 `CHARINDEX` 函数来检查某个值是否在一个字符串列中。
     */
    SQL_SERVER(DbType.SQL_SERVER, "Microsoft SQL Server", "CHARINDEX(',' + #{value} + ',', ',' + #{column} + ',') <> 0"),

    /**
     * SQL Server 2005 数据库
     * <p>
     * SQL Server 2005 使用 `CHARINDEX` 函数来检查某个值是否在一个字符串列中。
     */
    SQL_SERVER2005(DbType.SQL_SERVER2005, "Microsoft SQL Server 2005", "CHARINDEX(',' + #{value} + ',', ',' + #{column} + ',') <> 0"),

    /**
     * 达梦数据库
     * <p>
     * 达梦数据库使用 `FIND_IN_SET` 函数来检查某个值是否在一个字符串列表中。
     */
    DM(DbType.DM, "DM DBMS", "FIND_IN_SET('#{value}', #{column}) <> 0"),

    /**
     * 人大金仓数据库
     * <p>
     * 人大金仓使用 `POSITION` 函数来检查某个值是否在一个字符串列中。
     */
    KINGBASE_ES(DbType.KINGBASE_ES, "KingbaseES", "POSITION('#{value}' IN #{column}) <> 0"),
    ;

    /**
     * 用于通过数据库产品名查找对应的 DbTypeEnum 实例
     */
    public static final Map<String, DbTypeEnum> MAP_BY_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(DbTypeEnum::getProductName, Function.identity()));

    /**
     * 用于通过 MyBatis Plus 的 DbType 查找对应的 DbTypeEnum 实例
     */
    public static final Map<DbType, DbTypeEnum> MAP_BY_MP = Arrays.stream(values())
            .collect(Collectors.toMap(DbTypeEnum::getMpDbType, Function.identity()));

    /**
     * MyBatis Plus 类型
     * <p>
     * 对应 MyBatis Plus 中的 {@link DbType} 枚举，表示数据库类型。
     */
    private final DbType mpDbType;

    /**
     * 数据库产品名
     * <p>
     * 数据库的产品名，例如 MySQL、Oracle、PostgreSQL 等。
     */
    private final String productName;

    /**
     * SQL FIND_IN_SET 模板
     * <p>
     * 针对不同的数据库类型，提供相应的 `FIND_IN_SET` 函数模板。
     * 该模板用于 SQL 查询中判断某个值是否在某个列中出现。
     */
    private final String findInSetTemplate;

    /**
     * 根据数据库产品名查找对应的 DbType
     * <p>
     * 根据给定的数据库产品名，返回对应的 MyBatis Plus DbType 枚举。
     * 如果找不到对应的数据库类型，则返回 null。
     *
     * @param databaseProductName 数据库产品名
     * @return 对应的 DbType
     */
    public static DbType find(String databaseProductName) {
        if (StrUtil.isBlank(databaseProductName)) {
            return null;
        }
        return MAP_BY_NAME.get(databaseProductName).getMpDbType();
    }

    /**
     * 获取对应数据库类型的 `FIND_IN_SET` SQL 模板
     * <p>
     * 根据数据库类型返回相应的 `FIND_IN_SET` SQL 模板，用于在查询中判断某个值是否存在于指定列中。
     * 如果数据库不支持该操作，则抛出异常。
     *
     * @param dbType 数据库类型
     * @return 对应的 `FIND_IN_SET` 模板
     * @throws IllegalArgumentException 如果该数据库类型不支持 `FIND_IN_SET` 操作
     */
    public static String getFindInSetTemplate(DbType dbType) {
        return Optional.of(MAP_BY_MP.get(dbType).getFindInSetTemplate())
                .orElseThrow(() -> new IllegalArgumentException("FIND_IN_SET not supported"));
    }
}
