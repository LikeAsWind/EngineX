package org.nstep.engine.framework.desensitize.core.slider.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import org.nstep.engine.framework.desensitize.core.base.annotation.DesensitizeBy;
import org.nstep.engine.framework.desensitize.core.slider.handler.MobileDesensitization;

import java.lang.annotation.*;

/**
 * 自定义注解，用于手机号脱敏处理。
 * 该注解可以应用于字段，支持手机号的脱敏操作，保留前后部分数字，替换中间部分为指定字符（默认为“*”）。
 */
@Documented  // 标记该注解将包含在 Javadoc 中
@Target({ElementType.FIELD})  // 该注解只能应用于字段（Field）
@Retention(RetentionPolicy.RUNTIME)  // 注解将在运行时保留，可通过反射访问
@JacksonAnnotationsInside  // 该注解内部使用 Jackson 注解，表明该注解会影响 Jackson 序列化和反序列化
@DesensitizeBy(handler = MobileDesensitization.class)  // 通过 MobileDesensitization 类进行脱敏处理
public @interface MobileDesensitize {

    /**
     * 前缀保留长度
     * 用于指定手机号前面保留的数字个数，默认保留3个数字
     *
     * @return 前缀保留长度
     */
    int prefixKeep() default 3;

    /**
     * 后缀保留长度
     * 用于指定手机号后面保留的数字个数，默认保留4个数字
     *
     * @return 后缀保留长度
     */
    int suffixKeep() default 4;

    /**
     * 替换规则
     * 用于指定脱敏时替换中间部分的字符，默认替换为“*”
     * 例如：手机号 13248765917 脱敏之后为 132****5917
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
