package org.nstep.engine.framework.signature.config;

import org.nstep.engine.framework.redis.config.EngineRedisAutoConfiguration;
import org.nstep.engine.framework.signature.core.aop.ApiSignatureAspect;
import org.nstep.engine.framework.signature.core.redis.ApiSignatureRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * HTTP API 签名的自动配置类
 * <p>
 * 该类用于自动配置 HTTP API 签名功能。它通过 `ApiSignatureAspect` 和 `ApiSignatureRedisDAO` 提供 API 请求签名的拦截和存储。
 * 配置类会在 Redis 配置之后加载，确保相关的 Redis 操作可以在 API 签名功能中正常工作。
 */
@AutoConfiguration(after = EngineRedisAutoConfiguration.class)
public class EngineApiSignatureAutoConfiguration {

    /**
     * 创建 API 签名切面
     * <p>
     * 该方法返回一个 `ApiSignatureAspect` 实例，该切面用于拦截带有 API 签名注解的方法，进行签名校验。
     *
     * @param signatureRedisDAO 用于与 Redis 交互的 API 签名 DAO
     * @return ApiSignatureAspect 实例
     */
    @Bean
    public ApiSignatureAspect signatureAspect(ApiSignatureRedisDAO signatureRedisDAO) {
        return new ApiSignatureAspect(signatureRedisDAO);
    }

    /**
     * 创建 API 签名 Redis DAO
     * <p>
     * 该方法返回一个 `ApiSignatureRedisDAO` 实例，用于与 Redis 进行交互，存储和获取 API 签名相关数据。
     *
     * @param stringRedisTemplate 用于与 Redis 交互的模板
     * @return ApiSignatureRedisDAO 实例
     */
    @Bean
    public ApiSignatureRedisDAO signatureRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new ApiSignatureRedisDAO(stringRedisTemplate);
    }

}
