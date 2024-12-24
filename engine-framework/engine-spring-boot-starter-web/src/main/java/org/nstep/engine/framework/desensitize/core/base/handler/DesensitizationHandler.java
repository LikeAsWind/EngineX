package org.nstep.engine.framework.desensitize.core.base.handler;

import cn.hutool.core.util.ReflectUtil;

import java.lang.annotation.Annotation;

/**
 * 脱敏处理器接口
 * <p>
 * 该接口定义了脱敏操作的基本结构。实现该接口的类负责具体的脱敏逻辑，
 * 通过 `desensitize` 方法对原始字符串进行脱敏处理。此接口还提供了一个默认方法 `getDisable`，
 * 用于判断是否禁用脱敏功能。
 */
public interface DesensitizationHandler<T extends Annotation> {

    /**
     * 脱敏处理方法
     * <p>
     * 实现该方法对原始字符串进行脱敏处理，返回脱敏后的字符串。
     * 具体的脱敏逻辑由实现类根据注解信息进行自定义。
     *
     * @param origin     原始字符串，待脱敏的字段值
     * @param annotation 注解信息，提供脱敏处理所需的配置
     * @return 脱敏后的字符串
     */
    String desensitize(String origin, T annotation);

    /**
     * 是否禁用脱敏的 Spring EL 表达式
     * <p>
     * 该方法用于判断是否禁用脱敏操作。通过 Spring EL 表达式的方式，判断是否禁用脱敏。
     * 如果返回 `true`，则跳过脱敏操作。
     * <p>
     * 默认实现是通过反射获取注解中的 `disable` 属性值。
     *
     * @param annotation 注解信息，提供禁用脱敏的配置
     * @return 是否禁用脱敏的 Spring EL 表达式，返回 `true` 表示禁用脱敏
     */
    default String getDisable(T annotation) {
        // 约定：默认就是 enable() 属性。如果不符合，子类重写
        try {
            return ReflectUtil.invoke(annotation, "disable");
        } catch (Exception ex) {
            return "";
        }
    }

}
