package org.nstep.engine.framework.apilog.core.annotation;

import org.nstep.engine.framework.apilog.core.enums.OperateTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 访问日志注解
 * <p>
 * 用于标注方法是否需要记录访问日志。通过该注解，可以灵活控制是否记录请求参数、响应结果等信息，
 * 并且可以设置敏感参数以防止泄露敏感数据。注解还支持指定操作模块、操作名和操作分类，方便在日志中进行分类和标识。
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiAccessLog {

    // ========== 开关字段 ==========

    /**
     * 是否记录访问日志
     * <p>
     * 默认为 true，表示记录访问日志。如果设置为 false，则不记录该方法的访问日志。
     */
    boolean enable() default true;

    /**
     * 是否记录请求参数
     * <p>
     * 默认为 true，表示记录请求参数。请求数据一般不大，通常可以记录。如果设置为 false，则不记录请求参数。
     */
    boolean requestEnable() default true;

    /**
     * 是否记录响应结果
     * <p>
     * 默认为 false，表示不记录响应结果。响应数据可能较大，可以根据实际需求手动设置为 true 来记录响应结果。
     */
    boolean responseEnable() default false;

    /**
     * 敏感参数数组
     * <p>
     * 通过该字段，可以指定一些敏感参数，这些参数在请求参数和响应结果中将不会被记录。
     * 适用于防止泄露敏感数据，如密码、验证码等。
     */
    String[] sanitizeKeys() default {};

    // ========== 模块字段 ==========

    /**
     * 操作模块
     * <p>
     * 用于标识操作所属的模块。如果为空，将尝试从 Swagger 注解 {@link io.swagger.v3.oas.annotations.tags.Tag#name()} 中获取模块名。
     */
    String operateModule() default "";

    /**
     * 操作名
     * <p>
     * 用于标识操作的名称。如果为空，将尝试从 Swagger 注解 {@link io.swagger.v3.oas.annotations.Operation#summary()} 中获取操作名称。
     */
    String operateName() default "";

    /**
     * 操作分类
     * <p>
     * 用于标识操作的分类。实际是一个枚举数组，但由于枚举不能设置 null 作为默认值，默认为空数组。
     */
    OperateTypeEnum[] operateType() default {};
}
