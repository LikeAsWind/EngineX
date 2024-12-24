package org.nstep.engine.framework.common.util.collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.nstep.engine.framework.common.core.KeyValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Map 工具类
 */
public class MapUtils {

    /**
     * 从哈希表表中，获得 keys 对应的所有 value 数组
     *
     * @param multimap 哈希表
     * @param keys     keys
     * @return value 数组
     */
    public static <K, V> List<V> getList(Multimap<K, V> multimap, Collection<K> keys) {
        // 创建一个新的 ArrayList 来存储结果
        List<V> result = new ArrayList<>();
        // 遍历 keys 集合中的每个键
        keys.forEach(k -> {
            // 从 multimap 中获取键 k 对应的所有值
            Collection<V> values = multimap.get(k);
            // 如果 values 为空，则不进行任何操作
            if (CollectionUtil.isEmpty(values)) {
                return;
            }
            // 将 values 中的所有值添加到 result 列表中
            result.addAll(values);
        });
        // 返回包含所有值的列表
        return result;
    }


    /**
     * 从哈希表查找到 key 对应的 value，然后进一步处理
     * key 为 null 时, 不处理
     * 注意，如果查找到的 value 为 null 时，不进行处理
     *
     * @param map      哈希表
     * @param key      key
     * @param consumer 进一步处理的逻辑
     */
    public static <K, V> void findAndThen(Map<K, V> map, K key, Consumer<V> consumer) {
        // 如果键为空或映射为空，则立即返回
        if (ObjUtil.isNull(key) || CollUtil.isEmpty(map)) {
            return;
        }
        // 从映射中获取键对应的值
        V value = map.get(key);
        // 如果值为空，则立即返回
        if (value == null) {
            return;
        }
        // 对获取到的值进行进一步处理
        consumer.accept(value);
    }

    /**
     * 将一个 KeyValue 对象列表转换为一个 Map 对象
     *
     * @param keyValues 包含键值对的列表
     * @return 转换后的 Map 对象
     */
    public static <K, V> Map<K, V> convertMap(List<KeyValue<K, V>> keyValues) {
        // 创建一个新的 LinkedHashMap，其初始容量为 keyValues 列表的大小
        Map<K, V> map = Maps.newLinkedHashMapWithExpectedSize(keyValues.size());
        // 遍历 keyValues 列表，将每个 KeyValue 对象的键和值添加到 map 中
        keyValues.forEach(keyValue -> map.put(keyValue.getKey(), keyValue.getValue()));
        // 返回转换后的 map
        return map;
    }


}
