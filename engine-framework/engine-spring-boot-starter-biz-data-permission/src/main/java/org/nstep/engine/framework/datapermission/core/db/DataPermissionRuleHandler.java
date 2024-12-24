package org.nstep.engine.framework.datapermission.core.db;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Table;
import org.nstep.engine.framework.datapermission.core.rule.DataPermissionRule;
import org.nstep.engine.framework.datapermission.core.rule.DataPermissionRuleFactory;
import org.nstep.engine.framework.mybatis.core.util.MyBatisUtils;

import java.util.List;

/**
 * 基于 {@link DataPermissionRule} 的数据权限处理器
 * <p>
 * 它的底层，是基于 MyBatis Plus 的 <a href="https://baomidou.com/plugins/data-permission/">数据权限插件</a>
 * 核心原理：它会在 SQL 执行前拦截 SQL 语句，并根据用户权限动态添加权限相关的 SQL 片段。这样，只有用户有权限访问的数据才会被查询出来
 */
@RequiredArgsConstructor // Lombok注解，用于标记构造函数所需的参数，自动生成包含所有final属性的构造函数。
public class DataPermissionRuleHandler implements MultiDataPermissionHandler {

    // 注入的数据权限规则工厂，用于获取数据权限规则。
    private final DataPermissionRuleFactory ruleFactory;

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        // 获得 Mapper 对应的数据权限的规则
        List<DataPermissionRule> rules = ruleFactory.getDataPermissionRule(mappedStatementId);
        if (CollUtil.isEmpty(rules)) {
            return null; // 如果没有规则，则返回null。
        }

        // 生成条件
        Expression allExpression = null;
        for (DataPermissionRule rule : rules) {
            // 判断表名是否匹配
            String tableName = MyBatisUtils.getTableName(table);
            if (!rule.getTableNames().contains(tableName)) {
                continue; // 如果规则不适用于当前表，则跳过。
            }

            // 单条规则的条件
            Expression oneExpress = rule.getExpression(tableName, table.getAlias());
            if (oneExpress == null) {
                continue; // 如果规则没有生成有效的表达式，则跳过。
            }
            // 拼接到 allExpression 中
            allExpression = allExpression == null ? oneExpress
                    : new AndExpression(allExpression, oneExpress); // 使用AndExpression将多个表达式连接起来。
        }
        return allExpression; // 返回生成的表达式，如果没有表达式则返回null。
    }

}
