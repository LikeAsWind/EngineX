package org.nstep.engine.framework.desensitize.core.slider.handler;

import org.nstep.engine.framework.desensitize.core.slider.annotation.BankCardDesensitize;

/**
 * {@link BankCardDesensitize} 的脱敏处理器
 * 该类实现了针对 {@link BankCardDesensitize} 注解的脱敏处理逻辑，继承自 {@link AbstractSliderDesensitizationHandler}。
 * 它负责根据注解中的配置对银行卡号进行滑动脱敏处理。
 */
public class BankCardDesensitization extends AbstractSliderDesensitizationHandler<BankCardDesensitize> {

    /**
     * 获取银行卡号前缀保留长度
     * 该方法从 {@link BankCardDesensitize} 注解中获取前缀保留长度的配置。
     *
     * @param annotation {@link BankCardDesensitize} 注解
     * @return 前缀保留长度
     */
    @Override
    Integer getPrefixKeep(BankCardDesensitize annotation) {
        return annotation.prefixKeep();  // 返回注解中配置的前缀保留长度
    }

    /**
     * 获取银行卡号后缀保留长度
     * 该方法从 {@link BankCardDesensitize} 注解中获取后缀保留长度的配置。
     *
     * @param annotation {@link BankCardDesensitize} 注解
     * @return 后缀保留长度
     */
    @Override
    Integer getSuffixKeep(BankCardDesensitize annotation) {
        return annotation.suffixKeep();  // 返回注解中配置的后缀保留长度
    }

    /**
     * 获取替换符
     * 该方法从 {@link BankCardDesensitize} 注解中获取用于替换中间部分的字符。
     *
     * @param annotation {@link BankCardDesensitize} 注解
     * @return 替换符
     */
    @Override
    String getReplacer(BankCardDesensitize annotation) {
        return annotation.replacer();  // 返回注解中配置的替换符
    }

    /**
     * 获取禁用脱敏的 Spring EL 表达式
     * 该方法用于获取禁用脱敏的配置，当前实现返回空字符串，表示没有禁用脱敏。
     *
     * @param annotation {@link BankCardDesensitize} 注解
     * @return 禁用脱敏的 Spring EL 表达式
     */
    @Override
    public String getDisable(BankCardDesensitize annotation) {
        return "";  // 返回空字符串表示没有禁用脱敏
    }

}
