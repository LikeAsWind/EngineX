package org.nstep.engine.framework.mybatis.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.*;
import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.baomidou.mybatisplus.extension.parser.cache.JdkSerialCaffeineJsqlParseCache;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.nstep.engine.framework.mybatis.core.handler.DefaultDBFieldHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.concurrent.TimeUnit;

/**
 * MyBatis 配置类
 * <p>
 * 该类用于配置 MyBatis 和 MyBatis Plus，包括分页插件、自动填充、主键生成器等。
 * 并且确保 MyBatis Plus 的配置在该类配置之前加载，以避免 @MapperScan 可能扫描不到 Mapper。
 */
@AutoConfiguration(before = MybatisPlusAutoConfiguration.class)
// 目的：先于 MyBatis Plus 自动配置，避免 @MapperScan 可能扫描不到 Mapper 打印 warn 日志
@MapperScan(value = "${engine.info.base-package}", annotationClass = Mapper.class,
        lazyInitialization = "${mybatis.lazy-initialization:false}") // Mapper 懒加载，目前仅用于单元测试
public class EngineMybatisAutoConfiguration {

    static {
        // 动态 SQL 智能优化支持本地缓存加速解析，更完善的租户复杂 XML 动态 SQL 支持，静态注入缓存
        // 设置 JsqlParser 的缓存，以加速 SQL 解析过程
        JsqlParserGlobal.setJsqlParseCache(new JdkSerialCaffeineJsqlParseCache(
                (cache) -> cache.maximumSize(1024) // 设置缓存最大数量为 1024
                        .expireAfterWrite(5, TimeUnit.SECONDS)) // 设置缓存过期时间为 5 秒
        );
    }

    /**
     * 配置 MyBatis Plus 的拦截器
     * <p>
     * 该方法为 MyBatis Plus 添加了分页插件，用于支持分页查询。
     *
     * @return MybatisPlusInterceptor 配置的 MyBatis Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // 分页插件
        return mybatisPlusInterceptor;
    }

    /**
     * 配置 MyBatis 的 MetaObjectHandler
     * <p>
     * 该方法配置了自动填充字段处理器，用于自动填充数据库中的字段（如创建时间、更新时间等）。
     *
     * @return MetaObjectHandler 配置的自动填充处理器
     */
    @Bean
    public MetaObjectHandler defaultMetaObjectHandler() {
        return new DefaultDBFieldHandler(); // 自动填充参数类
    }

    /**
     * 配置主键生成器
     * <p>
     * 根据数据库类型选择合适的主键生成器，实现对主键生成策略的定制化。
     * 通过读取数据库类型来决定使用哪种主键生成器。
     *
     * @param environment Spring 环境，用于获取当前数据库类型
     * @return IKeyGenerator 主键生成器
     * @throws IllegalArgumentException 如果找不到合适的主键生成器实现，抛出异常
     */
    @Bean
    @ConditionalOnProperty(prefix = "mybatis-plus.global-config.db-config", name = "id-type", havingValue = "INPUT")
    public IKeyGenerator keyGenerator(ConfigurableEnvironment environment) {
        // 获取数据库类型
        DbType dbType = IdTypeEnvironmentPostProcessor.getDbType(environment);

        if (dbType != null) {
            // 根据不同的数据库类型返回相应的主键生成器
            switch (dbType) {
                case POSTGRE_SQL:
                    return new PostgreKeyGenerator(); // PostgreSQL 主键生成器
                case ORACLE:
                case ORACLE_12C:
                    return new OracleKeyGenerator(); // Oracle 主键生成器
                case H2:
                    return new H2KeyGenerator(); // H2 主键生成器
                case KINGBASE_ES:
                    return new KingbaseKeyGenerator(); // Kingbase 主键生成器
                case DM:
                    return new DmKeyGenerator(); // DM 主键生成器
            }
        }

        // 如果无法匹配数据库类型，则抛出异常
        throw new IllegalArgumentException(StrUtil.format("DbType{} 找不到合适的 IKeyGenerator 实现类", dbType));
    }
}
