package org.nstep.engine.framework.common.validation;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PhoneUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 自定义电话格式验证器
 */
public class TelephoneValidator implements ConstraintValidator<Telephone, String> {

    @Override
    public void initialize(Telephone annotation) {
        // 初始化方法，通常用于注解的设置，当前无操作
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果电话为空，默认不校验，即校验通过
        if (CharSequenceUtil.isEmpty(value)) {
            return true;
        }
        // 使用 HuTool 的 PhoneUtil 工具类验证电话
        return PhoneUtil.isTel(value) || PhoneUtil.isPhone(value);
    }
}
