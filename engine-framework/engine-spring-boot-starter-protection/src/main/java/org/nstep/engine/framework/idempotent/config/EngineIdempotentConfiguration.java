package org.nstep.engine.framework.idempotent.config;

import org.nstep.engine.framework.idempotent.core.aop.IdempotentAspect;
import org.nstep.engine.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import org.nstep.engine.framework.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import org.nstep.engine.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import org.nstep.engine.framework.idempotent.core.keyresolver.impl.UserIdempotentKeyResolver;
import org.nstep.engine.framework.idempotent.core.redis.IdempotentRedisDAO;
import org.nstep.engine.framework.redis.config.EngineRedisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@AutoConfiguration(after = EngineRedisAutoConfiguration.class)
public class EngineIdempotentConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        return new IdempotentAspect(keyResolvers, idempotentRedisDAO);
    }

    @Bean
    public IdempotentRedisDAO idempotentRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentRedisDAO(stringRedisTemplate);
    }

    // ========== 各种 IdempotentKeyResolver Bean ==========

    @Bean
    public DefaultIdempotentKeyResolver defaultIdempotentKeyResolver() {
        return new DefaultIdempotentKeyResolver();
    }

    @Bean
    public UserIdempotentKeyResolver userIdempotentKeyResolver() {
        return new UserIdempotentKeyResolver();
    }

    @Bean
    public ExpressionIdempotentKeyResolver expressionIdempotentKeyResolver() {
        return new ExpressionIdempotentKeyResolver();
    }

}
