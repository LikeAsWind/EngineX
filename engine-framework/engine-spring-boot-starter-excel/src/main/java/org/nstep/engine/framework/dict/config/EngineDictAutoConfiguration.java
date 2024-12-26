package org.nstep.engine.framework.dict.config;

import org.nstep.engine.framework.dict.core.DictFrameworkUtils;
import org.nstep.engine.module.system.api.dict.DictDataApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 字典框架的自动配置类。
 * 通过自动配置，初始化字典工具类 {@link DictFrameworkUtils}，并将其作为 Spring Bean 提供给应用程序。
 */
@AutoConfiguration
public class EngineDictAutoConfiguration {

    /**
     * 配置字典工具类 {@link DictFrameworkUtils} 的 Spring Bean。
     *
     * @param dictDataApi 字典数据的 API 接口实现，用于初始化字典工具类
     * @return 初始化后的 {@link DictFrameworkUtils} 实例
     */
    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass") // 抑制警告，因为工具类通常不需要实例化
    public DictFrameworkUtils dictUtils(@Qualifier("org.nstep.engine.module.system.api.dict.DictDataApi") DictDataApi dictDataApi) {
        // 调用工具类的初始化方法，将字典数据 API 注入到工具类中
        DictFrameworkUtils.init(dictDataApi);
        // 返回工具类实例
        return new DictFrameworkUtils();
    }
}