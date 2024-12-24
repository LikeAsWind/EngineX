package org.nstep.engine.framework.common.util.json.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 基于时间戳的 LocalDateTime 反序列化器
 * <p>
 * 用于将 JSON 数据中的时间戳（Long 类型）反序列化为 Java 的 LocalDateTime 对象。
 */
public class TimestampLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    /**
     * 单例实例，避免多次创建反序列化器对象。
     */
    public static final TimestampLocalDateTimeDeserializer INSTANCE = new TimestampLocalDateTimeDeserializer();

    /**
     * 反序列化方法，将 JSON 中的时间戳（毫秒级）转换为 LocalDateTime 对象。
     * <p>
     * 方法逻辑：
     * 1. 调用 `p.getValueAsLong()` 获取 JSON 中的时间戳值，时间戳以毫秒为单位。
     * 2. 使用 `Instant.ofEpochMilli` 将时间戳转换为 `Instant` 对象。
     * 3. 调用 `LocalDateTime.ofInstant` 将 `Instant` 转换为 `LocalDateTime`，
     * 并使用系统默认的时区（`ZoneId.systemDefault()`）。
     * 4. 返回生成的 LocalDateTime 对象。
     * <p>
     * 注意：
     * - 时间戳应为毫秒级别的 Long 值。
     * - 如果 JSON 中的时间戳值无效或不为 Long 类型，可能会抛出异常。
     *
     * @param p    JSON 解析器，用于读取 JSON 数据。
     * @param text 反序列化上下文，提供额外的配置和处理信息。
     * @return LocalDateTime 表示反序列化后的时间对象。
     * @throws IOException 如果读取或解析过程中发生 I/O 错误时抛出。
     */
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext text) throws IOException {

        return LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getValueAsLong()), ZoneId.systemDefault());
    }
}
