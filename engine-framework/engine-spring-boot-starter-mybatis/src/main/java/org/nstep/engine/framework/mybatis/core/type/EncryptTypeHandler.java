package org.nstep.engine.framework.mybatis.core.type;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.extra.spring.SpringUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 字段的 TypeHandler 实现类，基于 {@link cn.hutool.crypto.symmetric.AES} 实现。
 * 该类用于对字段进行加密和解密操作。加密和解密使用 AES 对称加密算法。
 * 可以通过 jasypt.encryptor.password 配置项，设置密钥。
 */
public class EncryptTypeHandler extends BaseTypeHandler<String> {

    // 配置项名称，用于获取密钥
    private static final String ENCRYPTOR_PROPERTY_NAME = "mybatis-plus.encryptor.password";

    // AES 加密器实例
    private static AES aes;

    /**
     * 解密方法，将数据库中存储的加密值解密为原始值。
     *
     * @param value 加密的字符串值
     * @return 解密后的原始值，如果值为 null 则返回 null
     */
    private static String decrypt(String value) {
        if (value == null) {
            return null; // 如果传入的值为 null，直接返回 null
        }
        // 使用 AES 加密器解密值
        return getEncryptor().decryptStr(value);
    }

    /**
     * 加密方法，将原始值加密为数据库可存储的加密值。
     *
     * @param rawValue 原始的字符串值
     * @return 加密后的字符串值，如果原始值为 null 则返回 null
     */
    public static String encrypt(String rawValue) {
        if (rawValue == null) {
            return null; // 如果原始值为 null，直接返回 null
        }
        // 使用 AES 加密器对原始值进行 Base64 加密
        return getEncryptor().encryptBase64(rawValue);
    }

    /**
     * 获取 AES 加密器实例。如果实例尚未创建，则从配置文件中读取密钥并初始化 AES 加密器。
     *
     * @return AES 加密器实例
     */
    private static AES getEncryptor() {
        if (aes != null) {
            return aes; // 如果加密器已经初始化，直接返回
        }
        // 从配置文件中读取加密密钥
        String password = SpringUtil.getProperty(ENCRYPTOR_PROPERTY_NAME);
        // 确保密钥不能为空
        Assert.notEmpty(password, "配置项({}) 不能为空", ENCRYPTOR_PROPERTY_NAME);
        // 使用密钥初始化 AES 加密器
        aes = SecureUtil.aes(password.getBytes());
        return aes;
    }

    /**
     * 设置非空参数。将传入的原始值加密后设置到 PreparedStatement 中。
     *
     * @param ps        PreparedStatement 对象
     * @param i         参数索引
     * @param parameter 要加密的原始值
     * @param jdbcType  参数的 JDBC 类型
     * @throws SQLException 如果设置参数时发生错误
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        // 加密传入的参数并设置到 PreparedStatement 中
        ps.setString(i, encrypt(parameter));
    }

    /**
     * 从结果集中获取可空的字段值，并解密返回原始值。
     *
     * @param rs         结果集对象
     * @param columnName 列名
     * @return 解密后的原始值，如果字段值为 null，则返回 null
     * @throws SQLException 如果获取结果时发生错误
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 获取结果集中的加密值并解密
        String value = rs.getString(columnName);
        return decrypt(value);
    }

    /**
     * 从结果集中获取可空的字段值，并解密返回原始值。
     *
     * @param rs          结果集对象
     * @param columnIndex 列的索引
     * @return 解密后的原始值，如果字段值为 null，则返回 null
     * @throws SQLException 如果获取结果时发生错误
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 获取结果集中的加密值并解密
        String value = rs.getString(columnIndex);
        return decrypt(value);
    }

    /**
     * 从 CallableStatement 中获取可空的字段值，并解密返回原始值。
     *
     * @param cs          CallableStatement 对象
     * @param columnIndex 列的索引
     * @return 解密后的原始值，如果字段值为 null，则返回 null
     * @throws SQLException 如果获取结果时发生错误
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 获取 CallableStatement 中的加密值并解密
        String value = cs.getString(columnIndex);
        return decrypt(value);
    }
}
