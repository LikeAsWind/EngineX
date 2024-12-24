package org.nstep.engine.framework.desensitize.core.slider.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import org.nstep.engine.framework.desensitize.core.base.annotation.DesensitizeBy;
import org.nstep.engine.framework.desensitize.core.slider.handler.DefaultDesensitizationHandler;

import java.lang.annotation.*;

/**
 * 滑动脱敏注解
 * 该注解用于对字段进行滑动脱敏处理。通过指定前缀和后缀保留的长度，以及替换规则，将中间部分替换为指定字符（默认为“*”）。
 * 适用于需要保留前后部分内容的脱敏场景。
 */
@Documented  // 标记该注解将包含在 Javadoc 中
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})  // 该注解可以应用于字段和其他注解类型
@Retention(RetentionPolicy.RUNTIME)  // 注解将在运行时保留，可通过反射访问
@JacksonAnnotationsInside  // 该注解内部使用 Jackson 注解，表明该注解会影响 Jackson 序列化和反序列化
@DesensitizeBy(handler = DefaultDesensitizationHandler.class)  // 通过 DefaultDesensitizationHandler 类进行脱敏处理
public @interface SliderDesensitize {

    /**
     * 后缀保留长度
     * 用于指定需要保留的字符串后缀部分的长度，默认不保留任何字符
     *
     * @return 后缀保留长度
     */
    int suffixKeep() default 0;

    /**
     * 替换规则
     * 用于指定脱敏时替换中间部分的字符，默认替换为“*”
     * 例如：prefixKeep = 1; suffixKeep = 2; replacer = "*";
     * 对于原始字符串 "123456"，脱敏后将变为 "1***56"
     *
     * @return 替换字符
     */
    String replacer() default "*";

    /**
     * 前缀保留长度
     * 用于指定需要保留的字符串前缀部分的长度，默认不保留任何字符
     *
     * @return 前缀保留长度
     */
    int prefixKeep() default 0;

    /**
     * 是否禁用脱敏
     * 支持 Spring EL 表达式，如果返回 true，则跳过脱敏处理
     * 例如：可以通过 SpEL 表达式动态决定是否进行脱敏操作
     *
     * @return 是否禁用脱敏的 Spring EL 表达式
     */
    String disable() default "";

}
