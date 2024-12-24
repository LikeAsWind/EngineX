package org.nstep.engine.framework.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 自定义手机号验证注解，用于校验手机号格式是否正确。
 */
@Target({
        ElementType.METHOD,        // 可以用于方法
        ElementType.FIELD,        // 可以用于字段
        ElementType.ANNOTATION_TYPE, // 可以用于注解类型
        ElementType.CONSTRUCTOR,  // 可以用于构造函数
        ElementType.PARAMETER,    // 可以用于方法参数
        ElementType.TYPE_USE      // 可以用于类型使用
})
@Retention(RetentionPolicy.RUNTIME)  // 注解在运行时保留
@Documented  // 该注解会被包含在 JavaDoc 中
@Constraint(
        validatedBy = MobileValidator.class  // 指定验证器类，用于校验手机号格式
)
public @interface Mobile {

    /**
     * 校验失败时的默认错误信息
     *
     * @return 错误信息
     */
    String message() default "手机号格式不正确";

    /**
     * 用于分组校验
     *
     * @return 分组
     */
    Class<?>[] groups() default {};

    /**
     * 用于携带额外的负载信息
     *
     * @return 负载信息
     */
    Class<? extends Payload>[] payload() default {};

}
