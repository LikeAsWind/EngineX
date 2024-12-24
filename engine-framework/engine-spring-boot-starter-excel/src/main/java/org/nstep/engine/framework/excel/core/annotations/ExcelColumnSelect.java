package org.nstep.engine.framework.excel.core.annotations;

import java.lang.annotation.*;

/**
 * 给 Excel 列添加下拉选择数据的注解
 * <p>
 * 用于标注在字段上，为 Excel 的列提供下拉选择功能。
 * 开发者可以通过指定字典类型或方法名称，动态生成下拉选项数据。
 * <p>
 * 注意：{@link #dictType()} 和 {@link #functionName()} 是互斥的，二选一即可。
 */
@Target({ElementType.FIELD}) // 注解只能应用于字段上
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可用，支持通过反射获取
@Inherited // 注解可以被子类继承
public @interface ExcelColumnSelect {

    /**
     * 指定下拉选项数据的字典类型。
     * <p>
     * 通过字典类型标识，从字典数据中获取对应的下拉选项。
     * <p>
     * 默认值为空字符串，表示未使用字典类型。
     *
     * @return 字典类型标识
     */
    String dictType() default "";

    /**
     * 指定获取下拉选项数据的方法名称。
     * <p>
     * 方法需要在当前上下文中可用，返回值应为下拉选项数据的集合。
     * <p>
     * 默认值为空字符串，表示未使用方法获取数据。
     *
     * @return 获取下拉选项数据的方法名称
     */
    String functionName() default "";

}
