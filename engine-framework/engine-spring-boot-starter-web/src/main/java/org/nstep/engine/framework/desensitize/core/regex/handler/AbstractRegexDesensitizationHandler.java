package org.nstep.engine.framework.desensitize.core.regex.handler;

import org.nstep.engine.framework.common.util.spring.SpringExpressionUtils;
import org.nstep.engine.framework.desensitize.core.base.handler.DesensitizationHandler;

import java.lang.annotation.Annotation;

/**
 * 正则表达式脱敏处理器抽象类，已实现通用的方法
 * <p>
 * 该类实现了脱敏处理的通用逻辑，包括判断是否禁用脱敏、执行脱敏操作。具体的正则表达式和替换规则需要由子类实现。
 * </p>
 */
public abstract class AbstractRegexDesensitizationHandler<T extends Annotation>
        implements DesensitizationHandler<T> {

    /**
     * 执行脱敏操作
     * <p>
     * 该方法会判断是否禁用脱敏，如果禁用则直接返回原始字符串；否则，使用正则表达式和替换规则进行脱敏。
     * </p>
     *
     * @param origin     原始字符串
     * @param annotation 注解信息
     * @return 脱敏后的字符串
     */
    @Override
    public String desensitize(String origin, T annotation) {
        // 1. 判断是否禁用脱敏
        Object disable = SpringExpressionUtils.parseExpression(getDisable(annotation));
        if (Boolean.TRUE.equals(disable)) {
            return origin;  // 如果禁用脱敏，返回原始字符串
        }

        // 2. 执行脱敏
        String regex = getRegex(annotation);  // 获取正则表达式
        String replacer = getReplacer(annotation);  // 获取替换规则
        return origin.replaceAll(regex, replacer);  // 使用正则表达式进行替换
    }

    /**
     * 获取注解上的 regex 参数
     * <p>
     * 该方法由子类实现，用于获取注解中定义的正则表达式。
     * </p>
     *
     * @param annotation 注解信息
     * @return 正则表达式
     */
    abstract String getRegex(T annotation);

    /**
     * 获取注解上的 replacer 参数
     * <p>
     * 该方法由子类实现，用于获取注解中定义的替换规则。
     * </p>
     *
     * @param annotation 注解信息
     * @return 替换字符串
     */
    abstract String getReplacer(T annotation);

}
