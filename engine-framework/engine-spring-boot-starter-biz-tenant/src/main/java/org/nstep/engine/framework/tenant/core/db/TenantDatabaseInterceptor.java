package org.nstep.engine.framework.tenant.core.db;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.nstep.engine.framework.tenant.config.TenantProperties;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * 基于 MyBatis Plus 多租户的功能，实现 DB 层面的多租户的功能
 * <p>
 * 该类实现了 MyBatis Plus 的 TenantLineHandler 接口，用于处理多租户功能。
 * 它主要通过拦截 SQL 查询，在 SQL 中自动注入租户 ID，并且可以忽略指定的表。
 */
public class TenantDatabaseInterceptor implements TenantLineHandler {

    /**
     * 存储需要忽略多租户的表名
     */
    private final Set<String> ignoreTables = new HashSet<>();

    /**
     * 构造函数，初始化需要忽略多租户的表
     * <p>
     * 通过 TenantProperties 获取配置，初始化需要忽略的表名。
     * 由于不同数据库的大小写习惯不同，所以需要同时添加大小写版本的表名。
     * 另外，"DUAL" 表是 Oracle 数据库中常见的一个特殊表，需要额外处理。
     *
     * @param properties 租户相关的配置
     */
    public TenantDatabaseInterceptor(TenantProperties properties) {
        // 不同 DB 下，大小写的习惯不同，所以需要都添加进去
        properties.getIgnoreTables().forEach(table -> {
            ignoreTables.add(table.toLowerCase()); // 添加小写版本的表名
            ignoreTables.add(table.toUpperCase()); // 添加大写版本的表名
        });
        // 在 OracleKeyGenerator 中，生成主键时，会查询这个表，查询这个表后，会自动拼接 TENANT_ID 导致报错
        ignoreTables.add("DUAL"); // Oracle 特殊表
    }

    /**
     * 获取当前租户 ID
     * <p>
     * 该方法会返回当前租户的 ID，MyBatis Plus 会将该 ID 自动注入到 SQL 中。
     *
     * @return 当前租户的 ID
     */
    @Override
    public Expression getTenantId() {
        // 从 TenantContextHolder 获取当前租户 ID
        return new LongValue(TenantContextHolder.getRequiredTenantId());
    }

    /**
     * 判断是否忽略该表的多租户处理
     * <p>
     * 如果当前是全局忽略多租户，或者该表在配置中被标记为忽略表，则返回 true。
     *
     * @param tableName 表名
     * @return 是否忽略该表的多租户处理
     */
    @Override
    public boolean ignoreTable(String tableName) {
        // 情况一：全局忽略多租户
        // 情况二：忽略多租户的表
        return TenantContextHolder.isIgnore() // 判断是否全局忽略
                || CollUtil.contains(ignoreTables, tableName); // 判断是否为需要忽略的表
    }

}
