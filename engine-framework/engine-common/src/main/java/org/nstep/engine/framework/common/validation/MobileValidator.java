package org.nstep.engine.framework.common.validation;

import cn.hutool.core.util.StrUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.nstep.engine.framework.common.util.validation.ValidationUtils;

/**
 * 自定义手机号验证器
 */
public class MobileValidator implements ConstraintValidator<Mobile, String> {

    /**
     * 初始化方法，注解初始化时调用（此处没有特殊处理）
     *
     * @param annotation 注解本身
     */
    @Override
    public void initialize(Mobile annotation) {
        // 没有特殊初始化操作
    }

    /**
     * 校验方法，用于验证手机号是否有效
     *
     * @param value   要验证的手机号
     * @param context 校验上下文
     * @return 如果手机号有效返回 true，否则返回 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果手机号为空，默认不校验，即校验通过
        if (StrUtil.isEmpty(value)) {
            return true;
        }
        // 使用 ValidationUtils 中的 isMobile 方法校验手机号格式
        return ValidationUtils.isMobile(value);
    }
}
