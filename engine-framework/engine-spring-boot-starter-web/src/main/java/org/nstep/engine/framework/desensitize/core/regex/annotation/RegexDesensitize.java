package org.nstep.engine.framework.desensitize.core.regex.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import org.nstep.engine.framework.desensitize.core.base.annotation.DesensitizeBy;
import org.nstep.engine.framework.desensitize.core.regex.handler.DefaultRegexDesensitizationHandler;

import java.lang.annotation.*;

/**
 * 正则脱敏注解
 * <p>
 * 用于对字段进行正则脱敏处理。通过此注解，可以指定正则表达式来匹配字段中的敏感信息，并通过替换规则将其脱敏。
 * </p>
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})  // 该注解适用于字段和注解类型
@Retention(RetentionPolicy.RUNTIME)  // 注解在运行时可用
@JacksonAnnotationsInside  // 表示这是一个 Jackson 注解，用于序列化时使用
@DesensitizeBy(handler = DefaultRegexDesensitizationHandler.class)  // 使用指定的脱敏处理器
public @interface RegexDesensitize {

    /**
     * 匹配的正则表达式（默认匹配所有）
     * <p>
     * 用于匹配需要脱敏的字符串。默认的正则表达式是 "^[\\s\\S]*$"，它匹配所有字符。
     * 可以根据需要修改正则表达式，精确匹配特定模式的字符串。
     * </p>
     */
    String regex() default "^[\\s\\S]*$";

    /**
     * 替换规则
     * <p>
     * 用于指定如何替换匹配到的字符串。默认替换规则是 "******"，即将匹配的字符串替换为星号。
     * 例如，如果正则表达式匹配到 "123456789"，并且 replacer 设置为 "******"，则脱敏后的字符串为 "******789"。
     * </p>
     */
    String replacer() default "******";

    /**
     * 是否禁用脱敏
     * <p>
     * 支持 Spring EL 表达式。如果返回 true，则跳过脱敏操作。
     * 例如，可以在某些条件下禁用脱敏操作，通常用于调试或者特定业务需求。
     * </p>
     */
    String disable() default "";

}
