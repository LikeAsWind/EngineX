package org.nstep.engine.framework.ratelimiter.core.keyresolver.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.system.SystemUtil;
import org.aspectj.lang.JoinPoint;
import org.nstep.engine.framework.ratelimiter.core.annotation.RateLimiter;
import org.nstep.engine.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;

/**
 * Server 节点级别的限流 Key 解析器，使用方法名 + 方法参数 + IP，组装成一个 Key
 * <p>
 * 该类通过结合方法名、方法参数和服务器节点信息（包括 IP 地址和进程 ID），
 * 动态生成限流 Key。为了避免 Key 过长，使用 MD5 进行“压缩”。
 */
public class ServerNodeRateLimiterKeyResolver implements RateLimiterKeyResolver {

    /**
     * 解析限流的 Key
     * <p>
     * 该方法使用方法名、方法参数和当前服务器节点（包括 IP 地址和进程 ID）来生成唯一的限流 Key。
     * 通过 MD5 对生成的 Key 进行压缩，避免 Key 过长。
     *
     * @param joinPoint   AOP 切面，提供方法签名和参数
     * @param rateLimiter 限流注解对象，包含限流配置
     * @return 返回解析后的限流 Key
     */
    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        // 获取方法名
        String methodName = joinPoint.getSignature().toString();

        // 获取方法参数的字符串表示
        String argsStr = StrUtil.join(",", joinPoint.getArgs());

        // 获取当前服务器节点的信息，包括 IP 地址和进程 ID
        String serverNode = String.format("%s@%d", SystemUtil.getHostInfo().getAddress(), SystemUtil.getCurrentPID());

        // 将方法名、参数和服务器节点信息拼接后，通过 MD5 进行压缩生成 Key
        return SecureUtil.md5(methodName + argsStr + serverNode);
    }

}
