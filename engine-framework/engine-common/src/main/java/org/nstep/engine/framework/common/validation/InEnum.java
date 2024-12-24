package org.nstep.engine.framework.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.nstep.engine.framework.common.core.IntArrayValuable;

import java.lang.annotation.*;

/**
 * 自定义注解，用于验证字段值是否在指定的枚举范围内。
 * 该注解通过实现 {@link IntArrayValuable} 接口的枚举类来指定有效值范围。
 */
@Target({
        ElementType.METHOD,       // 适用于方法
        ElementType.FIELD,       // 适用于字段
        ElementType.ANNOTATION_TYPE,  // 适用于注解类型
        ElementType.CONSTRUCTOR, // 适用于构造函数
        ElementType.PARAMETER,   // 适用于方法参数
        ElementType.TYPE_USE     // 适用于类型使用
})
@Retention(RetentionPolicy.RUNTIME)  // 运行时可用
@Documented  // 生成文档时包含注解
@Constraint(validatedBy = {InEnumValidator.class, InEnumCollectionValidator.class})  // 指定验证器
public @interface InEnum {

    /**
     * @return 实现 {@link IntArrayValuable} 接口的枚举类，用于指定有效值范围。
     */
    Class<? extends IntArrayValuable> value();

    /**
     * @return 默认的错误消息，表示字段值必须在指定的范围内。
     */
    String message() default "必须在指定范围 {value}";

    /**
     * @return 分组信息，支持分组校验。
     */
    Class<?>[] groups() default {};

    /**
     * @return 负载信息，允许携带额外的元数据。
     */
    Class<? extends Payload>[] payload() default {};
}
