package org.nstep.engine.framework.desensitize.core.base.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.nstep.engine.framework.desensitize.core.base.handler.DesensitizationHandler;
import org.nstep.engine.framework.desensitize.core.base.serializer.StringDesensitizeSerializer;

import java.lang.annotation.*;

/**
 * 顶级脱敏注解，自定义注解需要使用此注解
 * <p>
 * 该注解是用于标记自定义脱敏注解的顶级注解。自定义的脱敏注解需要使用此注解来进行标记，
 * 并通过指定一个脱敏处理器类来定义具体的脱敏逻辑。
 * 该注解会通过 `StringDesensitizeSerializer` 序列化器来进行脱敏操作。
 * <p>
 * 注解的使用场景：通常用于字段或返回值上，标记需要进行脱敏的字段，结合自定义脱敏处理器进行脱敏。
 */
@Documented // 表示该注解将包含在 Javadoc 中
@Target(ElementType.ANNOTATION_TYPE) // 该注解只能用于注解类型
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时保留
@JacksonAnnotationsInside // 此注解是其他所有 Jackson 注解的元注解，表示它是 Jackson 注解的一部分
@JsonSerialize(using = StringDesensitizeSerializer.class) // 指定使用的序列化器
public @interface DesensitizeBy {

    /**
     * 脱敏处理器
     * <p>
     * 用于指定一个脱敏处理器类，该类实现了 `DesensitizationHandler` 接口，
     * 负责定义具体的脱敏逻辑。可以通过此处理器对数据进行不同方式的脱敏。
     *
     * @return 脱敏处理器的类
     */
    @SuppressWarnings("rawtypes")
    Class<? extends DesensitizationHandler> handler();
}
