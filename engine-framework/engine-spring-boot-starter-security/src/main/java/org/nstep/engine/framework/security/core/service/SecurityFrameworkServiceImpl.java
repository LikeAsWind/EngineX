package org.nstep.engine.framework.security.core.service;

import cn.hutool.core.collection.CollUtil;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.common.core.KeyValue;
import org.nstep.engine.framework.security.core.LoginUser;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.module.system.api.permission.PermissionApi;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.nstep.engine.framework.common.util.cache.CacheUtils.buildCache;
import static org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 默认的 {@link SecurityFrameworkService} 实现类
 * <p>
 * 该类实现了 {@link SecurityFrameworkService} 接口，提供了基于权限、角色和授权范围的校验操作。
 * 同时，使用了缓存机制来提高性能，避免重复查询相同的数据。
 */
@AllArgsConstructor
public class SecurityFrameworkServiceImpl implements SecurityFrameworkService {

    private final PermissionApi permissionApi;

    /**
     * 针对 {@link #hasAnyRoles(String...)} 的缓存
     * <p>
     * 使用缓存来减少重复的角色查询，提高性能。缓存的有效期为 1 分钟。
     */
    private final LoadingCache<KeyValue<Long, List<String>>, Boolean> hasAnyRolesCache = buildCache(
            Duration.ofMinutes(1L), // 过期时间 1 分钟
            new CacheLoader<>() {

                @NotNull
                @Override
                public Boolean load(@NotNull KeyValue<Long, List<String>> key) {
                    // 从 permissionApi 查询用户是否具有任意角色
                    return permissionApi.hasAnyRoles(key.getKey(), key.getValue().toArray(new String[0])).getCheckedData();
                }

            });

    /**
     * 针对 {@link #hasAnyPermissions(String...)} 的缓存
     * <p>
     * 使用缓存来减少重复的权限查询，提高性能。缓存的有效期为 1 分钟。
     */
    private final LoadingCache<KeyValue<Long, List<String>>, Boolean> hasAnyPermissionsCache = buildCache(
            Duration.ofMinutes(1L), // 过期时间 1 分钟
            new CacheLoader<>() {

                @NotNull
                @Override
                public Boolean load(@NotNull KeyValue<Long, List<String>> key) {
                    // 从 permissionApi 查询用户是否具有任意权限
                    return permissionApi.hasAnyPermissions(key.getKey(), key.getValue().toArray(new String[0])).getCheckedData();
                }

            });

    @Override
    public boolean hasPermission(String permission) {
        // 判断用户是否拥有指定权限，调用 hasAnyPermissions 方法
        return hasAnyPermissions(permission);
    }

    @Override
    @SneakyThrows
    public boolean hasAnyPermissions(String... permissions) {
        Long userId = getLoginUserId();
        if (userId == null) {
            // 如果没有登录用户，则返回 false
            return false;
        }
        // 使用缓存来查询用户是否具有任意权限
        return hasAnyPermissionsCache.get(new KeyValue<>(userId, Arrays.asList(permissions)));
    }

    @Override
    public boolean hasRole(String role) {
        // 判断用户是否拥有指定角色，调用 hasAnyRoles 方法
        return hasAnyRoles(role);
    }

    @Override
    @SneakyThrows
    public boolean hasAnyRoles(String... roles) {
        Long userId = getLoginUserId();
        if (userId == null) {
            // 如果没有登录用户，则返回 false
            return false;
        }
        // 使用缓存来查询用户是否具有任意角色
        return hasAnyRolesCache.get(new KeyValue<>(userId, Arrays.asList(roles)));
    }

    @Override
    public boolean hasScope(String scope) {
        // 判断用户是否拥有指定授权范围，调用 hasAnyScopes 方法
        return hasAnyScopes(scope);
    }

    @Override
    public boolean hasAnyScopes(String... scope) {
        LoginUser user = SecurityFrameworkUtils.getLoginUser();
        if (user == null) {
            // 如果没有登录用户，则返回 false
            return false;
        }
        // 判断用户的授权范围中是否包含指定的授权范围
        return CollUtil.containsAny(user.getScopes(), Arrays.asList(scope));
    }

}
