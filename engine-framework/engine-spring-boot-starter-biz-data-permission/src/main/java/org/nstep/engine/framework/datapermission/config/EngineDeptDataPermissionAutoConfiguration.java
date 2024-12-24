package org.nstep.engine.framework.datapermission.config;

// 导入所需的类

import cn.hutool.extra.spring.SpringUtil;
import org.nstep.engine.framework.datapermission.core.rule.dept.DeptDataPermissionRule;
import org.nstep.engine.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import org.nstep.engine.framework.security.core.LoginUser;
import org.nstep.engine.module.system.api.permission.PermissionApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 基于部门的数据权限自动配置类
 * 标记这是一个自动配置类，Spring Boot在启动时会检测并自动配置这个类
 */
@AutoConfiguration
// 条件注解，当类路径下存在LoginUser类时，这个配置类才会被注册
@ConditionalOnClass(LoginUser.class)
// 条件注解，当容器中存在DeptDataPermissionRuleCustomizer类型的Bean时，这个配置类才会被注册
@ConditionalOnBean(value = DeptDataPermissionRuleCustomizer.class)
public class EngineDeptDataPermissionAutoConfiguration {

    /**
     * 创建基于部门的数据权限规则的Bean
     *
     * @param permissionApi 权限API接口
     * @param customizers   一个自定义配置器列表，用于自定义部门数据权限规则
     * @return 返回一个DeptDataPermissionRule实例
     */
    @Bean
    public DeptDataPermissionRule deptDataPermissionRule(@Qualifier("org.nstep.engine.module.system.api.permission.PermissionApi") PermissionApi permissionApi,
                                                         List<DeptDataPermissionRuleCustomizer> customizers) {
        // Cloud 专属逻辑：优先使用本地的 PermissionApi 实现类，而不是Feign调用
        // 原因：在创建租户时，租户还没创建好，导致Feign调用获取数据权限时，报“租户不存在”的错误
        try {
            PermissionApi permissionApiImpl = SpringUtil.getBean("permissionApiImpl", PermissionApi.class);
            if (permissionApiImpl != null) {
                permissionApi = permissionApiImpl;
            }
        } catch (Exception ignored) {
        }

        // 创建 DeptDataPermissionRule 对象
        DeptDataPermissionRule rule = new DeptDataPermissionRule(permissionApi);
        // 补全表配置
        customizers.forEach(customizer -> customizer.customize(rule));
        return rule;
    }

}