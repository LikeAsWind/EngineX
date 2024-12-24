package org.nstep.engine.framework.common.util.json;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON 工具类
 * <p>
 * 该工具类封装了常用的 JSON 序列化与反序列化方法，使用 Jackson 的 ObjectMapper 实现。
 * 提供了将对象转为 JSON 字符串、从 JSON 字符串解析为对象等功能。
 * 支持处理各种类型的 JSON 数据，并提供了对空值、未知属性的处理配置。
 * <p>
 * 该类还支持对 LocalDateTime 类型的序列化和反序列化，解决了日期时间处理问题。
 */
@Slf4j
public class JsonUtils {

    /**
     * ObjectMapper 实例，用于 JSON 的序列化和反序列化操作。
     */
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 配置 ObjectMapper，忽略空的 JavaBean、忽略未知属性、忽略 null 值
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 忽略 null 值
        objectMapper.registerModules(new JavaTimeModule()); // 解决 LocalDateTime 的序列化
    }

    /**
     * 初始化 ObjectMapper 属性，允许外部通过 Spring 创建的 ObjectMapper Bean 进行配置
     *
     * @param objectMapper ObjectMapper 对象
     */
    public static void init(ObjectMapper objectMapper) {
        JsonUtils.objectMapper = objectMapper;
    }

    /**
     * 将对象转换为 JSON 字符串
     *
     * @param object 需要序列化的对象
     * @return JSON 字符串
     */
    @SneakyThrows
    public static String toJsonString(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * 将对象转换为 JSON 字节数组
     *
     * @param object 需要序列化的对象
     * @return JSON 字节数组
     */
    @SneakyThrows
    public static byte[] toJsonByte(Object object) {
        return objectMapper.writeValueAsBytes(object);
    }

    /**
     * 将对象转换为格式化的 JSON 字符串（包含缩进）
     *
     * @param object 需要序列化的对象
     * @return 格式化的 JSON 字符串
     */
    @SneakyThrows
    public static String toJsonPrettyString(Object object) {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象
     *
     * @param text  JSON 字符串
     * @param clazz 目标类型
     * @return 解析后的对象
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        try {
            return objectMapper.readValue(text, clazz);
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 从 JSON 字符串的指定路径解析出对象
     *
     * @param text  JSON 字符串
     * @param path  JSON 中的路径
     * @param clazz 目标类型
     * @return 解析后的对象
     */
    public static <T> T parseObject(String text, String path, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        try {
            JsonNode treeNode = objectMapper.readTree(text);
            JsonNode pathNode = treeNode.path(path);
            return objectMapper.readValue(pathNode.toString(), clazz);
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象，支持泛型类型
     *
     * @param text JSON 字符串
     * @param type 目标类型
     * @return 解析后的对象
     */
    public static <T> T parseObject(String text, Type type) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructType(type));
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象，支持 @JsonTypeInfo 注解的场景
     *
     * @param text  JSON 字符串
     * @param clazz 目标类型
     * @return 解析后的对象
     */
    public static <T> T parseObject2(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        return JSONUtil.toBean(text, clazz);
    }

    /**
     * 将 JSON 字节数组解析为指定类型的对象
     *
     * @param bytes JSON 字节数组
     * @param clazz 目标类型
     * @return 解析后的对象
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (ArrayUtil.isEmpty(bytes)) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            log.error("json parse err,json:{}", bytes, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象，支持 TypeReference 泛型类型
     *
     * @param text          JSON 字符串
     * @param typeReference 类型引用
     * @return 解析后的对象
     */
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(text, typeReference);
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析 JSON 字符串为指定类型的对象，如果解析失败，则返回 null
     *
     * @param text          JSON 字符串
     * @param typeReference 类型引用
     * @return 解析后的对象，失败时返回 null
     */
    public static <T> T parseObjectQuietly(String text, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(text, typeReference);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将 JSON 字符串解析为对象列表
     *
     * @param text  JSON 字符串
     * @param clazz 目标类型
     * @return 解析后的对象列表
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 从 JSON 字符串的指定路径解析出对象列表
     *
     * @param text  JSON 字符串
     * @param path  JSON 中的路径
     * @param clazz 目标类型
     * @return 解析后的对象列表
     */
    public static <T> List<T> parseArray(String text, String path, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        try {
            JsonNode treeNode = objectMapper.readTree(text);
            JsonNode pathNode = treeNode.path(path);
            return objectMapper.readValue(pathNode.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为 JsonNode 对象
     *
     * @param text JSON 字符串
     * @return JsonNode 对象
     */
    public static JsonNode parseTree(String text) {
        try {
            return objectMapper.readTree(text);
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字节数组解析为 JsonNode 对象
     *
     * @param text JSON 字节数组
     * @return JsonNode 对象
     */
    public static JsonNode parseTree(byte[] text) {
        try {
            return objectMapper.readTree(text);
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断给定的字符串是否为有效的 JSON 格式
     *
     * @param text 字符串
     * @return 是否为有效的 JSON
     */
    public static boolean isJson(String text) {
        return JSONUtil.isTypeJSON(text);
    }
}
