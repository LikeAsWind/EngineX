package org.nstep.engine.framework.datapermission.core.annotation;

// 导入所需的类

import org.nstep.engine.framework.datapermission.core.rule.DataPermissionRule;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 可声明在类或者方法上，标识使用的数据权限规则
 * 指定注解可以用于类（TYPE）或方法（METHOD）
 * 指定注解在运行时有效
 * 指定注解会被javadoc工具记录
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    /**
     * 当前类或方法是否开启数据权限
     * 即使不添加 @DataPermission 注解，默认是开启状态
     * 可通过设置 enable 为 false 禁用
     * 定义一个名为enable的属性，默认值为true，表示数据权限默认是开启的
     */
    boolean enable() default true;

    /**
     * 生效的数据权限规则数组，优先级高于 {@link #excludeRules()}
     * 定义一个名为includeRules的属性，用于指定包含的数据权限规则类数组，默认为空数组
     */
    Class<? extends DataPermissionRule>[] includeRules() default {};

    /**
     * 排除的数据权限规则数组，优先级最低
     * 定义一个名为excludeRules的属性，用于指定排除的数据权限规则类数组，默认为空数组
     */
    Class<? extends DataPermissionRule>[] excludeRules() default {};

}