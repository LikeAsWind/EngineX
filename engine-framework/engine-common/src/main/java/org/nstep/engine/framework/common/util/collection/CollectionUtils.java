package org.nstep.engine.framework.common.util.collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * Collection 工具类
 */
public class CollectionUtils {

    /**
     * 判断一个对象是否包含在一个对象数组中
     *
     * @param source  源对象
     * @param targets 目标对象数组
     * @return 如果源对象包含在目标对象数组中，则返回 true，否则返回 false
     */
    public static boolean containsAny(Object source, Object... targets) {
        // 将目标对象数组转换为列表
        return asList(targets).contains(source);
    }


    /**
     * 判断一组集合中是否有任意一个为空
     *
     * @param collections 集合数组
     * @return 如果有任意一个集合为空，则返回 true，否则返回 false
     */
    public static boolean isAnyEmpty(Collection<?>... collections) {
        // 使用 Arrays.stream 方法将集合数组转换为流
        return Arrays.stream(collections)
                // 使用 anyMatch 方法检查流中的集合是否有任意一个为空
                .anyMatch(CollectionUtil::isEmpty);
    }


    /**
     * 判断一个集合中是否有任意一个元素满足给定的谓词
     *
     * @param from      源集合
     * @param predicate 谓词
     * @return 如果有任意一个元素满足谓词，则返回 true，否则返回 false
     */
    public static <T> boolean anyMatch(Collection<T> from, Predicate<T> predicate) {
        // 使用 stream 方法将集合转换为流
        return from.stream()
                // 使用 anyMatch 方法检查流中的元素是否有任意一个满足谓词
                .anyMatch(predicate);
    }


