package org.nstep.engine.framework.ratelimiter.core.keyresolver.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import org.aspectj.lang.JoinPoint;
import org.nstep.engine.framework.ratelimiter.core.annotation.RateLimiter;
import org.nstep.engine.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;

/**
 * 默认（全局级别）限流 Key 解析器，使用方法名 + 方法参数，组装成一个 Key
 * <p>
 * 该类根据方法名和方法参数生成唯一的限流 Key。通过 MD5 对生成的 Key 进行压缩，避免 Key 过长。
 * 该解析器适用于全局级别的限流场景。
 */
public class DefaultRateLimiterKeyResolver implements RateLimiterKeyResolver {

    /**
     * 解析限流的 Key
     * <p>
     * 该方法根据方法名和方法参数生成一个唯一的限流 Key。通过将这些信息组合成一个字符串并进行 MD5 压缩，
     * 确保生成的 Key 唯一且不会过长。
     *
     * @param joinPoint   AOP 切面，提供了方法签名和参数
     * @param rateLimiter 限流注解对象，包含限流配置
     * @return 返回一个唯一的限流 Key
     */
    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        // 获取方法名
        String methodName = joinPoint.getSignature().toString();
        // 获取方法参数的字符串表示
        String argsStr = StrUtil.join(",", joinPoint.getArgs());
        // 通过方法名和参数生成并返回 MD5 压缩后的限流 Key
        return SecureUtil.md5(methodName + argsStr);
    }

}
