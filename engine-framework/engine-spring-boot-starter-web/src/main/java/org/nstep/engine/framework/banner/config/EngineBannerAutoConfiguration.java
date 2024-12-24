package org.nstep.engine.framework.banner.config;

import org.nstep.engine.framework.banner.core.BannerApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Banner 的自动配置类
 * <p>
 * 该类负责在 Spring Boot 应用启动时自动配置一个 BannerApplicationRunner Bean，
 * 用于在应用启动时显示自定义的 Banner 信息。
 * 通过 Spring Boot 的自动配置机制，确保 Banner 功能在应用启动时自动生效。
 */
@AutoConfiguration // 标记为自动配置类，Spring Boot 会自动加载该配置
public class EngineBannerAutoConfiguration {

    /**
     * 创建一个 BannerApplicationRunner Bean
     * <p>
     * BannerApplicationRunner 负责在应用启动时显示自定义的 Banner 信息。
     *
     * @return 返回一个 BannerApplicationRunner 实例
     */
    @Bean
    public BannerApplicationRunner bannerApplicationRunner() {
        return new BannerApplicationRunner(); // 返回一个新的 BannerApplicationRunner 实例
    }

}
