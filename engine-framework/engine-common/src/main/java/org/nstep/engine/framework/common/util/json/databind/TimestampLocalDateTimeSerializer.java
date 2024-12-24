package org.nstep.engine.framework.common.util.json.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 基于时间戳的 LocalDateTime 序列化器
 * 用于将 Java 的 LocalDateTime 对象序列化为 JSON 数据中的时间戳（Long 类型）。
 */
public class TimestampLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    /**
     * 单例实例，避免多次创建序列化器对象。
     */
    public static final TimestampLocalDateTimeSerializer INSTANCE = new TimestampLocalDateTimeSerializer();

    /**
     * 序列化方法，将 LocalDateTime 对象转换为 JSON 中的时间戳（毫秒级）。
     * 方法逻辑：
     * 1. 使用 `value.atZone(ZoneId.systemDefault())` 将 LocalDateTime 转换为 ZonedDateTime，
     * 以便绑定到系统默认时区。
     * 2. 调用 `toInstant()` 将 ZonedDateTime 转换为 Instant 对象。
     * 3. 调用 `toEpochMilli()` 将 Instant 转换为毫秒级时间戳。
     * 4. 使用 `gen.writeNumber` 将时间戳写入 JSON 数据中。
     * 注意：
     * - 序列化后的时间戳以毫秒为单位。
     * - 确保 LocalDateTime 不为空，否则可能会抛出 NullPointerException。
     *
     * @param value       待序列化的 LocalDateTime 对象。
     * @param gen         JSON 生成器，用于输出序列化后的 JSON 数据。
     * @param serializers 序列化提供器，用于获取序列化相关的配置和上下文。
     * @throws IOException 如果写入或序列化过程中发生 I/O 错误时抛出。
     */
    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }
}
