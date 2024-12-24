package org.nstep.engine.framework.idempotent.core.keyresolver.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import org.aspectj.lang.JoinPoint;
import org.nstep.engine.framework.idempotent.core.annotation.Idempotent;
import org.nstep.engine.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import org.nstep.engine.framework.web.core.util.WebFrameworkUtils;

/**
 * 用户级别的幂等 Key 解析器
 * <p>
 * 该解析器生成的幂等性 Key 由方法名、方法参数、用户 ID 和用户类型组成。通过将这些信息组合成一个唯一的 Key，确保同一个用户的重复请求能够被正确识别和处理。
 * 为了避免 Key 过长，使用 MD5 进行“压缩”。
 * </p>
 */
public class UserIdempotentKeyResolver implements IdempotentKeyResolver {

    /**
     * 解析幂等性 Key
     * <p>
     * 该方法将方法名、方法参数、当前登录用户的 ID 和类型组合起来，生成一个用户级别的幂等性 Key。
     * 为了避免 Key 过长，使用 MD5 进行“压缩”。
     * </p>
     *
     * @param joinPoint  AOP 切面对象，包含了方法签名和参数信息
     * @param idempotent {@link Idempotent} 注解，包含了幂等性相关的配置
     * @return 生成的幂等性 Key
     */
    @Override
    public String resolver(JoinPoint joinPoint, Idempotent idempotent) {
        // 获取方法名
        String methodName = joinPoint.getSignature().toString();
        // 获取方法参数的字符串表示
        String argsStr = StrUtil.join(",", joinPoint.getArgs());
        // 获取当前登录用户的 ID 和类型
        Long userId = WebFrameworkUtils.getLoginUserId();
        Integer userType = WebFrameworkUtils.getLoginUserType();
        // 生成并返回 MD5 压缩后的幂等性 Key
        return SecureUtil.md5(methodName + argsStr + userId + userType);
    }

}
