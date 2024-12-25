package org.nstep.engine.module.infra.framework.file.config;

import org.nstep.engine.module.infra.framework.file.core.client.FileClientFactory;
import org.nstep.engine.module.infra.framework.file.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 * <p>
 * 该类用于配置文件相关的 Bean，主要用于初始化文件客户端工厂。
 * 通过@Configuration注解将该类标记为配置类，Spring会自动扫描并加载配置。
 * </p>
 */
@Configuration(proxyBeanMethods = false)  // 标记该类为配置类，proxyBeanMethods = false 表示不代理 Bean 方法，优化性能
public class EngineFileAutoConfiguration {

    /**
     * 创建并返回一个 FileClientFactory 实例
     * <p>
     * 该方法用于初始化 FileClientFactory 的实现类 FileClientFactoryImpl。
     * 返回的实例将会被 Spring 容器管理，并作为 Bean 提供给其他组件使用。
     * </p>
     *
     * @return FileClientFactory 实例
     */
    @Bean  // 标记该方法为 Bean 定义方法，Spring 会自动将返回的对象注册为 Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();  // 返回 FileClientFactoryImpl 实例
    }

}
