package org.nstep.engine.framework.signature.core.aop;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.nstep.engine.framework.common.exception.ServiceException;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.nstep.engine.framework.signature.core.annotation.ApiSignature;
import org.nstep.engine.framework.signature.core.redis.ApiSignatureRedisDAO;

import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 拦截声明了 {@link ApiSignature} 注解的方法，实现签名验证。
 * 该切面类用于在方法执行前验证 HTTP 请求的签名是否合法。
 */
@Aspect
@Slf4j
@AllArgsConstructor
public class ApiSignatureAspect {

    private final ApiSignatureRedisDAO signatureRedisDAO; // 用于从 Redis 中获取和存储签名相关的数据

    /**
     * 获取请求头中的加签参数并返回一个排序的 Map。
     *
     * @param signature 签名注解，用于获取签名相关的字段名称
     * @param request   请求对象
     * @return 返回一个包含签名参数的 SortedMap，按字典顺序排序
     */
    private static SortedMap<String, String> getRequestHeaderMap(ApiSignature signature, HttpServletRequest request) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put(signature.appId(), request.getHeader(signature.appId())); // 获取 appId
        sortedMap.put(signature.timestamp(), request.getHeader(signature.timestamp())); // 获取 timestamp
        sortedMap.put(signature.nonce(), request.getHeader(signature.nonce())); // 获取 nonce
        return sortedMap;
    }

    /**
     * 获取请求参数并返回一个排序的 Map。
     *
     * @param request 请求对象
     * @return 返回一个包含请求参数的 SortedMap，按字典顺序排序
     */
    private static SortedMap<String, String> getRequestParameterMap(HttpServletRequest request) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            sortedMap.put(entry.getKey(), entry.getValue()[0]); // 获取请求参数并取第一个值
        }
        return sortedMap;
    }

    /**
     * 切面方法，在目标方法执行前执行，验证请求的签名是否合法。
     * 如果签名验证通过，则继续执行目标方法；否则抛出异常。
     *
     * @param joinPoint 连接点，包含方法签名和参数
     * @param signature {@link ApiSignature} 注解实例
     */
    @Before("@annotation(signature)")
    public void beforePointCut(JoinPoint joinPoint, ApiSignature signature) {
        // 1. 验证签名，若验证通过，则直接返回
        if (verifySignature(signature, Objects.requireNonNull(ServletUtils.getRequest()))) {
            return;
        }

        // 2. 验证失败，记录日志并抛出异常
        log.error("[beforePointCut][方法{} 参数({}) 签名失败]", joinPoint.getSignature().toString(),
                joinPoint.getArgs());
        throw new ServiceException(BAD_REQUEST.getCode(),
                StrUtil.blankToDefault(signature.message(), BAD_REQUEST.getMsg()));
    }

    /**
     * 验证请求的签名是否合法。
     *
     * @param signature {@link ApiSignature} 注解实例
     * @param request   请求对象
     * @return 返回是否签名验证通过
     */
    public boolean verifySignature(ApiSignature signature, HttpServletRequest request) {
        // 1. 校验请求头中的签名参数是否合法
        if (!verifyHeaders(signature, request)) {
            return false;
        }

        // 2. 校验 appId 是否能获取到对应的 appSecret
        String appId = request.getHeader(signature.appId());
        String appSecret = signatureRedisDAO.getAppSecret(appId);
        Assert.notNull(appSecret, "[appId({})] 找不到对应的 appSecret", appId);

        // 3. 校验签名是否合法
        String clientSignature = request.getHeader(signature.sign()); // 获取客户端传递的签名
        String serverSignatureString = buildSignatureString(signature, request, appSecret); // 构建服务端签名字符串
        String serverSignature = DigestUtil.sha256Hex(serverSignatureString); // 计算服务端签名
        if (ObjUtil.notEqual(clientSignature, serverSignature)) {
            return false; // 如果客户端签名与服务端签名不一致，则验证失败
        }

        // 4. 将 nonce 存入 Redis 缓存，防止重复使用
        String nonce = request.getHeader(signature.nonce());
        signatureRedisDAO.setNonce(appId, nonce, signature.timeout() * 2, signature.timeUnit());
        return true; // 签名验证通过
    }

    /**
     * 校验请求头中的加签参数是否合法。
     * <p>
     * 1. 校验 appId 是否为空
     * 2. 校验 timestamp 是否为空，是否超时
     * 3. 校验 nonce 是否为空，是否重复使用
     * 4. 校验 sign 是否为空
     *
     * @param signature {@link ApiSignature} 注解实例
     * @param request   请求对象
     * @return 返回是否请求头参数校验通过
     */
    private boolean verifyHeaders(ApiSignature signature, HttpServletRequest request) {
        // 1. 非空校验
        String appId = request.getHeader(signature.appId());
        if (StrUtil.isBlank(appId)) {
            return false; // appId 为空，验证失败
        }
        String timestamp = request.getHeader(signature.timestamp());
        if (StrUtil.isBlank(timestamp)) {
            return false; // timestamp 为空，验证失败
        }
        String nonce = request.getHeader(signature.nonce());
        if (StrUtil.length(nonce) < 10) {
            return false; // nonce 长度小于 10，验证失败
        }
        String sign = request.getHeader(signature.sign());
        if (StrUtil.isBlank(sign)) {
            return false; // sign 为空，验证失败
        }

        // 2. 校验 timestamp 是否超出允许的范围
        long expireTime = signature.timeUnit().toMillis(signature.timeout()); // 转换为毫秒
        long requestTimestamp = Long.parseLong(timestamp); // 获取请求中的时间戳
        long timestampDisparity = Math.abs(System.currentTimeMillis() - requestTimestamp); // 计算时间差
        if (timestampDisparity > expireTime) {
            return false; // 如果时间差超过允许范围，验证失败
        }

        // 3. 校验 nonce 是否已使用过
        return signatureRedisDAO.getNonce(appId, nonce) == null; // 如果 nonce 已存在，则验证失败
    }

    /**
     * 构建用于计算签名的字符串。
     * <p>
     * 签名字符串的格式为：请求参数 + 请求体 + 请求头 + 密钥
     *
     * @param signature {@link ApiSignature} 注解实例
     * @param request   请求对象
     * @param appSecret 应用密钥
     * @return 返回构建的签名字符串
     */
    private String buildSignatureString(ApiSignature signature, HttpServletRequest request, String appSecret) {
        SortedMap<String, String> parameterMap = getRequestParameterMap(request); // 获取请求参数
        SortedMap<String, String> headerMap = getRequestHeaderMap(signature, request); // 获取请求头
        String requestBody = StrUtil.nullToDefault(ServletUtils.getBody(request), ""); // 获取请求体
        // 拼接请求参数、请求体、请求头和密钥，构建签名字符串
        return MapUtil.join(parameterMap, "&", "=")
                + requestBody
                + MapUtil.join(headerMap, "&", "=")
                + appSecret;
    }
}
