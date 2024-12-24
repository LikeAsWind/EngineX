package org.nstep.engine.framework.desensitize.core.slider.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import org.nstep.engine.framework.desensitize.core.base.annotation.DesensitizeBy;
import org.nstep.engine.framework.desensitize.core.slider.handler.CarLicenseDesensitization;

import java.lang.annotation.*;

/**
 * 车牌号脱敏注解
 * <p>
 * 该注解用于对车牌号进行脱敏处理，保留前缀和后缀的部分，其余部分使用指定的替换规则进行脱敏。
 * </p>
 */
@Documented
@Target({ElementType.FIELD})  // 适用于字段
@Retention(RetentionPolicy.RUNTIME)  // 在运行时保留
@JacksonAnnotationsInside  // 使其成为 Jackson 序列化的一部分
@DesensitizeBy(handler = CarLicenseDesensitization.class)  // 使用 CarLicenseDesensitization 作为脱敏处理器
public @interface CarLicenseDesensitize {

    /**
     * 前缀保留长度
     * <p>
     * 指定车牌号前缀保留的长度，默认保留前 3 位。
     * </p>
     */
    int prefixKeep() default 3;

    /**
     * 后缀保留长度
     * <p>
     * 指定车牌号后缀保留的长度，默认保留后 1 位。
     * </p>
     */
    int suffixKeep() default 1;

    /**
     * 替换规则，车牌号
     * <p>
     * 指定脱敏时替换的字符，默认使用 "*" 进行替换。
     * 例如：粤A66666 脱敏之后为 粤A6***6
     * </p>
     */
    String replacer() default "*";

    /**
     * 是否禁用脱敏
     * <p>
     * 支持 Spring EL 表达式，如果返回 true 则跳过脱敏。
     * </p>
     */
    String disable() default "";
}
