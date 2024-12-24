package org.nstep.engine.framework.tenant.core.service;

import java.util.List;

/**
 * Tenant 框架 Service 接口，定义获取租户信息
 * <p>
 * 该接口用于多租户架构中提供租户相关的服务，包括获取所有租户的编号和校验租户的合法性。
 * 主要功能：
 * 1. 获取系统中所有租户的租户编号。
 * 2. 校验指定租户编号是否有效。
 */
public interface TenantFrameworkService {

    /**
     * 获得所有租户
     * <p>
     * 该方法用于获取系统中所有租户的租户编号，通常用于需要对所有租户进行全局操作的场景。
     *
     * @return 租户编号数组，包含所有租户的 ID。
     */
    List<Long> getTenantIds();

    /**
     * 校验租户是否合法
     * <p>
     * 该方法用于校验指定的租户编号是否有效，例如检查租户是否被禁用、是否过期等。
     *
     * @param id 租户编号，传入需要校验的租户 ID。
     */
    void validTenant(Long id);

}
