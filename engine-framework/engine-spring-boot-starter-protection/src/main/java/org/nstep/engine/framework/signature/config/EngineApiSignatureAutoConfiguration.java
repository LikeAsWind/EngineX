package org.nstep.engine.framework.signature.config;

import org.nstep.engine.framework.redis.config.EngineRedisAutoConfiguration;
import org.nstep.engine.framework.signature.core.aop.ApiSignatureAspect;
import org.nstep.engine.framework.signature.core.redis.ApiSignatureRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * HTTP API 签名的自动配置类
 */
@AutoConfiguration(after = EngineRedisAutoConfiguration.class)
public class EngineApiSignatureAutoConfiguration {

    @Bean
    public ApiSignatureAspect signatureAspect(ApiSignatureRedisDAO signatureRedisDAO) {
        return new ApiSignatureAspect(signatureRedisDAO);
    }

    @Bean
    public ApiSignatureRedisDAO signatureRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new ApiSignatureRedisDAO(stringRedisTemplate);
    }

}
