package org.nstep.engine.framework.desensitize.core.slider.handler;

import org.nstep.engine.framework.desensitize.core.slider.annotation.CarLicenseDesensitize;

/**
 * {@link CarLicenseDesensitize} 的脱敏处理器
 * 该类实现了针对 {@link CarLicenseDesensitize} 注解的脱敏处理逻辑，继承自 {@link AbstractSliderDesensitizationHandler}。
 * 它负责根据注解中的配置对车牌号进行滑动脱敏处理。
 */
public class CarLicenseDesensitization extends AbstractSliderDesensitizationHandler<CarLicenseDesensitize> {

    /**
     * 获取车牌号前缀保留长度
     * 该方法从 {@link CarLicenseDesensitize} 注解中获取前缀保留长度的配置。
     *
     * @param annotation {@link CarLicenseDesensitize} 注解
     * @return 前缀保留长度
     */
    @Override
    Integer getPrefixKeep(CarLicenseDesensitize annotation) {
        return annotation.prefixKeep();  // 返回注解中配置的前缀保留长度
    }

    /**
     * 获取车牌号后缀保留长度
     * 该方法从 {@link CarLicenseDesensitize} 注解中获取后缀保留长度的配置。
     *
     * @param annotation {@link CarLicenseDesensitize} 注解
     * @return 后缀保留长度
     */
    @Override
    Integer getSuffixKeep(CarLicenseDesensitize annotation) {
        return annotation.suffixKeep();  // 返回注解中配置的后缀保留长度
    }

    /**
     * 获取替换符
     * 该方法从 {@link CarLicenseDesensitize} 注解中获取用于替换中间部分的字符。
     *
     * @param annotation {@link CarLicenseDesensitize} 注解
     * @return 替换符
     */
    @Override
    String getReplacer(CarLicenseDesensitize annotation) {
        return annotation.replacer();  // 返回注解中配置的替换符
    }

    /**
     * 获取禁用脱敏的 Spring EL 表达式
     * 该方法从 {@link CarLicenseDesensitize} 注解中获取禁用脱敏的配置。
     *
     * @param annotation {@link CarLicenseDesensitize} 注解
     * @return 禁用脱敏的 Spring EL 表达式
     */
    @Override
    public String getDisable(CarLicenseDesensitize annotation) {
        return annotation.disable();  // 返回注解中配置的禁用脱敏表达式
    }

}
