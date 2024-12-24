package org.nstep.engine.framework.desensitize.core.slider.handler;

import org.nstep.engine.framework.common.util.spring.SpringExpressionUtils;
import org.nstep.engine.framework.desensitize.core.base.handler.DesensitizationHandler;

import java.lang.annotation.Annotation;

/**
 * 滑动脱敏处理器抽象类，已实现通用的方法。
 * 该类为滑动脱敏处理提供了通用的脱敏逻辑，并定义了获取注解属性的方法供子类实现。
 * 适用于需要对字符串进行滑动脱敏处理的场景。
 */
public abstract class AbstractSliderDesensitizationHandler<T extends Annotation>
        implements DesensitizationHandler<T> {

    /**
     * 执行脱敏操作
     * 根据注解中的配置，决定如何脱敏原始字符串。脱敏的规则包括前后缀保留的长度和替换字符。
     *
     * @param origin 原始字符串
     * @param annotation 脱敏注解
     * @return 脱敏后的字符串
     */
    @Override
    public String desensitize(String origin, T annotation) {
        // 1. 判断是否禁用脱敏
        Object disable = SpringExpressionUtils.parseExpression(getDisable(annotation));
        if (Boolean.FALSE.equals(disable)) {
            return origin;  // 如果禁用脱敏，直接返回原始字符串
        }

        // 2. 获取注解中的脱敏配置
        int prefixKeep = getPrefixKeep(annotation);  // 获取前缀保留长度
        int suffixKeep = getSuffixKeep(annotation);  // 获取后缀保留长度
        String replacer = getReplacer(annotation);   // 获取替换符
        int length = origin.length();  // 获取原始字符串的长度

        // 情况一：原始字符串长度小于等于保留长度，则原始字符串全部替换
        if (prefixKeep >= length || suffixKeep >= length) {
            return buildReplacerByLength(replacer, length);
        }

        // 情况二：原始字符串长度小于等于前后缀保留字符串长度，则原始字符串全部替换
        if ((prefixKeep + suffixKeep) >= length) {
            return buildReplacerByLength(replacer, length);
        }

        // 情况三：原始字符串长度大于前后缀保留字符串长度，则替换中间字符串
        int interval = length - prefixKeep - suffixKeep;  // 计算中间需要替换的部分长度
        return origin.substring(0, prefixKeep) +  // 保留前缀
                buildReplacerByLength(replacer, interval) +  // 替换中间部分
                origin.substring(prefixKeep + interval);  // 保留后缀
    }

    /**
     * 根据长度循环构建替换符
     * 根据给定的替换符和长度，生成一个由替换符构成的字符串。
     *
     * @param replacer 替换符
     * @param length   长度
     * @return 构建后的替换符字符串
     */
    private String buildReplacerByLength(String replacer, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(replacer);  // 将替换符追加到字符串构建器
        }
        return builder.toString();
    }

    /**
     * 获取前缀保留长度
     * 该方法由子类实现，用于获取注解中配置的前缀保留长度。
     *
     * @param annotation 注解信息
     * @return 前缀保留长度
     */
    abstract Integer getPrefixKeep(T annotation);

    /**
     * 获取后缀保留长度
     * 该方法由子类实现，用于获取注解中配置的后缀保留长度。
     *
     * @param annotation 注解信息
     * @return 后缀保留长度
     */
    abstract Integer getSuffixKeep(T annotation);

    /**
     * 获取替换符
     * 该方法由子类实现，用于获取注解中配置的替换符。
     *
     * @param annotation 注解信息
     * @return 替换符
     */
    abstract String getReplacer(T annotation);

}
