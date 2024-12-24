package org.nstep.engine.framework.redis.config;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis 配置类
 * <p>
 * 该类用于配置 Redis 相关的 Bean，特别是自定义的 `RedisTemplate` 和序列化方式。通过该配置，Redis 数据将以 JSON 格式进行序列化，且能够正确处理 Java 8 中的 `LocalDateTime` 类型。
 * 此配置类会在 `RedissonAutoConfigurationV2` 之前加载，以确保使用自定义的 `RedisTemplate`。
 */
@AutoConfiguration(before = RedissonAutoConfigurationV2.class) // 目的：使用自己定义的 RedisTemplate Bean
public class EngineRedisAutoConfiguration {

    /**
     * 构建 RedisSerializer 对象
     * <p>
     * 该方法用于构建一个 JSON 序列化器，特别处理 `LocalDateTime` 类型的序列化。
     * 通过 Jackson 的 `ObjectMapper` 注册 `JavaTimeModule` 模块，确保可以正确序列化和反序列化 `LocalDateTime`。
     *
     * @return 返回构建好的 RedisSerializer
     */
    public static RedisSerializer<?> buildRedisSerializer() {
        RedisSerializer<Object> json = RedisSerializer.json();
        // 解决 LocalDateTime 的序列化问题
        ObjectMapper objectMapper = (ObjectMapper) ReflectUtil.getFieldValue(json, "mapper");
        objectMapper.registerModules(new JavaTimeModule());
        return json;
    }

    /**
     * 创建 RedisTemplate Bean，使用 JSON 序列化方式
     * <p>
     * 该方法创建并配置一个 `RedisTemplate` 实例。该模板用于与 Redis 交互，配置了 JSON 序列化方式来处理 Redis 存储的数据。
     *
     * @param factory RedisConnectionFactory 对象，用于创建 Redis 连接
     * @return 返回配置好的 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置 RedisConnection 工厂。此工厂用于创建与 Redis 的连接
        template.setConnectionFactory(factory);
        // 使用 String 序列化方式，序列化 KEY
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // 使用 JSON 序列化方式，序列化 VALUE 和 HashValue
        template.setValueSerializer(buildRedisSerializer());
        template.setHashValueSerializer(buildRedisSerializer());
        return template;
    }

}
