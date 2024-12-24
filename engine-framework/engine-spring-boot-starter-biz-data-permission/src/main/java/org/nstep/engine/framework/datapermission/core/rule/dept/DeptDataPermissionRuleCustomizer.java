package org.nstep.engine.framework.datapermission.core.rule.dept;

/**
 * 函数式接口，用于自定义部门数据权限规则。
 * {@link DeptDataPermissionRule} 的自定义配置接口
 */
@FunctionalInterface
public interface DeptDataPermissionRuleCustomizer {

    /**
     * 自定义部门数据权限规则。
     * 实现该方法可以配置基于部门ID（dept_id）和用户ID（user_id）的过滤规则。
     * <p>
     * 1. 调用 {@link DeptDataPermissionRule#addDeptColumn(Class, String)} 方法，配置基于 dept_id 的过滤规则
     * 2. 调用 {@link DeptDataPermissionRule#addUserColumn(Class, String)} 方法，配置基于 user_id 的过滤规则
     * <p>
     *
     * @param rule 要被自定义的权限规则对象。
     */
    void customize(DeptDataPermissionRule rule);

}
