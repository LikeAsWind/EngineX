package org.nstep.engine.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文档地址
 */
@Getter
@AllArgsConstructor
public enum DocumentEnum {

    REDIS_INSTALL("https://gitee.com/zhijiantianya/engine/issues/I4VCSJ", "Redis 安装文档");

    private final String url;
    private final String memo;

}
