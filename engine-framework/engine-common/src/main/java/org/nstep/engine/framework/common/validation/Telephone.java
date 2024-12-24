package org.nstep.engine.framework.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 自定义电话格式验证注解
 */
@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = TelephoneValidator.class // 使用自定义的验证器
)
public @interface Telephone {

    /**
     * 默认错误消息
     *
     * @return 错误消息
     */
    String message() default "电话格式不正确";

    /**
     * 用于分组校验
     *
     * @return 分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载信息
     *
     * @return 负载
     */
    Class<? extends Payload>[] payload() default {};
}
