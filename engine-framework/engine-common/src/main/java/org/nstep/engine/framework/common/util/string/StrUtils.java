package org.nstep.engine.framework.common.util.string;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 字符串工具类，提供常见的字符串处理方法。
 */
public class StrUtils {

    /**
     * 截取字符串至最大长度，超过部分用 "..." 替代。
     *
     * @param str       被截取的字符串
     * @param maxLength 最大长度，包含 "..." 部分
     * @return 截取后的字符串
     */
    public static String maxLength(CharSequence str, int maxLength) {
        return StrUtil.maxLength(str, maxLength - 3); // -3 的原因，是该方法会补充 "..." 恰好
    }

    /**
     * 检查给定字符串是否以任何一个指定的前缀开始。
     * 如果给定的字符串或前缀集合为空，则返回 false。
     *
     * @param str      给定的字符串
     * @param prefixes 需要检测的前缀集合
     * @return 如果字符串以任何一个前缀开始，返回 true；否则返回 false
     * @since 3.0.6
     */
    public static boolean startWithAny(String str, Collection<String> prefixes) {
        // 如果给定字符串或前缀集合为空，返回 false
        if (StrUtil.isEmpty(str) || ArrayUtil.isEmpty(prefixes)) {
            return false;
        }

        // 遍历前缀集合，检查是否有任何一个前缀匹配
        for (CharSequence suffix : prefixes) {
            if (StrUtil.startWith(str, suffix, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将字符串按指定分隔符分割并转换为 Long 类型的列表。
     *
     * @param value     待分割的字符串
     * @param separator 分隔符
     * @return 转换后的 Long 类型列表
     */
    public static List<Long> splitToLong(String value, CharSequence separator) {
        long[] longs = StrUtil.splitToLong(value, separator);
        return Arrays.stream(longs).boxed().collect(Collectors.toList());
    }

    /**
     * 将字符串按逗号分割并转换为 Long 类型的集合。
     *
     * @param value 待分割的字符串
     * @return 转换后的 Long 类型集合
     */
    public static Set<Long> splitToLongSet(String value) {
        return splitToLongSet(value, StrPool.COMMA);
    }

    /**
     * 将字符串按指定分隔符分割并转换为 Long 类型的集合。
     *
     * @param value     待分割的字符串
     * @param separator 分隔符
     * @return 转换后的 Long 类型集合
     */
    public static Set<Long> splitToLongSet(String value, CharSequence separator) {
        long[] longs = StrUtil.splitToLong(value, separator);
        return Arrays.stream(longs).boxed().collect(Collectors.toSet());
    }

    /**
     * 将字符串按指定分隔符分割并转换为 Integer 类型的列表。
     *
     * @param value     待分割的字符串
     * @param separator 分隔符
     * @return 转换后的 Integer 类型列表
     */
    public static List<Integer> splitToInteger(String value, CharSequence separator) {
        int[] integers = StrUtil.splitToInt(value, separator);
        return Arrays.stream(integers).boxed().collect(Collectors.toList());
    }

    /**
     * 移除字符串中包含指定子字符串的行。
     *
     * @param content  待处理的字符串
     * @param sequence 需要移除的行中包含的子字符串
     * @return 移除指定行后的字符串
     */
    public static String removeLineContains(String content, String sequence) {
        // 如果输入内容或子字符串为空，直接返回原始内容
        if (StrUtil.isEmpty(content) || StrUtil.isEmpty(sequence)) {
            return content;
        }
        // 将字符串按行分割，过滤掉包含指定子字符串的行，然后重新拼接成字符串
        return Arrays.stream(content.split("\n"))
                .filter(line -> !line.contains(sequence)) // 过滤掉包含指定子字符串的行
                .collect(Collectors.joining("\n")); // 重新将剩余行连接成字符串
    }

}
