package org.nstep.engine.framework.desensitize.core.regex.handler;

import org.nstep.engine.framework.desensitize.core.regex.annotation.EmailDesensitize;

/**
 * {@link EmailDesensitize} 的脱敏处理器
 * <p>
 * 该类实现了邮箱脱敏处理器，用于处理 {@link EmailDesensitize} 注解的字段。
 * 它根据注解中的正则表达式和替换规则进行邮箱脱敏操作。
 * </p>
 */
public class EmailDesensitizationHandler extends AbstractRegexDesensitizationHandler<EmailDesensitize> {

    /**
     * 获取 {@link EmailDesensitize} 注解中的正则表达式
     *
     * @param annotation {@link EmailDesensitize} 注解
     * @return 正则表达式
     */
    @Override
    String getRegex(EmailDesensitize annotation) {
        return annotation.regex();  // 返回注解中定义的正则表达式
    }

    /**
     * 获取 {@link EmailDesensitize} 注解中的替换规则
     *
     * @param annotation {@link EmailDesensitize} 注解
     * @return 替换规则
     */
    @Override
    String getReplacer(EmailDesensitize annotation) {
        return annotation.replacer();  // 返回注解中定义的替换规则
    }

}
