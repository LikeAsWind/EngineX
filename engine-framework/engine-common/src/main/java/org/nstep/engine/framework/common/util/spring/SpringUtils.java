package org.nstep.engine.framework.common.util.spring;

import cn.hutool.extra.spring.SpringUtil;

import java.util.Objects;

/**
 * Spring 工具类
 * <p>
 * 继承自 {@link SpringUtil}，封装了一些常用的 Spring 环境相关的工具方法。
 * 本类提供了判断当前是否为生产环境的方法。
 */
public class SpringUtils extends SpringUtil {

    /**
     * 是否为生产环境
     * <p>
     * 通过 Spring 配置文件中的 `spring.profiles.active` 属性判断当前是否为生产环境。
     *
     * @return 如果当前环境为生产环境，则返回 true；否则返回 false
     */
    public static boolean isNonProductionEnvironment() {
        // 获取当前激活的 Spring 配置文件（环境）
        String activeProfile = getActiveProfile();
        // 判断是否为 "prod" 环境
        return Objects.equals("prod", activeProfile);
    }

}
