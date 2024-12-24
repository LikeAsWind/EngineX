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

//确保 EngineIdempotentConfiguration 在 EngineRedisAutoConfiguration 加载后才会加载，从而保证 Redis 配置先行，避免依赖关系的错误。
@AutoConfiguration(after = EngineRedisAutoConfiguration.class)
public class EngineIdempotentConfiguration {

    /**
     * 创建 IdempotentAspect Bean，负责处理幂等性相关的切面逻辑。
     * <p>
     * 该切面通过注入的 keyResolvers 处理不同的幂等性键解析器，并通过 idempotentRedisDAO
     * 操作 Redis 存储，确保接口的幂等性。
     *
     * @param keyResolvers       各种幂等性键解析器
     * @param idempotentRedisDAO 用于操作 Redis 的 DAO
     * @return 返回一个 IdempotentAspect 实例
     */
    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        // 创建并返回 IdempotentAspect 实例
        return new IdempotentAspect(keyResolvers, idempotentRedisDAO);
    }

    /**
     * 创建 IdempotentRedisDAO Bean，提供与 Redis 的交互接口，用于存储和查询幂等性数据。
     * <p>
     * 该 DAO 使用 StringRedisTemplate 来操作 Redis 数据库，存储与接口幂等性相关的数据。
     *
     * @param stringRedisTemplate Redis 操作模板
     * @return 返回一个 IdempotentRedisDAO 实例
     */
    @Bean
    public IdempotentRedisDAO idempotentRedisDAO(StringRedisTemplate stringRedisTemplate) {
        // 创建并返回 IdempotentRedisDAO 实例
        return new IdempotentRedisDAO(stringRedisTemplate);
    }

    // ========== 各种 IdempotentKeyResolver Bean ==========

    /**
     * 创建 DefaultIdempotentKeyResolver Bean，默认的幂等性键解析器。
     * <p>
     * 该解析器用于生成默认的幂等性键，通常用于没有特定规则的接口。
     *
     * @return 返回一个 DefaultIdempotentKeyResolver 实例
     */
    @Bean
    public DefaultIdempotentKeyResolver defaultIdempotentKeyResolver() {
        // 创建并返回 DefaultIdempotentKeyResolver 实例
        return new DefaultIdempotentKeyResolver();
    }

    /**
     * 创建 UserIdempotentKeyResolver Bean，专门为用户相关的接口生成幂等性键。
     * <p>
     * 该解析器根据用户的标识（如用户ID）生成幂等性键，适用于与用户相关的幂等性操作。
     *
     * @return 返回一个 UserIdempotentKeyResolver 实例
     */
    @Bean
    public UserIdempotentKeyResolver userIdempotentKeyResolver() {
        // 创建并返回 UserIdempotentKeyResolver 实例
        return new UserIdempotentKeyResolver();
    }

    /**
     * 创建 ExpressionIdempotentKeyResolver Bean，支持根据表达式生成幂等性键。
     * <p>
     * 该解析器通过传入的表达式（如 SpEL 表达式）来动态生成幂等性键，适用于复杂的幂等性需求。
     *
     * @return 返回一个 ExpressionIdempotentKeyResolver 实例
     */
    @Bean
    public ExpressionIdempotentKeyResolver expressionIdempotentKeyResolver() {
        // 创建并返回 ExpressionIdempotentKeyResolver 实例
        return new ExpressionIdempotentKeyResolver();
    }

}
