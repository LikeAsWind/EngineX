package org.nstep.engine.framework.signature.core.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * HTTP API 签名 Redis DAO
 */
@AllArgsConstructor
public class ApiSignatureRedisDAO {

    /**
     * 验签随机数
     * <p>
     * KEY 格式：signature_nonce:%s // 参数为 随机数
     * VALUE 格式：String
     * 过期时间：不固定
     */
    private static final String SIGNATURE_NONCE = "api_signature_nonce:%s:%s";
    /**
     * 签名密钥
     * <p>
     * HASH 结构
     * KEY 格式：%s // 参数为 appid
     * VALUE 格式：String
     * 过期时间：永不过期（预加载到 Redis）
     */
    private static final String SIGNATURE_APPID = "api_signature_app";
    private final StringRedisTemplate stringRedisTemplate;

    // ========== 验签随机数 ==========

    private static String formatNonceKey(String appId, String nonce) {
        return String.format(SIGNATURE_NONCE, appId, nonce);
    }

    public String getNonce(String appId, String nonce) {
        return stringRedisTemplate.opsForValue().get(formatNonceKey(appId, nonce));
    }

    public void setNonce(String appId, String nonce, int time, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(formatNonceKey(appId, nonce), "", time, timeUnit);
    }

    // ========== 签名密钥 ==========

    public String getAppSecret(String appId) {
        return (String) stringRedisTemplate.opsForHash().get(SIGNATURE_APPID, appId);
    }

}