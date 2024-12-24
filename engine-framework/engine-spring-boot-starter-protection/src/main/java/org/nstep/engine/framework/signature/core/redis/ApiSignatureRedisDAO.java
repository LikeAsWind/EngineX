package org.nstep.engine.framework.signature.core.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * HTTP API 签名 Redis 数据访问对象（DAO）
 * <p>
 * 该类负责与 Redis 交互，提供签名验证相关的操作，包括获取和存储签名密钥、存储和获取随机数（nonce）等。
 */
@AllArgsConstructor
public class ApiSignatureRedisDAO {

    /**
     * 验签随机数的 Redis 键格式。
     * <p>
     * KEY 格式：signature_nonce:%s // 参数为 随机数
     * VALUE 格式：String
     * 过期时间：不固定
     */
    private static final String SIGNATURE_NONCE = "api_signature_nonce:%s:%s";

    /**
     * 签名密钥的 Redis 键格式。
     * <p>
     * HASH 结构
     * KEY 格式：%s // 参数为 appid
     * VALUE 格式：String
     * 过期时间：永不过期（预加载到 Redis）
     */
    private static final String SIGNATURE_APPID = "api_signature_app";

    // Redis 操作模板
    private final StringRedisTemplate stringRedisTemplate;

    // ========== 验签随机数 ==========

    /**
     * 格式化 Redis 键，用于存储和获取随机数（nonce）的值。
     *
     * @param appId 应用 ID
     * @param nonce 随机数
     * @return 格式化后的 Redis 键
     */
    private static String formatNonceKey(String appId, String nonce) {
        return String.format(SIGNATURE_NONCE, appId, nonce); // 格式化为 Redis 键
    }

    /**
     * 获取指定 appId 和 nonce 的 Redis 存储值。
     *
     * @param appId 应用 ID
     * @param nonce 随机数
     * @return Redis 中存储的值（如果存在），否则返回 null
     */
    public String getNonce(String appId, String nonce) {
        return stringRedisTemplate.opsForValue().get(formatNonceKey(appId, nonce)); // 获取 Redis 中存储的值
    }

    /**
     * 设置指定 appId 和 nonce 的 Redis 存储值，并指定过期时间。
     *
     * @param appId    应用 ID
     * @param nonce    随机数
     * @param time     过期时间
     * @param timeUnit 过期时间单位
     */
    public void setNonce(String appId, String nonce, int time, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(formatNonceKey(appId, nonce), "", time, timeUnit); // 设置 Redis 中的值，并指定过期时间
    }

    // ========== 签名密钥 ==========

    /**
     * 获取指定 appId 的签名密钥。
     *
     * @param appId 应用 ID
     * @return 返回该 appId 对应的签名密钥，如果不存在则返回 null
     */
    public String getAppSecret(String appId) {
        return (String) stringRedisTemplate.opsForHash().get(SIGNATURE_APPID, appId); // 从 Redis 哈希中获取 appSecret
    }

}
