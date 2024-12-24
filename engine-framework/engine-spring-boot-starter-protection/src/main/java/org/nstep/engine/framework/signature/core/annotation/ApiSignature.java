package org.nstep.engine.framework.signature.core.annotation;

import org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * HTTP API 签名注解
 * <p>
 * 该注解用于标记需要进行签名验证的 HTTP API 接口。它可以应用于方法或类级别，用于指定签名参数、超时时间等配置。
 * 当接口请求中包含正确的签名时，才能通过签名验证。
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSignature {

    /**
     * 同一个请求多长时间内有效，默认 60 秒
     * <p>
     * 该参数用于设置签名的有效时间，超过该时间签名将失效。默认为 60 秒。
     *
     * @return 超时时间（单位：秒）
     */
    int timeout() default 60;

    /**
     * 时间单位，默认为 SECONDS 秒
     * <p>
     * 用于指定超时时间的单位。默认单位为秒（SECONDS），可以根据需要设置为其他时间单位。
     *
     * @return 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    // ========================== 签名参数 ==========================

    /**
     * 提示信息，签名失败的提示
     * <p>
     * 当签名验证失败时，返回的错误提示信息。如果为空，则使用默认的错误提示 "签名不正确"。
     *
     * @return 签名失败时的提示信息
     * @see GlobalErrorCodeConstants#BAD_REQUEST
     */
    String message() default "签名不正确"; // 为空时，使用 BAD_REQUEST 错误提示

    /**
     * 签名字段：appId 应用ID
     * <p>
     * 用于指定签名字段中的应用 ID。默认为 "appId"。
     *
     * @return 应用 ID 字段名称
     */
    String appId() default "appId";

    /**
     * 签名字段：timestamp 时间戳
     * <p>
     * 用于指定签名字段中的时间戳。默认为 "timestamp"。
     *
     * @return 时间戳字段名称
     */
    String timestamp() default "timestamp";

    /**
     * 签名字段：nonce 随机数，10 位以上
     * <p>
     * 用于指定签名字段中的随机数。默认为 "nonce"。
     *
     * @return 随机数字段名称
     */
    String nonce() default "nonce";

    /**
     * sign 客户端签名
     * <p>
     * 用于指定签名字段中的签名。默认为 "sign"。
     *
     * @return 签名字段名称
     */
    String sign() default "sign";

}
