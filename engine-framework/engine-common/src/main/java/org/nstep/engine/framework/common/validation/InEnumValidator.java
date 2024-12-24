package org.nstep.engine.framework.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.nstep.engine.framework.common.core.IntArrayValuable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 校验器，用于验证整数值是否在指定的枚举值范围内。
 * 该验证器适用于 {@link InEnum} 注解，检查单个整数值是否在指定的枚举类的有效值范围内。
 */
public class InEnumValidator implements ConstraintValidator<InEnum, Integer> {

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
     * 校验方法，检查单个整数值是否在指定的枚举值范围内。
     *
     * @param value   要校验的整数值
     * @param context 校验上下文
     * @return 如果整数值在有效值范围内，返回 true；否则返回 false
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        // 为空时，默认不校验，即认为通过
        if (value == null) {
            return true;
        }
        // 校验通过：如果值在有效值范围内
        if (values.contains(value)) {
            return true;
        }

        // 校验不通过：自定义提示语句
        context.disableDefaultConstraintViolation();  // 禁用默认的错误提示消息
        // 构建自定义错误消息，并替换掉模板中的 {value} 为实际的有效值范围
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()
                        .replaceAll("\\{value}", values.toString()))  // 将有效值转为字符串
                .addConstraintViolation();  // 添加自定义的错误消息
        return false;
    }

}
