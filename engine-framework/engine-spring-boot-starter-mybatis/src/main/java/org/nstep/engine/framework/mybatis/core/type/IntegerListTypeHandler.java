package org.nstep.engine.framework.mybatis.core.type;

import cn.hutool.core.collection.CollUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.nstep.engine.framework.common.util.string.StrUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * List<Integer> 类型转换器实现类，用于将数据库中的 varchar 类型字段转换为 List<Integer> 类型。
 * 该类用于 MyBatis 中处理数据库中存储的以逗号分隔的整数列表。
 */
@MappedJdbcTypes(JdbcType.VARCHAR) // 指定该类型处理器对应数据库的 VARCHAR 类型
@MappedTypes(List.class) // 指定该类型处理器适用于 List 类型
public class IntegerListTypeHandler implements TypeHandler<List<Integer>> {

    // 定义分隔符，表示字符串中整数的分隔符
    private static final String COMMA = ",";

    /**
     * 将 List<Integer> 类型的参数转换为数据库可存储的字符串格式（逗号分隔的整数列表）。
     *
     * @param ps       PreparedStatement 对象
     * @param i        参数索引
     * @param strings  要存储的 List<Integer> 对象
     * @param jdbcType 参数的 JDBC 类型
     * @throws SQLException 如果设置参数时发生错误
     */
    @Override
    public void setParameter(PreparedStatement ps, int i, List<Integer> strings, JdbcType jdbcType) throws SQLException {
        // 使用逗号将 List<Integer> 转换为字符串，并设置到 PreparedStatement 中
        ps.setString(i, CollUtil.join(strings, COMMA));
    }

    /**
     * 从 ResultSet 中获取指定列的值，并将其转换为 List<Integer> 类型。
     *
     * @param rs         ResultSet 对象
     * @param columnName 列名
     * @return 转换后的 List<Integer> 类型的结果
     * @throws SQLException 如果获取结果时发生错误
     */
    @Override
    public List<Integer> getResult(ResultSet rs, String columnName) throws SQLException {
        // 获取列的值并转换为 List<Integer>
        String value = rs.getString(columnName);
        return getResult(value);
    }

    /**
     * 从 ResultSet 中获取指定列的值，并将其转换为 List<Integer> 类型。
     *
     * @param rs          ResultSet 对象
     * @param columnIndex 列的索引
     * @return 转换后的 List<Integer> 类型的结果
     * @throws SQLException 如果获取结果时发生错误
     */
    @Override
    public List<Integer> getResult(ResultSet rs, int columnIndex) throws SQLException {
        // 获取列的值并转换为 List<Integer>
        String value = rs.getString(columnIndex);
        return getResult(value);
    }

    /**
     * 从 CallableStatement 中获取指定列的值，并将其转换为 List<Integer> 类型。
     *
     * @param cs          CallableStatement 对象
     * @param columnIndex 列的索引
     * @return 转换后的 List<Integer> 类型的结果
     * @throws SQLException 如果获取结果时发生错误
     */
    @Override
    public List<Integer> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 获取列的值并转换为 List<Integer>
        String value = cs.getString(columnIndex);
        return getResult(value);
    }

    /**
     * 将逗号分隔的字符串转换为 List<Integer> 类型。
     *
     * @param value 逗号分隔的字符串
     * @return 转换后的 List<Integer> 类型的结果，如果字符串为 null，则返回 null
     */
    private List<Integer> getResult(String value) {
        if (value == null) {
            return null; // 如果值为 null，返回 null
        }
        // 使用工具类将逗号分隔的字符串转换为 List<Integer>
        return StrUtils.splitToInteger(value, COMMA);
    }
}
