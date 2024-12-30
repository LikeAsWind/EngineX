package org.nstep.engine.module.message.util;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;


/**
 * 一个工具类，提供用于操作 {@link java.util.concurrent.ConcurrentHashMap} 的辅助方法。
 * 该类包含一个针对 Java 8 中 {@code ConcurrentHashMap#computeIfAbsent} 方法性能问题的解决方案。
 */
public class ConcurrentHashMapUtils {

    /**
     * 标志位，用于判断当前 Java 运行时版本是否为 Java 8。
     */
    private static boolean IS_JAVA8;

    // 静态代码块，用于初始化 IS_JAVA8 标志位
    static {
        try {
            // 检查 Java 版本是否以 "1.8." 开头（表示 Java 8）
            IS_JAVA8 = System.getProperty("java.version").startsWith("1.8.");
        } catch (Exception ignore) {
            // 如果发生异常，默认认为是 Java 8
            IS_JAVA8 = true;
        }
    }

    /**
     * 提供一个针对 Java 8 中 {@code ConcurrentHashMap#computeIfAbsent} 方法性能问题的解决方案。
     * <p>
     * 问题详情：<a href="https://bugs.openjdk.java.net/browse/JDK-8161372">JDK-8161372</a>
     * </p>
     *
     * @param map  并发映射表
     * @param key  要计算其关联值的键
     * @param func 用于计算值的映射函数
     * @param <K>  映射表中维护的键的类型
     * @param <V>  映射值的类型
     * @return 与指定键关联的当前值（存在则返回现有值，否则返回计算后的值）
     * @throws NullPointerException 如果指定的映射表或映射函数为 null
     */
    public static <K, V> V computeIfAbsent(ConcurrentMap<K, V> map, K key, Function<? super K, ? extends V> func) {
        // 检查运行环境是否为 Java 8
        if (IS_JAVA8) {
            // 尝试获取与键关联的值
            V v = map.get(key);
            if (null == v) {
                // 如果值不存在，则计算并存储该值
                v = map.computeIfAbsent(key, func);
            }
            return v;
        } else {
            // 对于非 Java 8 环境，直接使用 computeIfAbsent 方法
            return map.computeIfAbsent(key, func);
        }
    }
}
