package org.nstep.engine.gateway.jackson;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Jackson 自动配置类，用于配置自定义的 JSON 序列化与反序列化规则
 * <p>
 * 本配置类用于初始化 Jackson 的序列化与反序列化规则，特别是针对日期、时间、Long 类型的处理，
 * 解决 JSON 序列化中可能出现的精度丢失问题，并将自定义配置应用到所有的 ObjectMapper 实例中。
 * <p>
 * 通过创建一个 SimpleModule 并将其注册到 ObjectMapper 中，定制了 Long 类型、LocalDate、LocalTime、LocalDateTime 等类型的处理方式。
 */
@Configuration
@Slf4j // 使用 Lombok 提供的日志功能
public class JacksonAutoConfiguration {

    /**
     * 配置自定义的 JSON 序列化与反序列化规则，并将其应用到所有 ObjectMapper 实例
     * <p>
     * 1. 创建一个 SimpleModule 对象，注册自定义的序列化和反序列化规则。
     * 2. 将这些规则注册到 Spring Boot 提供的所有 ObjectMapper 实例中。
     * 3. 初始化 JsonUtils 并设置默认的 ObjectMapper。
     *
     * @param objectMappers 所有注册到 Spring 上下文中的 ObjectMapper 实例列表
     * @return JsonUtils 自定义的 JSON 工具类实例
     */
    @Bean
    public JsonUtils jsonUtils(List<ObjectMapper> objectMappers) {
        // 1.1 创建 SimpleModule 对象，定义自定义的序列化和反序列化规则
        SimpleModule simpleModule = new SimpleModule();
        simpleModule
                // 处理 Long 类型的序列化，避免超过 2^53-1 的数值在 JS 中丢失精度
                .addSerializer(Long.class, NumberSerializer.INSTANCE)
                .addSerializer(Long.TYPE, NumberSerializer.INSTANCE)
                // 处理 LocalDate 类型的序列化和反序列化
                .addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE)
                .addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE)
                // 处理 LocalTime 类型的序列化和反序列化
                .addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE)
                .addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE)
                // 处理 LocalDateTime 类型的序列化和反序列化，使用 Long 时间戳
                .addSerializer(LocalDateTime.class, TimestampLocalDateTimeSerializer.INSTANCE)
                .addDeserializer(LocalDateTime.class, TimestampLocalDateTimeDeserializer.INSTANCE);

        // 1.2 将 SimpleModule 注册到每个 ObjectMapper 中
        objectMappers.forEach(objectMapper -> objectMapper.registerModule(simpleModule));

        // 2. 初始化 JsonUtils 并设置默认的 ObjectMapper
        JsonUtils.init(CollUtil.getFirst(objectMappers));
        log.info("[init][初始化 JsonUtils 成功]");

        // 返回 JsonUtils 实例
        return new JsonUtils();
    }

}
