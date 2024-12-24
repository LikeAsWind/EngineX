package org.nstep.engine.framework.excel.core.annotations;

import java.lang.annotation.*;

/**
 * 字典格式化注解
 * <p>
 * 用于标注在字段上，实现将字段的字典值格式化为对应的字典标签。
 * 通过此注解，可以在序列化或显示时，将字段值映射为更具可读性的字典标签。
 * 适用于需要使用字典翻译的场景，例如状态码、分类等字段。
 */
@Target({ElementType.FIELD}) // 表示此注解只能用于字段上
@Retention(RetentionPolicy.RUNTIME) // 表示此注解在运行时可用，通过反射可以获取到
@Inherited // 表示此注解可以被子类继承
public @interface DictFormat {

    /**
     * 指定字典类型的标识。
     * <p>
     * 例如，可以使用常量类中的字典类型常量，如 SysDictTypeConstants 或 InfDictTypeConstants。
     * <p>
     * 在运行时，根据此字典类型，通过工具类或服务，将字段的字典值映射为对应的字典标签。
     *
     * @return 字典类型的标识字符串
     */
    String value();

}
