package org.nstep.engine.framework.translate.config;

import com.fhs.trans.service.impl.TransService;
import org.nstep.engine.framework.translate.core.TranslateUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 自动配置类，用于配置翻译工具类 TranslateUtils。
 */
@AutoConfiguration
public class EngineTranslateAutoConfiguration {

    /**
     * 创建 TranslateUtils 的 Bean，并初始化它。
     *
     * @param transService 翻译服务，用于初始化 TranslateUtils
     * @return TranslateUtils 实例
     */
    @Bean
    @SuppressWarnings({"InstantiationOfUtilityClass", "SpringJavaInjectionPointsAutowiringInspection"})
    public TranslateUtils translateUtils(TransService transService) {
        // 初始化 TranslateUtils，传入 TransService 进行配置
        TranslateUtils.init(transService);

        // 返回 TranslateUtils 的实例
        return new TranslateUtils();
    }
}
