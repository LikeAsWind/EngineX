package org.nstep.engine.framework.desensitize.core.slider.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import org.nstep.engine.framework.desensitize.core.base.annotation.DesensitizeBy;
import org.nstep.engine.framework.desensitize.core.slider.handler.PasswordDesensitization;

import java.lang.annotation.*;

/**
 * 自定义注解，用于密码脱敏处理。
 * 该注解可以应用于字段，支持密码的脱敏操作，默认将密码全部替换为指定字符（默认为“*”）。
 */
@Documented  // 标记该注解将包含在 Javadoc 中
@Target({ElementType.FIELD})  // 该注解只能应用于字段（Field）
@Retention(RetentionPolicy.RUNTIME)  // 注解将在运行时保留，可通过反射访问
@JacksonAnnotationsInside  // 该注解内部使用 Jackson 注解，表明该注解会影响 Jackson 序列化和反序列化
@DesensitizeBy(handler = PasswordDesensitization.class)  // 通过 PasswordDesensitization 类进行脱敏处理
public @interface PasswordDesensitize {

    /**
     * 前缀保留长度
     * 用于指定密码前面保留的字符个数，默认不保留任何字符
     *
     * @return 前缀保留长度
     */
    int prefixKeep() default 0;

    /**
     * 后缀保留长度
     * 用于指定密码后面保留的字符个数，默认不保留任何字符
     *
     * @return 后缀保留长度
     */
    int suffixKeep() default 0;

    /**
     * 替换规则
     * 用于指定脱敏时替换密码的字符，默认替换为“*”
     * 例如：密码 123456 脱敏之后为 ******，即将密码全部替换为“*”
     *
     * @return 替换字符
     */
    String replacer() default "*";

    /**
     * 是否禁用脱敏
     * 支持 Spring EL 表达式，如果返回 true，则跳过脱敏处理
     * 例如：可以通过 SpEL 表达式动态决定是否进行脱敏操作
     *
     * @return 是否禁用脱敏的 Spring EL 表达式
     */
    String disable() default "";

}
