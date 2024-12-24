package org.nstep.engine.framework.datasource.core.enums;

/**
 * 对应于多数据源中不同数据源配置
 * <p>
 * 通过在方法上，使用 {@link com.baomidou.dynamic.datasource.annotation.DS} 注解，设置使用的数据源。
 * 注意，默认是 {@link #MASTER} 数据源
 * <p>
 * 对应官方文档为 <a href=" http://dynamic-datasource.com/guide/customize/Annotation.html"></a>
 */
public interface DataSourceEnum {

    /**
     * 主库，推荐使用 {@link com.baomidou.dynamic.datasource.annotation.Master} 注解
     * <p>
     * 在多数据源配置中，主库通常用于写操作，推荐使用 {@link com.baomidou.dynamic.datasource.annotation.Master} 注解来标识主库。
     */
    String MASTER = "master";

    /**
     * 从库，推荐使用 {@link com.baomidou.dynamic.datasource.annotation.Slave} 注解
     * <p>
     * 从库通常用于读操作，推荐使用 {@link com.baomidou.dynamic.datasource.annotation.Slave} 注解来标识从库。
     */
    String SLAVE = "slave";

}
