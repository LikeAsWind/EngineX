package org.nstep.engine.framework.common.util.json.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import java.io.IOException;

/**
 * Long 序列化规则
 * <p>
 * 会将超长 long 值转换为 string，解决前端 JavaScript 最大安全整数是 2^53-1 的问题
 */
@JacksonStdImpl
public class NumberSerializer extends com.fasterxml.jackson.databind.ser.std.NumberSerializer {

    public static final NumberSerializer INSTANCE = new NumberSerializer(Number.class);
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    public NumberSerializer(Class<? extends Number> rawType) {
        super(rawType);
    }

    /**
     * 序列化方法，用于将 Number 类型的值序列化为 JSON 格式。
     * <p>
     * 方法逻辑：
     * 1. 检查 `value` 是否在 JavaScript 的安全整数范围内：
     * - 安全整数范围为 [-9007199254740991, 9007199254740991]。
     * - 这个范围是由 JavaScript 的 Number 类型的最大安全整数定义的，即 2^53-1。
     * 2. 如果 `value` 在安全范围内：
     * - 调用父类的 `serialize` 方法，按照默认规则序列化为 JSON 数值。
     * 3. 如果 `value` 超出安全范围：
     * - 将 `value` 转换为字符串并输出，避免前端因整数精度丢失而出现错误。
     * <p>
     * 注意：
     * - 此方法主要用于解决前端 JavaScript 在处理超大整数时的精度问题。
     * - 如果未超出范围，仍然按照标准数值序列化规则处理。
     *
     * @param value       待序列化的 Number 值。
     * @param gen         JSON 生成器，用于输出序列化后的 JSON 数据。
     * @param serializers 序列化提供器，用于获取序列化相关的配置和上下文。
     * @throws IOException 如果发生 I/O 错误时抛出。
     */
    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value.longValue() > MIN_SAFE_INTEGER && value.longValue() < MAX_SAFE_INTEGER) {
            super.serialize(value, gen, serializers);
        } else {
            gen.writeString(value.toString());
        }
    }

}
