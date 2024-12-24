package org.nstep.engine.framework.common.validation;

import cn.hutool.core.collection.CollUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.nstep.engine.framework.common.core.IntArrayValuable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 校验器，用于验证集合中的元素是否都在指定的枚举值范围内。
 * 该验证器适用于 {@link InEnum} 注解，检查集合中的整数值是否在指定的枚举类的有效值范围内。
 */
public class InEnumCollectionValidator implements ConstraintValidator<InEnum, Collection<Integer>> {

    private List<Integer> values;

    /**
     * 初始化方法，用于获取注解中指定的枚举类的有效值。
     * 从 {@link InEnum} 注解中获取枚举类，获取其实现的 {@link IntArrayValuable} 接口的有效值范围。
     *
     * @param annotation {@link InEnum} 注解
     */
    @Override
    public void initialize(InEnum annotation) {
        // 获取枚举类的常量值
        IntArrayValuable[] enumValues = annotation.value().getEnumConstants();
        if (enumValues.length == 0) {
            // 如果没有有效的枚举值，设置为空集合
            this.values = Collections.emptyList();
        } else {
            // 获取第一个枚举常量的有效值，并转化为列表
            this.values = Arrays.stream(enumValues[0].array())
                    .boxed()  // 将基本类型转换为包装类型
                    .collect(Collectors.toList());
        }
    }

    /**
     * 校验方法，检查集合中的元素是否都在指定的枚举值范围内。
     *
     * @param list    要校验的集合
     * @param context 校验上下文
     * @return 如果集合中的元素都在有效值范围内，返回 true；否则返回 false
     */
    @Override
    public boolean isValid(Collection<Integer> list, ConstraintValidatorContext context) {
        // 校验通过：如果集合中的所有元素都在有效值范围内
        if (CollUtil.containsAll(values, list)) {
            return true;
        }

        // 校验不通过：自定义提示语句
        context.disableDefaultConstraintViolation();  // 禁用默认的错误提示消息
        // 构建自定义错误消息，并替换掉模板中的 {value} 为实际的值
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()
                        .replaceAll("\\{value}", CollUtil.join(list, ",")))  // 将集合转为逗号分隔的字符串
                .addConstraintViolation();  // 添加自定义的错误消息
        return false;
    }

}