    /**
     * 根据给定的谓词过滤一个集合，并返回过滤后的集合
     *
     * @param from      源集合
     * @param predicate 谓词
     * @return 过滤后的集合
     */
    public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 ArrayList
            return new ArrayList<>();
        }
        // 使用 stream 方法将集合转换为流
        return from.stream()
                // 使用 filter 方法根据谓词过滤流中的元素
                .filter(predicate)
                // 使用 collect 方法将过滤后的流收集到一个列表中
                .collect(Collectors.toList());
    }


    /**
     * 根据给定的键映射函数对集合进行去重，并返回去重后的集合
     *
     * @param from      源集合
     * @param keyMapper 键映射函数
     * @return 去重后的集合
     */
    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 ArrayList
            return new ArrayList<>();
        }
        // 调用另一个 distinct 方法，传入覆盖操作的默认实现 (t1, t2) -> t1
        return distinct(from, keyMapper, (t1, t2) -> t1);
    }


    /**
     * 根据给定的键映射函数和覆盖操作对集合进行去重，并返回去重后的集合
     *
     * @param from      源集合
     * @param keyMapper 键映射函数，用于从集合元素中提取键
     * @param cover     覆盖操作，用于处理重复键的元素
     * @return 去重后的集合
     */
    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 ArrayList
            return new ArrayList<>();
        }
        // 调用 convertMap 方法，将源集合转换为一个 Map，其中键是通过 keyMapper 函数从元素中提取的，值是元素本身
        // 然后，使用 cover 函数来处理重复键的元素，最后将 Map 的值收集到一个列表中并返回
        return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
    }


    /**
     * 将一个数组转换为一个列表，并对列表中的每个元素应用一个函数
     *
     * @param from 源数组
     * @param func 转换函数
     * @return 转换后的列表
     */
    public static <T, U> List<U> convertList(T[] from, Function<T, U> func) {
        // 检查源数组是否为空
        if (ArrayUtil.isEmpty(from)) {
            // 如果源数组为空，返回一个空的 ArrayList
            return new ArrayList<>();
        }
        // 调用另一个 convertList 方法，将源数组转换为一个列表，然后对列表中的每个元素应用转换函数
        return convertList(Arrays.asList(from), func);
    }


    /**
     * 将一个集合中的元素转换为另一种类型，并返回转换后的列表
     *
     * @param from 源集合
     * @param func 转换函数，用于将源集合中的元素转换为目标类型
     * @return 转换后的列表，如果源集合为空，则返回一个空的列表
     */
    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 ArrayList
            return new ArrayList<>();
        }
        // 使用 stream 方法将集合转换为流
        return from.stream()
                // 使用 map 方法将流中的每个元素应用转换函数
                .map(func)
                // 使用 filter 方法过滤掉转换结果中的空值
                .filter(Objects::nonNull)
                // 使用 collect 方法将过滤后的流收集到一个列表中
                .collect(Collectors.toList());
    }


    /**
     * 将一个集合中的元素转换为另一种类型，并返回转换后的列表
     *
     * @param from   源集合
     * @param func   转换函数，用于将源集合中的元素转换为目标类型
     * @param filter 过滤函数，用于筛选出需要转换的元素
     * @return 转换后的列表，如果源集合为空，则返回一个空的列表
     */
    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 ArrayList
            return new ArrayList<>();
        }
        // 使用 stream 方法将集合转换为流
        return from.stream()
                // 使用 filter 方法过滤流中的元素，只保留满足过滤条件的元素
                .filter(filter)
                // 使用 map 方法将流中的每个元素应用转换函数
                .map(func)
                // 使用 filter 方法过滤掉转换结果中的空值
                .filter(Objects::nonNull)
                // 使用 collect 方法将过滤后的流收集到一个列表中
                .collect(Collectors.toList());
    }


    /**
     * 使用 flatMap 操作将一个集合中的元素转换为另一种类型的列表
     *
     * @param from 源集合
     * @param func 转换函数，用于将源集合中的每个元素转换为一个流
     * @return 转换后的列表，如果源集合为空，则返回一个空的列表
     */
    public static <T, U> List<U> convertListByFlatMap(Collection<T> from, Function<T, ? extends Stream<? extends U>> func) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 ArrayList
            return new ArrayList<>();
        }
        // 使用 stream 方法将集合转换为流
        return from.stream()
                // 使用 filter 方法过滤流中的元素，只保留非空元素
                .filter(Objects::nonNull)
                // 使用 flatMap 方法将每个元素转换为一个流，并将这些流合并成一个流
                .flatMap(func)
                // 使用 filter 方法过滤合并后的流中的元素，只保留非空元素
                .filter(Objects::nonNull)
                // 使用 collect 方法将过滤后的流收集到一个列表中
                .collect(Collectors.toList());
    }


    /**
     * 使用 flatMap 操作将一个集合中的元素转换为另一种类型的列表
     *
     * @param from   源集合
     * @param mapper 映射函数，用于将源集合中的每个元素转换为另一种类型
     * @param func   转换函数，用于将映射后的元素转换为一个流
     * @return 转换后的列表，如果源集合为空，则返回一个空的列表
     */
    public static <T, U, R> List<R> convertListByFlatMap(Collection<T> from, Function<? super T, ? extends U> mapper, Function<U, ? extends Stream<? extends R>> func) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 ArrayList
            return new ArrayList<>();
        }
        // 使用 stream 方法将集合转换为流
        return from.stream()
                // 使用 map 方法将流中的每个元素应用映射函数
                .map(mapper)
                // 使用 filter 方法过滤掉转换结果中的空值
                .filter(Objects::nonNull)
                // 使用 flatMap 方法将每个元素转换为一个流，并将这些流合并成一个流
                .flatMap(func)
                // 使用 filter 方法过滤合并后的流中的元素，只保留非空元素
                .filter(Objects::nonNull)
                // 使用 collect 方法将过滤后的流收集到一个列表中
                .collect(Collectors.toList());
    }


    /**
     * 从一个映射中合并所有的值到一个列表中
     *
     * @param map 包含值列表的映射
     * @return 合并后的列表，如果映射为空，则返回一个空列表
     */
    public static <K, V> List<V> mergeValuesFromMap(Map<K, List<V>> map) {
        // 检查映射是否为空
        if (map == null || map.isEmpty()) {
            // 如果映射为空，返回一个空的列表
            return new ArrayList<>();
        }
        // 使用 values() 方法获取映射中所有的值列表
        return map.values()
                // 使用 stream() 方法将值列表转换为流
                .stream()
                // 使用 flatMap() 方法将每个值列表流展开并合并成一个流
                .flatMap(List::stream)
                // 使用 collect() 方法将流收集到一个列表中
                .collect(Collectors.toList());
    }


    /**
     * 将一个集合转换为一个集合
     *
     * @param from 源集合
     * @return 转换后的集合，如果源集合为空，则返回一个空的集合
     */
    public static <T> Set<T> convertSet(Collection<T> from) {
        // 调用另一个 convertSet 方法，传入一个恒等函数，即直接返回每个元素本身
        return convertSet(from, v -> v);
    }


    /**
     * 将一个集合转换为一个集合
     *
     * @param from 源集合
     * @param func 转换函数，用于将源集合中的每个元素转换为另一种类型
     * @return 转换后的集合，如果源集合为空，则返回一个空的集合
     */
    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 HashSet
            return new HashSet<>();
        }
        // 使用 stream 方法将集合转换为流
        return from.stream()
                // 使用 map 方法将流中的每个元素应用转换函数
                .map(func)
                // 使用 filter 方法过滤掉转换结果中的空值
                .filter(Objects::nonNull)
                // 使用 collect 方法将过滤后的流收集到一个集合中
                .collect(Collectors.toSet());
    }


    /**
     * 将一个集合转换为一个集合
     *
     * @param from   源集合
     * @param func   转换函数，用于将源集合中的每个元素转换为另一种类型
     * @param filter 过滤函数，用于筛选出需要转换的元素
     * @return 转换后的集合，如果源集合为空，则返回一个空的集合
     */
    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 HashSet
            return new HashSet<>();
        }
        // 使用 stream 方法将集合转换为流
        return from.stream()
                // 使用 filter 方法过滤流中的元素，只保留满足过滤条件的元素
                .filter(filter)
                // 使用 map 方法将流中的每个元素应用转换函数
                .map(func)
                // 使用 filter 方法过滤掉转换结果中的空值
                .filter(Objects::nonNull)
                // 使用 collect 方法将过滤后的流收集到一个集合中
                .collect(Collectors.toSet());
    }


    /**
     * 将一个集合转换为一个集合
     *
     * @param from    源集合
     * @param filter  过滤函数，用于筛选出需要转换的元素
     * @param keyFunc 转换函数，用于将源集合中的每个元素转换为另一种类型
     * @return 转换后的集合，如果源集合为空，则返回一个空的集合
     */
    public static <T, K> Map<K, T> convertMapByFilter(Collection<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 HashMap
            return new HashMap<>();
        }
        // 使用 stream 方法将集合转换为流
        return from.stream()
                // 使用 filter 方法过滤流中的元素，只保留满足过滤条件的元素
                .filter(filter)
                // 使用 collect 方法将过滤后的流收集到一个映射中
                .collect(Collectors.toMap(keyFunc, v -> v));
    }


    /**
     * 使用 flatMap 操作将一个集合中的元素转换为另一种类型的集合
     *
     * @param from 源集合
     * @param func 转换函数，用于将源集合中的每个元素转换为一个流
     * @return 转换后的集合，如果源集合为空，则返回一个空的集合
     */
    public static <T, U> Set<U> convertSetByFlatMap(Collection<T> from, Function<T, ? extends Stream<? extends U>> func) {
        if (CollUtil.isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }


    /**
     * 使用 flatMap 操作将一个集合中的元素转换为另一种类型的集合
     *
     * @param from   源集合
     * @param mapper 映射函数，用于将源集合中的每个元素转换为另一种类型
     * @param func   转换函数，用于将映射后的元素转换为一个流
     * @return 转换后的集合，如果源集合为空，则返回一个空的集合
     */
    public static <T, U, R> Set<R> convertSetByFlatMap(Collection<T> from, Function<? super T, ? extends U> mapper, Function<U, ? extends Stream<? extends R>> func) {
        if (CollUtil.isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().map(mapper).filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }


    /**
     * 将一个集合转换为一个映射，其中每个元素通过给定的键函数映射到一个键
     *
     * @param from    源集合
     * @param keyFunc 键函数，用于从每个元素中提取键
     * @return 转换后的映射，如果源集合为空，则返回一个空的映射
     */
    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 HashMap
            return new HashMap<>();
        }
        // 使用 convertMap 方法的重载版本，传入一个恒等函数作为值函数
        return convertMap(from, keyFunc, Function.identity());
    }


    /**
     * 将一个集合转换为一个映射，其中每个元素通过给定的键函数映射到一个键
     *
     * @param from     源集合
     * @param keyFunc  键函数，用于从每个元素中提取键
     * @param supplier 映射的供应商，用于创建新的映射实例
     * @return 转换后的映射，如果源集合为空，则返回一个空的映射
     */
    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 HashMap
            return supplier.get();
        }
        // 使用 convertMap 方法的重载版本，传入一个恒等函数作为值函数
        return convertMap(from, keyFunc, Function.identity(), supplier);
    }


    /**
     * 将一个集合转换为一个映射，其中每个元素通过给定的键函数和值函数映射到一个键值对
     *
     * @param from      源集合
     * @param keyFunc   键函数，用于从每个元素中提取键
     * @param valueFunc 值函数，用于从每个元素中提取值
     * @return 转换后的映射，如果源集合为空，则返回一个空的映射
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 HashMap
            return new HashMap<>();
        }
        // 使用 convertMap 方法的重载版本，传入一个合并函数，该函数在遇到重复键时返回第一个值
        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
    }


    /**
     * 将一个集合转换为一个映射，其中每个元素通过给定的键函数和值函数映射到一个键值对
     * 如果遇到重复的键，则使用指定的合并函数来合并值
     *
     * @param from          源集合
     * @param keyFunc       键函数，用于从每个元素中提取键
     * @param valueFunc     值函数，用于从每个元素中提取值
     * @param mergeFunction 合并函数，用于在遇到重复键时合并值
     * @return 转换后的映射，如果源集合为空，则返回一个空的映射
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 HashMap
            return new HashMap<>();
        }
        // 使用 convertMap 方法的重载版本，传入一个合并函数，该函数在遇到重复键时返回第一个值
        return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
    }


    /**
     * 将一个集合转换为一个映射，其中每个元素通过给定的键函数和值函数映射到一个键值对
     * 如果遇到重复的键，则使用指定的合并函数来合并值
     *
     * @param from      源集合
     * @param keyFunc   键函数，用于从每个元素中提取键
     * @param valueFunc 值函数，用于从每个元素中提取值
     * @param ()        合并函数，用于在遇到重复键时合并值
     * @param supplier  映射的供应商，用于创建新的映射实例
     * @return 转换后的映射，如果源集合为空，则返回一个空的映射
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 HashMap
            return supplier.get();
        }
        // 使用 convertMap 方法的重载版本，传入一个合并函数，该函数在遇到重复键时返回第一个值
        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
    }


    /**
     * 将一个集合转换为一个映射，其中每个元素通过给定的键函数和值函数映射到一个键值对
     * 如果遇到重复的键，则使用指定的合并函数来合并值
     *
     * @param from          源集合
     * @param keyFunc       键函数，用于从每个元素中提取键
     * @param valueFunc     值函数，用于从每个元素中提取值
     * @param mergeFunction 合并函数，用于在遇到重复键时合并值
     * @param supplier      映射的供应商，用于创建新的映射实例
     * @return 转换后的映射，如果源集合为空，则返回一个空的映射
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 HashMap
            return new HashMap<>();
        }
        // 使用 convertMap 方法的重载版本，传入一个合并函数，该函数在遇到重复键时返回第一个值
        return from.stream().collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier));
    }


    /**
     * 将一个集合转换为一个映射，其中每个元素通过给定的键函数映射到一个键，并且每个键对应一个列表，列表中包含所有映射到该键的元素
     *
     * @param from    源集合
     * @param keyFunc 键函数，用于从每个元素中提取键
     * @return 转换后的映射，如果源集合为空，则返回一个空的映射
     */
    public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 HashMap
            return new HashMap<>();
        }
        // 使用 stream 方法将集合转换为流
        return from.stream()
                // 使用 collect 方法将流收集到一个映射中，其中键是通过 keyFunc 函数从每个元素中提取的，值是一个列表，包含所有映射到该键的元素
                .collect(Collectors.groupingBy(keyFunc, Collectors.mapping(t -> t, Collectors.toList())));
    }


    /**
     * 将一个集合转换为一个映射，其中每个元素通过给定的键函数和值函数映射到一个键值对
     * 并且每个键对应一个列表，列表中包含所有映射到该键的元素
     *
     * @param from      源集合
     * @param keyFunc   键函数，用于从每个元素中提取键
     * @param valueFunc 值函数，用于从每个元素中提取值
     * @return 转换后的映射，如果源集合为空，则返回一个空的映射
     */
    public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的 HashMap
            return new HashMap<>();
        }
        // 使用 stream 方法将集合转换为流
        return from.stream()
                // 使用 collect 方法将流收集到一个映射中，其中键是通过 keyFunc 函数从每个元素中提取的，值是一个列表，包含所有映射到该键的元素
                // 这里使用了 Collectors.groupingBy 来分组，并使用 Collectors.mapping 来转换每个元素的值
                .collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList())));
    }


    /**
     * 将一个集合转换为一个映射，其中每个元素通过给定的键函数和值函数映射到一个键值对
     * 并且每个键对应一个集合，集合中包含所有映射到该键的元素
     *
     * @param from      源集合
     * @param keyFunc   键函数，用于从每个元素中提取键
     * @param valueFunc 值函数，用于从每个元素中提取值
     * @return 转换后的映射，如果源集合为空，则返回一个空的映射
     */
    public static <T, K, V> Map<K, Set<V>> convertMultiMapToSet(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toSet())));
    }


    /**
     * 将一个集合转换为一个不可变映射，其中每个元素通过给定的键函数映射到一个键
     *
     * @param from    源集合
     * @param keyFunc 键函数，用于从每个元素中提取键
     * @return 转换后的不可变映射，如果源集合为空，则返回一个空的不可变映射
     */
    public static <T, K> Map<K, T> convertImmutableMap(Collection<T> from, Function<T, K> keyFunc) {
        // 检查源集合是否为空
        if (CollUtil.isEmpty(from)) {
            // 如果源集合为空，返回一个空的不可变映射
            return Collections.emptyMap();
        }
        // 创建一个不可变映射的构建器
        ImmutableMap.Builder<K, T> builder = ImmutableMap.builder();
        // 遍历源集合中的每个元素
        from.forEach(item -> builder.put(keyFunc.apply(item), item));
        // 构建并返回不可变映射
        return builder.build();
    }


    /**
     * 对比老、新两个列表，找出新增、修改、删除的数据
     *
     * @param oldList  老列表
     * @param newList  新列表
     * @param sameFunc 对比函数，返回 true 表示相同，返回 false 表示不同
     *                 注意，same 是通过每个元素的“标识”，判断它们是不是同一个数据
     * @return [新增列表、修改列表、删除列表]
     */
    public static <T> List<List<T>> diffList(Collection<T> oldList, Collection<T> newList, BiFunction<T, T, Boolean> sameFunc) {
        // 创建一个新列表，默认包含新列表中的所有元素，后续会移除已匹配的元素
        List<T> createList = new LinkedList<>(newList);
        // 创建一个修改列表，用于存储匹配且发生变化的元素
        List<T> updateList = new ArrayList<>();
        // 创建一个删除列表，用于存储在新列表中未找到匹配的老元素
        List<T> deleteList = new ArrayList<>();

        // 遍历老列表中的每个元素
        for (T oldObj : oldList) {
            // 初始化找到的匹配元素为 null
            T foundObj = null;
            // 遍历新列表中的元素，寻找匹配的元素
            for (Iterator<T> iterator = createList.iterator(); iterator.hasNext(); ) {
                T newObj = iterator.next();
                // 如果对比函数返回 false，即元素不匹配，则继续寻找
                if (!sameFunc.apply(oldObj, newObj)) {
                    continue;
                }
                // 如果找到匹配的元素，将其从新列表中移除，并结束寻找
                iterator.remove();
                foundObj = newObj;
                break;
            }
            // 如果找到匹配的元素，将其添加到修改列表中；否则，将老元素添加到删除列表中
            if (foundObj != null) {
                updateList.add(foundObj);
            } else {
                deleteList.add(oldObj);
            }
        }
        // 返回包含新增、修改和删除列表的结果
        return asList(createList, updateList, deleteList);
    }


    /**
     * 检查一个集合是否包含另一个集合中的任何元素
     *
     * @param source     源集合
     * @param candidates 候选集合
     * @return 如果源集合包含候选集合中的任何元素，则返回 true；否则返回 false
     */
    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        // 调用 Spring 框架的 CollectionUtils 类的 containsAny 方法来检查源集合是否包含候选集合中的任何元素
        return org.springframework.util.CollectionUtils.containsAny(source, candidates);
    }


    /**
     * 获取列表中的第一个元素
     *
     * @param from 源列表
     * @return 如果列表不为空，则返回列表的第一个元素；否则返回 null
     */
    public static <T> T getFirst(List<T> from) {
        // 检查列表是否为空
        return CollectionUtil.isNotEmpty(from) ? from.get(0) : null;
    }


    /**
     * 在给定的集合中查找满足指定谓词条件的第一个元素
     * 如果找到了符合条件的元素，则返回该元素；否则返回 null
     *
     * @param from      要搜索的集合
     * @param predicate 用于测试每个元素是否满足条件的谓词
     * @return 满足条件的第一个元素，如果没有找到则返回 null
     */
    public static <T> T findFirst(Collection<T> from, Predicate<T> predicate) {
        // 调用 findFirst 方法的重载版本，传入一个标识函数，该函数返回每个元素本身
        return findFirst(from, predicate, Function.identity());
    }


    /**
     * 在给定的集合中查找满足指定谓词条件的第一个元素，并将其转换为指定类型
     * 如果找到了符合条件的元素，则返回转换后的元素；否则返回 null
     *
     * @param from      要搜索的集合
     * @param predicate 用于测试每个元素是否满足条件的谓词
     * @param func      用于将满足条件的元素转换为指定类型的函数
     * @return 满足条件的第一个元素的转换结果，如果没有找到则返回 null
     */
    public static <T, U> U findFirst(Collection<T> from, Predicate<T> predicate, Function<T, U> func) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }
        return from.stream().filter(predicate).findFirst().map(func).orElse(null);
    }


    /**
     * 获取集合中的最大值
     *
     * @param from      源集合
     * @param valueFunc 用于从每个元素中提取值的函数
     * @return 集合中的最大值，如果集合为空，则返回 null
     */
    public static <T, V extends Comparable<? super V>> V getMaxValue(Collection<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }
        assert !from.isEmpty(); // 断言，避免告警
        T t = from.stream().max(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }


    /**
     * 获取列表中的最小值
     *
     * @param from      源列表
     * @param valueFunc 用于从每个元素中提取值的函数
     * @return 集合中的最小值，如果集合为空，则返回 null
     */
    public static <T, V extends Comparable<? super V>> V getMinValue(List<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }
        assert !from.isEmpty(); // 断言，避免告警
        T t = from.stream().min(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }


    /**
     * 获取列表中的最小值对象
     *
     * @param from      源列表
     * @param valueFunc 用于从每个元素中提取值的函数
     * @return 集合中的最小值对象，如果集合为空，则返回 null
     */
    public static <T, V extends Comparable<? super V>> T getMinObject(List<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }
        assert !from.isEmpty(); // 断言，避免告警
        return from.stream().min(Comparator.comparing(valueFunc)).get();
    }


    /**
     * 计算集合中元素的总和值
     *
     * @param from        源集合
     * @param valueFunc   用于从每个元素中提取值的函数
     * @param accumulator 用于累加值的函数
     * @return 集合中元素的总和值，如果集合为空，则返回 null
     */
    public static <T, V extends Comparable<? super V>> V getSumValue(Collection<T> from, Function<T, V> valueFunc, BinaryOperator<V> accumulator) {
        // 调用另一个重载的 getSumValue 方法，传入默认值为 null
        return getSumValue(from, valueFunc, accumulator, null);
    }


    /**
     * 计算集合中元素的总和值
     *
     * @param from         源集合
     * @param valueFunc    用于从每个元素中提取值的函数
     * @param accumulator  用于累加值的函数
     * @param defaultValue 如果集合为空，则返回的默认值
     * @return 集合中元素的总和值，如果集合为空，则返回默认值
     */
    public static <T, V extends Comparable<? super V>> V getSumValue(Collection<T> from, Function<T, V> valueFunc, BinaryOperator<V> accumulator, V defaultValue) {
        // 如果集合为空，则返回默认值
        if (CollUtil.isEmpty(from)) {
            return defaultValue;
        }
        // 断言，避免告警
        assert !from.isEmpty();
        // 使用流操作计算总和值
        return from.stream().map(valueFunc).filter(Objects::nonNull).reduce(accumulator).orElse(defaultValue);
    }


    /**
     * 如果给定的元素不为 null，则将其添加到指定的集合中
     *
     * @param coll 要添加元素的集合
     * @param item 要添加的元素
     */
    public static <T> void addIfNotNull(Collection<T> coll, T item) {
        // 检查元素是否为 null
        if (item == null) {
            // 如果元素为 null，则直接返回，不进行任何操作
            return;
        }
        // 如果元素不为 null，则将其添加到集合中
        coll.add(item);
    }


    /**
     * 创建一个只包含一个元素的集合
     *
     * @param obj 要包含的元素
     * @return 如果元素为 null，则返回一个空列表；否则返回一个只包含该元素的不可变集合
     */
    public static <T> Collection<T> singleton(T obj) {
        // 如果元素为 null，则返回一个空列表
        return obj == null ? Collections.emptyList() : Collections.singleton(obj);
    }


    /**
     * 将一个包含列表的列表转换为一个单一的列表
     *
     * @param list 包含列表的列表
     * @return 一个包含所有元素的新列表
     */
    public static <T> List<T> newArrayList(List<List<T>> list) {
        // 使用流的 flatMap 方法将每个内部列表展开，然后收集到一个新列表中
        return list.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }


}