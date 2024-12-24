package org.nstep.engine.framework.idempotent.core.keyresolver.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import org.aspectj.lang.JoinPoint;
import org.nstep.engine.framework.idempotent.core.annotation.Idempotent;
import org.nstep.engine.framework.idempotent.core.keyresolver.IdempotentKeyResolver;

/**
 * 默认（全局级别）幂等 Key 解析器，使用方法名 + 方法参数，组装成一个 Key
 * <p>
 * 为了避免 Key 过长，使用 MD5 进行“压缩”。
 * </p>
 * <p>
 * 该解析器通过方法名和方法参数来生成一个唯一的幂等性 Key。为了防止生成的 Key 过长，使用 MD5 对其进行加密。
 * </p>
 */
public class DefaultIdempotentKeyResolver implements IdempotentKeyResolver {

    /**
     * 解析一个幂等性 Key
     * <p>
     * 该方法通过方法名和方法参数来生成一个幂等性 Key。首先，获取方法签名和参数，然后将它们拼接在一起，
     * 最后使用 MD5 加密算法生成一个固定长度的 Key。
     * </p>
     *
     * @param joinPoint  AOP 切面，包含了方法签名和参数信息
     * @param idempotent {@link Idempotent} 注解，包含了幂等性相关的配置
     * @return 生成的幂等性 Key（经过 MD5 加密）
     */
    @Override
    public String resolver(JoinPoint joinPoint, Idempotent idempotent) {
        // 获取方法签名
        String methodName = joinPoint.getSignature().toString();
        // 获取方法参数并拼接成字符串
        String argsStr = StrUtil.join(",", joinPoint.getArgs());
        // 将方法名和参数拼接后，使用 MD5 加密生成唯一的幂等性 Key
        return SecureUtil.md5(methodName + argsStr);
    }
}
