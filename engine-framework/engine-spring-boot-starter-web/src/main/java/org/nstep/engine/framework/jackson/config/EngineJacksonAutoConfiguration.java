package org.nstep.engine.framework.jackson.config;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.framework.common.util.json.databind.NumberSerializer;
import org.nstep.engine.framework.common.util.json.databind.TimestampLocalDateTimeDeserializer;
import org.nstep.engine.framework.common.util.json.databind.TimestampLocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * EngineJacksonAutoConfiguration 是一个自动配置类，用于初始化和配置 Jackson 序列化和反序列化规则。
 * 该类通过自定义的 SimpleModule 配置了一些常见的类型（如 Long、LocalDate、LocalTime 和 LocalDateTime）的序列化和反序列化规则，
 * 并将这些规则注册到 Spring 容器中的所有 ObjectMapper 实例中。
 * <p>
 * 该类还会初始化一个 JsonUtils 实例，并将配置好的 ObjectMapper 注入其中，确保 JsonUtils 能够使用自定义的序列化规则。
 *
 * @see JsonUtils
 * @see ObjectMapper
 * @see SimpleModule
 */
@AutoConfiguration
@Slf4j
public class EngineJacksonAutoConfiguration {

    /**
     * 创建并初始化一个 {@link JsonUtils} 实例，配置自定义的 Jackson 序列化和反序列化规则。
     *
     * @param objectMappers 所有可用的 {@link ObjectMapper} 实例列表
     * @return 初始化后的 {@link JsonUtils} 实例
     */
    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public JsonUtils jsonUtils(List<ObjectMapper> objectMappers) {
        // 1.1 创建 SimpleModule 对象
        SimpleModule simpleModule = new SimpleModule();

        // 1.2 配置序列化和反序列化规则
        // 为 Long 类型添加自定义序列化规则，防止在 JavaScript 中出现精度丢失
        simpleModule
                .addSerializer(Long.class, NumberSerializer.INSTANCE)
                .addSerializer(Long.TYPE, NumberSerializer.INSTANCE)
                // 为 LocalDate 类型添加序列化和反序列化规则
                .addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE)
                .addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE)
                // 为 LocalTime 类型添加序列化和反序列化规则
                .addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE)
                .addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE)
                // 为 LocalDateTime 类型添加序列化和反序列化规则，使用 Long 时间戳
                .addSerializer(LocalDateTime.class, TimestampLocalDateTimeSerializer.INSTANCE)
                .addDeserializer(LocalDateTime.class, TimestampLocalDateTimeDeserializer.INSTANCE);

        // 1.3 将配置的模块注册到所有的 ObjectMapper 实例中
        objectMappers.forEach(objectMapper -> objectMapper.registerModule(simpleModule));

        // 2. 设置 ObjectMapper 到 JsonUtils
        // 使用第一个 ObjectMapper 来初始化 JsonUtils
        JsonUtils.init(CollUtil.getFirst(objectMappers));

        // 3. 打印日志，表示初始化成功
        log.info("[init][初始化 JsonUtils 成功]");

        // 4. 返回 JsonUtils 实例
        return new JsonUtils();
    }

}

