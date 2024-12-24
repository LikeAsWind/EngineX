package org.nstep.engine.framework.common.util.validation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * 校验工具类，提供常见的验证方法。
 */
public class ValidationUtils {

    // 正则表达式：用于验证中国大陆手机号
    private static final Pattern PATTERN_MOBILE = Pattern.compile("^(?:(?:\\+|00)86)?1(?:(?:3[\\d])|(?:4[0,1,4-9])|(?:5[0-3,5-9])|(?:6[2,5-7])|(?:7[0-8])|(?:8[\\d])|(?:9[0-3,5-9]))\\d{8}$");

    // 正则表达式：用于验证 URL 格式
    private static final Pattern PATTERN_URL = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zAZ0-9+&@#/%=~_|]");

    // 正则表达式：用于验证 XML 名称的格式
    private static final Pattern PATTERN_XML_NCNAME = Pattern.compile("[a-zA-Z_][\\-_.0-9_a-zA-Z$]*");

    /**
     * 校验手机号格式
     *
     * @param mobile 待校验的手机号
     * @return 如果手机号格式正确，返回 true；否则返回 false
     */
    public static boolean isMobile(String mobile) {
        return StringUtils.hasText(mobile)
                && PATTERN_MOBILE.matcher(mobile).matches(); // 使用正则表达式匹配手机号格式
    }

    /**
     * 校验 URL 格式
     *
     * @param url 待校验的 URL
     * @return 如果 URL 格式正确，返回 true；否则返回 false
     */
    public static boolean isURL(String url) {
        return StringUtils.hasText(url)
                && PATTERN_URL.matcher(url).matches(); // 使用正则表达式匹配 URL 格式
    }

    /**
     * 校验 XML 名称格式
     *
     * @param str 待校验的字符串
     * @return 如果字符串符合 XML 名称规则，返回 true；否则返回 false
     */
    public static boolean isXmlNCName(String str) {
        return StringUtils.hasText(str)
                && PATTERN_XML_NCNAME.matcher(str).matches(); // 使用正则表达式匹配 XML 名称格式
    }

    /**
     * 校验对象的约束条件，抛出异常（如果有违反约束的情况）
     *
     * @param object 待校验的对象
     * @param groups 校验分组（可选）
     */
    public static void validate(Object object, Class<?>... groups) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Assert.notNull(validator); // 确保 Validator 不为空
        validate(validator, object, groups); // 调用具体的校验方法
    }

    /**
     * 使用指定的 Validator 校验对象的约束条件，抛出异常（如果有违反约束的情况）
     *
     * @param validator 校验器
     * @param object    待校验的对象
     * @param groups    校验分组（可选）
     */
    public static void validate(Validator validator, Object object, Class<?>... groups) {
        // 获取校验结果
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        // 如果存在违反约束的情况，则抛出异常
        if (CollUtil.isNotEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

}
