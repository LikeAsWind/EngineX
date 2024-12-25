package org.nstep.engine.module.system.enums.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录结果的枚举类
 * <p>
 * 该枚举类定义了不同的登录结果类型，包括：
 * - 登录成功
 * - 账号或密码不正确
 * - 用户被禁用
 * - 图片验证码不存在
 * - 图片验证码不正确
 */
@Getter
@AllArgsConstructor
public enum LoginResultEnum {

    /**
     * 登录成功
     * <p>
     * 代表用户登录成功，结果值为 0。
     */
    SUCCESS(0),

    /**
     * 账号或密码不正确
     * <p>
     * 代表用户输入的账号或密码不正确，结果值为 10。
     */
    BAD_CREDENTIALS(10),

    /**
     * 用户被禁用
     * <p>
     * 代表用户账号被禁用，无法登录，结果值为 20。
     */
    USER_DISABLED(20),

    /**
     * 图片验证码不存在
     * <p>
     * 代表系统未找到图片验证码，结果值为 30。
     */
    CAPTCHA_NOT_FOUND(30),

    /**
     * 图片验证码不正确
     * <p>
     * 代表用户输入的图片验证码不正确，结果值为 31。
     */
    CAPTCHA_CODE_ERROR(31);

    /**
     * 登录结果
     * <p>
     * 存储登录结果的具体类型值，具体值对应不同的登录结果状态。
     */
    private final Integer result;

}
