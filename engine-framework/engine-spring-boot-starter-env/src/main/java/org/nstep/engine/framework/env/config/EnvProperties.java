package org.nstep.engine.framework.env.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 环境配置类，用于绑定 `engine.env` 配置前缀下的属性。
 * <p>
 * 该类通过 `@ConfigurationProperties` 注解，自动将配置文件中以 `engine.env` 为前缀的配置项绑定到该类的字段上。
 * 在此类中，主要配置项为环境标签 `tag`，它用于标识不同的环境（例如：开发环境、生产环境等）。
 */
@ConfigurationProperties(prefix = "engine.env")
@Data // 自动生成 getter、setter、toString、equals 和 hashCode 方法
public class EnvProperties {

    // 定义常量 TAG_KEY，用于表示环境标签的配置项键
    public static final String TAG_KEY = "engine.env.tag";

    /**
     * 环境标签，用于标识当前环境。
     * <p>
     * 该属性会绑定到配置文件中的 `engine.env.tag` 配置项，表示应用的当前环境标签。
     * 例如，可以设置为 "dev"、"prod" 等，作为不同环境的标识。
     */
    private String tag;

}
