package org.nstep.engine.framework.env.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 环境配置
 */
@ConfigurationProperties(prefix = "engine.env")
@Data
public class EnvProperties {

    public static final String TAG_KEY = "engine.env.tag";

    /**
     * 环境标签
     */
    private String tag;

}
