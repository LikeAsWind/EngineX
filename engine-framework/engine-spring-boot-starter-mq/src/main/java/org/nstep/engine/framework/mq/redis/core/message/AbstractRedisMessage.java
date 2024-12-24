package org.nstep.engine.framework.mq.redis.core.message;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis 消息抽象基类
 * <p>
 * 该类作为 Redis 消息的基类，提供了对消息头（headers）的管理功能。
 * 子类可以继承该类，扩展更多的消息内容和功能。
 */
@Data
public abstract class AbstractRedisMessage {

    /**
     * 消息头（headers）
     * <p>
     * 用于存储消息的元数据（如消息的 ID、类型、时间戳等）。
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * 获取指定的消息头
     *
     * @param key 消息头的键
     * @return 消息头的值
     */
    public String getHeader(String key) {
        return headers.get(key); // 返回指定键的消息头值
    }

    /**
     * 添加或更新消息头
     *
     * @param key   消息头的键
     * @param value 消息头的值
     */
    public void addHeader(String key, String value) {
        headers.put(key, value); // 将指定的键值对添加到消息头中
    }

}
