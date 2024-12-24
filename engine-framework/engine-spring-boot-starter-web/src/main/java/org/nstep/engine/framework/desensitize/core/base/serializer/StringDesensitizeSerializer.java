package org.nstep.engine.framework.desensitize.core.base.serializer;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import lombok.Setter;
import org.nstep.engine.framework.desensitize.core.base.annotation.DesensitizeBy;
import org.nstep.engine.framework.desensitize.core.base.handler.DesensitizationHandler;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 脱敏序列化器
 * <p>
 * 该类实现了 JSON 返回数据时，使用 {@link DesensitizationHandler} 对声明脱敏注解的字段进行脱敏处理。
 * 它通过实现 Jackson 的 `StdSerializer` 类，定制了字符串类型字段的序列化过程。
 * </p>
 */
@SuppressWarnings("rawtypes")
public class StringDesensitizeSerializer extends StdSerializer<String> implements ContextualSerializer {

    @Getter
    @Setter
    private DesensitizationHandler desensitizationHandler;

    // 默认构造函数，初始化为处理 String 类型
    protected StringDesensitizeSerializer() {
        super(String.class);
    }

    /**
     * 创建上下文序列化器
     * <p>
     * 当 Jackson 序列化一个字段时，会调用此方法来创建一个上下文序列化器。
     * 如果字段有 {@link DesensitizeBy} 注解，则创建一个新的 `StringDesensitizeSerializer` 实例，并
     * 设置其脱敏处理器。
     * </p>
     *
     * @param serializerProvider 序列化提供者
     * @param beanProperty       当前字段的 Bean 属性
     * @return 序列化器
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) {
        DesensitizeBy annotation = beanProperty.getAnnotation(DesensitizeBy.class);
        if (annotation == null) {
            return this;
        }
        // 创建一个 StringDesensitizeSerializer 对象，使用 DesensitizeBy 对应的处理器
        StringDesensitizeSerializer serializer = new StringDesensitizeSerializer();
        serializer.setDesensitizationHandler(Singleton.get(annotation.handler()));
        return serializer;
    }

    /**
     * 序列化方法
     * <p>
     * 该方法用于序列化字符串字段。如果该字段被标注了脱敏注解 {@link DesensitizeBy}，则使用指定的
     * 脱敏处理器对字段值进行脱敏。
     * </p>
     *
     * @param value              要序列化的值
     * @param gen                JSON 生成器
     * @param serializerProvider 序列化提供者
     * @throws IOException 序列化过程中可能抛出的异常
     */
    @Override
    @SuppressWarnings("unchecked")
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (StrUtil.isBlank(value)) {
            gen.writeNull();  // 如果值为空，写入 null
            return;
        }

        // 获取当前字段的 Field 对象
        Field field = getField(gen);

        // 获取字段的所有脱敏注解
        DesensitizeBy[] annotations = AnnotationUtil.getCombinationAnnotations(field, DesensitizeBy.class);
        if (ArrayUtil.isEmpty(annotations)) {
            gen.writeString(value);  // 如果没有脱敏注解，直接写入原始值
            return;
        }

        // 遍历字段上的所有注解，检查是否有脱敏注解
        for (Annotation annotation : field.getAnnotations()) {
            if (AnnotationUtil.hasAnnotation(annotation.annotationType(), DesensitizeBy.class)) {
                // 使用脱敏处理器处理字段值
                value = this.desensitizationHandler.desensitize(value, annotation);
                gen.writeString(value);  // 写入脱敏后的值
                return;
            }
        }

        // 如果没有找到脱敏注解，直接写入原始值
        gen.writeString(value);
    }

    /**
     * 获取字段对象
     * <p>
     * 通过 `JsonGenerator` 获取当前正在序列化的字段的 `Field` 对象。
     * </p>
     *
     * @param generator JSON 生成器
     * @return 当前字段的 Field 对象
     */
    private Field getField(JsonGenerator generator) {
        String currentName = generator.getOutputContext().getCurrentName();  // 获取当前字段名
        Object currentValue = generator.getCurrentValue();  // 获取当前字段值
        Class<?> currentValueClass = currentValue.getClass();  // 获取字段的类类型
        return ReflectUtil.getField(currentValueClass, currentName);  // 获取字段的 Field 对象
    }

}
