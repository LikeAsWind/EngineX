package org.nstep.engine.framework.desensitize.core.slider.handler;

import org.nstep.engine.framework.desensitize.core.slider.annotation.MobileDesensitize;

/**
 * {@link MobileDesensitize} 的脱敏处理器
 * 该类实现了针对 {@link MobileDesensitize} 注解的脱敏处理逻辑，继承自 {@link AbstractSliderDesensitizationHandler}。
 * 它负责根据注解中的配置对手机号进行滑动脱敏处理。
 */
public class MobileDesensitization extends AbstractSliderDesensitizationHandler<MobileDesensitize> {

    /**
     * 获取手机号前缀保留长度
     * 该方法从 {@link MobileDesensitize} 注解中获取前缀保留长度的配置。
     *
     * @param annotation {@link MobileDesensitize} 注解
     * @return 前缀保留长度
     */
    @Override
    Integer getPrefixKeep(MobileDesensitize annotation) {
        return annotation.prefixKeep();  // 返回注解中配置的前缀保留长度
    }

    /**
     * 获取手机号后缀保留长度
     * 该方法从 {@link MobileDesensitize} 注解中获取后缀保留长度的配置。
     *
     * @param annotation {@link MobileDesensitize} 注解
     * @return 后缀保留长度
     */
    @Override
    Integer getSuffixKeep(MobileDesensitize annotation) {
        return annotation.suffixKeep();  // 返回注解中配置的后缀保留长度
    }

    /**
     * 获取替换符
     * 该方法从 {@link MobileDesensitize} 注解中获取用于替换中间部分的字符。
     *
     * @param annotation {@link MobileDesensitize} 注解
     * @return 替换符
     */
    @Override
    String getReplacer(MobileDesensitize annotation) {
        return annotation.replacer();  // 返回注解中配置的替换符
    }
}
