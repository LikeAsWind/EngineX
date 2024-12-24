package org.nstep.engine.framework.desensitize.core.regex.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import org.nstep.engine.framework.desensitize.core.base.annotation.DesensitizeBy;
import org.nstep.engine.framework.desensitize.core.regex.handler.EmailDesensitizationHandler;

import java.lang.annotation.*;

/**
 * 邮箱脱敏注解
 * <p>
 * 用于对邮箱地址字段进行脱敏处理。通过此注解，可以指定邮箱地址的脱敏规则，
 * 例如将邮箱的中间部分替换为星号，以保护用户的隐私。
 * </p>
 */
@Documented
@Target({ElementType.FIELD})  // 该注解仅适用于字段
@Retention(RetentionPolicy.RUNTIME)  // 注解在运行时可用
@JacksonAnnotationsInside  // 表示这是一个 Jackson 注解，用于序列化时使用
@DesensitizeBy(handler = EmailDesensitizationHandler.class)  // 使用指定的脱敏处理器
public @interface EmailDesensitize {

    /**
     * 匹配的正则表达式
     * <p>
     * 用于匹配邮箱地址的正则表达式。默认的正则表达式是：(^.)[^@]*(@.*$)，
     * 它可以匹配邮箱地址中的用户名和域名部分。
     * </p>
     */
    String regex() default "(^.)[^@]*(@.*$)";

    /**
     * 替换规则，邮箱脱敏后的格式
     * <p>
     * 指定如何替换邮箱地址中的敏感部分。默认替换规则是将邮箱用户名部分替换为星号，
     * 比如：example@gmail.com 脱敏后为 e****@gmail.com。
     * </p>
     */
    String replacer() default "$1****$2";

    /**
     * 是否禁用脱敏
     * <p>
     * 支持 Spring EL 表达式。如果返回 true，则跳过脱敏操作。
     * 例如，可以在某些情况下禁用脱敏操作，例如调试模式或者特定条件下。
     * </p>
     */
    String disable() default "";

}
