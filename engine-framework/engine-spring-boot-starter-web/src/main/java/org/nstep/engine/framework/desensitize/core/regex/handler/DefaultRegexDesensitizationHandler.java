package org.nstep.engine.framework.desensitize.core.regex.handler;

import org.nstep.engine.framework.desensitize.core.regex.annotation.RegexDesensitize;

/**
 * {@link RegexDesensitize} 的正则脱敏处理器
 * <p>
 * 该类实现了正则脱敏处理器，用于处理 {@link RegexDesensitize} 注解的字段。
 * 它根据注解中的正则表达式和替换规则进行脱敏操作。
 * </p>
 */
public class DefaultRegexDesensitizationHandler extends AbstractRegexDesensitizationHandler<RegexDesensitize> {

    /**
     * 获取 {@link RegexDesensitize} 注解中的正则表达式
     *
     * @param annotation {@link RegexDesensitize} 注解
     * @return 正则表达式
     */
    @Override
    String getRegex(RegexDesensitize annotation) {
        return annotation.regex();  // 返回注解中定义的正则表达式
    }

    /**
     * 获取 {@link RegexDesensitize} 注解中的替换规则
     *
     * @param annotation {@link RegexDesensitize} 注解
     * @return 替换规则
     */
    @Override
    String getReplacer(RegexDesensitize annotation) {
        return annotation.replacer();  // 返回注解中定义的替换规则
    }

    /**
     * 获取 {@link RegexDesensitize} 注解中的禁用脱敏的 Spring EL 表达式
     *
     * @param annotation {@link RegexDesensitize} 注解
     * @return 禁用脱敏的表达式
     */
    @Override
    public String getDisable(RegexDesensitize annotation) {
        return annotation.disable();  // 返回禁用脱敏的 Spring EL 表达式
    }

}
