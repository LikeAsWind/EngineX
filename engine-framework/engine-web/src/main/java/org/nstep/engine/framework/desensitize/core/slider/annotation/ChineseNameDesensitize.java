package org.nstep.engine.framework.desensitize.core.slider.annotation;


import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import org.nstep.engine.framework.desensitize.core.slider.handler.ChineseNameDesensitization;
import org.nstep.engine.framework.desensitize.core.base.annotation.DesensitizeBy;

import java.lang.annotation.*;

/**
 * 中文名

 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = ChineseNameDesensitization.class)
public @interface ChineseNameDesensitize {

    /**
     * 前缀保留长度
     */
    int prefixKeep() default 1;

    /**
     * 后缀保留长度
     */
    int suffixKeep() default 0;

    /**
     * 替换规则，中文名;比如：刘子豪脱敏之后为刘**
     */
    String replacer() default "*";

    /**
     * 是否禁用脱敏
     * <p>
     * 支持 Spring EL 表达式，如果返回 true 则跳过脱敏
     */
    String disable() default "";

}
