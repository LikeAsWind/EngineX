package org.nstep.engine.module.system.enums.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录日志的类型枚举
 * <p>
 * 该枚举类定义了不同的登录日志类型，包括：
 * - 使用账号登录
 * - 使用社交登录
 * - 使用手机登录
 * - 使用短信登录
 * - 自己主动登出
 * - 强制退出
 */
@Getter
@AllArgsConstructor
public enum LoginLogTypeEnum {

    /**
     * 使用账号登录
     * <p>
     * 代表用户通过账号进行登录，日志类型值为 100。
     */
    LOGIN_USERNAME(100),

    /**
     * 使用社交登录
     * <p>
     * 代表用户通过社交平台进行登录，日志类型值为 101。
     */
    LOGIN_SOCIAL(101),

    /**
     * 使用手机登录
     * <p>
     * 代表用户通过手机进行登录，日志类型值为 103。
     */
    LOGIN_MOBILE(103),

    /**
     * 使用短信登录
     * <p>
     * 代表用户通过短信验证码进行登录，日志类型值为 104。
     */
    LOGIN_SMS(104),

    /**
     * 自己主动登出
     * <p>
     * 代表用户自己主动登出，日志类型值为 200。
     */
    LOGOUT_SELF(200),

    /**
     * 强制退出
     * <p>
     * 代表管理员或系统强制用户退出，日志类型值为 202。
     */
    LOGOUT_DELETE(202);

    /**
     * 日志类型
     * <p>
     * 存储登录日志的具体类型值，具体值对应不同的登录或登出方式。
     */
    private final Integer type;

}
