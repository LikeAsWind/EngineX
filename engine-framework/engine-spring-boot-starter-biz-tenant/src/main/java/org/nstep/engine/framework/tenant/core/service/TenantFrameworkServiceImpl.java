package org.nstep.engine.framework.tenant.core.service;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.tenant.TenantApi;

import java.time.Duration;
import java.util.List;

import static org.nstep.engine.framework.common.util.cache.CacheUtils.buildAsyncReloadingCache;

/**
 * Tenant 框架 Service 实现类
 * <p>
 * 该类实现了 TenantFrameworkService 接口，提供了获取所有租户信息和校验租户合法性的功能。
 * 使用了缓存机制来优化性能，避免频繁调用外部接口。
 */
@RequiredArgsConstructor
public class TenantFrameworkServiceImpl implements TenantFrameworkService {

    private final TenantApi tenantApi;  // 外部接口，用于获取租户信息

    /**
     * 针对 {@link #getTenantIds()} 的缓存
     * <p>
     * 使用 LoadingCache 来缓存所有租户的 ID 列表，缓存过期时间为 1 分钟。
     * 这样可以减少对外部接口的频繁请求，提高性能。
     */
    private final LoadingCache<Object, List<Long>> getTenantIdsCache = buildAsyncReloadingCache(
            Duration.ofMinutes(1L), // 过期时间 1 分钟
            new CacheLoader<>() {

                @NotNull
                @Override
                public List<Long> load(@NotNull Object key) {
                    return tenantApi.getTenantIdList().getCheckedData(); // 从外部接口获取租户 ID 列表
                }

            });

    /**
     * 针对 {@link #validTenant(Long)} 的缓存
     * <p>
     * 使用 LoadingCache 来缓存租户合法性校验的结果，缓存过期时间为 1 分钟。
     * 这样可以避免频繁校验同一租户，提升性能。
     */
    private final LoadingCache<Long, CommonResult<Boolean>> validTenantCache = buildAsyncReloadingCache(
            Duration.ofMinutes(1L), // 过期时间 1 分钟
            new CacheLoader<>() {

                @NotNull
                @Override
                public CommonResult<Boolean> load(@NotNull Long id) {
                    return tenantApi.validTenant(id); // 校验租户是否合法
                }

            });

    /**
     * 获取所有租户的租户 ID 列表
     * <p>
     * 通过缓存机制优化性能，避免每次都调用外部接口获取租户列表。
     *
     * @return 租户 ID 列表
     */
    @Override
    @SneakyThrows
    public List<Long> getTenantIds() {
        return getTenantIdsCache.get(Boolean.TRUE); // 获取缓存中的租户 ID 列表
    }

    /**
     * 校验租户是否合法
     * <p>
     * 通过缓存机制优化性能，避免频繁校验同一租户。
     *
     * @param id 租户 ID
     */
    @Override
    @SneakyThrows
    public void validTenant(Long id) {
        validTenantCache.get(id).checkError(); // 获取缓存中的租户合法性校验结果
    }

}

