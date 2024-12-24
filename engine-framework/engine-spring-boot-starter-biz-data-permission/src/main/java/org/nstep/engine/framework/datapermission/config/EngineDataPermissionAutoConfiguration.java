package org.nstep.engine.framework.datapermission.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import org.nstep.engine.framework.datapermission.core.aop.DataPermissionAnnotationAdvisor;
import org.nstep.engine.framework.datapermission.core.db.DataPermissionRuleHandler;
import org.nstep.engine.framework.datapermission.core.rule.DataPermissionRule;
import org.nstep.engine.framework.datapermission.core.rule.DataPermissionRuleFactory;
import org.nstep.engine.framework.datapermission.core.rule.DataPermissionRuleFactoryImpl;
import org.nstep.engine.framework.mybatis.core.util.MyBatisUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 数据权限的自动配置类
 */
@AutoConfiguration
public class EngineDataPermissionAutoConfiguration {

    /**
     * 创建数据权限规则工厂的Bean
     *
     * @param rules 一个包含所有数据权限规则的列表
     * @return 返回一个数据权限规则工厂实例
     */
    @Bean
    public DataPermissionRuleFactory dataPermissionRuleFactory(List<DataPermissionRule> rules) {
        return new DataPermissionRuleFactoryImpl(rules);
    }

    /**
     * 创建数据权限规则处理器的Bean
     *
     * @param interceptor MyBatis Plus拦截器
     * @param ruleFactory 数据权限规则工厂
     * @return 返回一个数据权限规则处理器实例
     */
    @Bean
    public DataPermissionRuleHandler dataPermissionRuleHandler(MybatisPlusInterceptor interceptor, DataPermissionRuleFactory ruleFactory) {
        // 创建 DataPermissionInterceptor 拦截器
        DataPermissionRuleHandler handler = new DataPermissionRuleHandler(ruleFactory);
        DataPermissionInterceptor inner = new DataPermissionInterceptor(handler);
        // 添加到 interceptor 中
        // 需要加在首个，主要是为了在分页插件前面。这个是 MyBatis Plus 的规定
        MyBatisUtils.addInterceptor(interceptor, inner, 0);
        return handler;
    }

    /**
     * 创建数据权限注解顾问的Bean
     *
     * @return 返回一个数据权限注解顾问实例
     */
    @Bean
    public DataPermissionAnnotationAdvisor dataPermissionAnnotationAdvisor() {
        return new DataPermissionAnnotationAdvisor();
    }

}