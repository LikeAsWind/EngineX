package org.nstep.engine.module.system.api.tenant;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.service.tenant.TenantService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 租户 API 实现类
 * <p>
 * 该类提供了与租户相关的 RESTful API 接口，
 * 包括获取租户 ID 列表、验证租户有效性等功能。
 * 提供给 Feign 调用，处理租户相关的业务逻辑。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class TenantApiImpl implements TenantApi {

    @Resource
    private TenantService tenantService; // 租户服务，处理租户相关的业务逻辑

    /**
     * 获取租户 ID 列表
     *
     * @return 租户 ID 列表
     */
    @Override
    public CommonResult<List<Long>> getTenantIdList() {
        return success(tenantService.getTenantIdList());
    }

    /**
     * 验证租户是否有效
     *
     * @param id 租户 ID
     * @return 验证结果
     */
    @Override
    public CommonResult<Boolean> validTenant(Long id) {
        tenantService.validTenant(id);
        return success(true);
    }

}
