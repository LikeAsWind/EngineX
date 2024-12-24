package org.nstep.engine.framework.desensitize.core.slider.handler;

import org.nstep.engine.framework.desensitize.core.slider.annotation.ChineseNameDesensitize;

/**
 * {@link ChineseNameDesensitize} 的脱敏处理器
 * 该类实现了针对 {@link ChineseNameDesensitize} 注解的脱敏处理逻辑，继承自 {@link AbstractSliderDesensitizationHandler}。
 * 它负责根据注解中的配置对中文姓名进行滑动脱敏处理。
 */
public class ChineseNameDesensitization extends AbstractSliderDesensitizationHandler<ChineseNameDesensitize> {

    /**
     * 获取中文姓名前缀保留长度
     * 该方法从 {@link ChineseNameDesensitize} 注解中获取前缀保留长度的配置。
     *
     * @param annotation {@link ChineseNameDesensitize} 注解
     * @return 前缀保留长度
     */
    @Override
    Integer getPrefixKeep(ChineseNameDesensitize annotation) {
        return annotation.prefixKeep();  // 返回注解中配置的前缀保留长度
    }

    /**
     * 获取中文姓名后缀保留长度
     * 该方法从 {@link ChineseNameDesensitize} 注解中获取后缀保留长度的配置。
     *
     * @param annotation {@link ChineseNameDesensitize} 注解
     * @return 后缀保留长度
     */
    @Override
    Integer getSuffixKeep(ChineseNameDesensitize annotation) {
        return annotation.suffixKeep();  // 返回注解中配置的后缀保留长度
    }

    /**
     * 获取替换符
     * 该方法从 {@link ChineseNameDesensitize} 注解中获取用于替换中间部分的字符。
     *
     * @param annotation {@link ChineseNameDesensitize} 注解
     * @return 替换符
     */
    @Override
    String getReplacer(ChineseNameDesensitize annotation) {
        return annotation.replacer();  // 返回注解中配置的替换符
    }
}
