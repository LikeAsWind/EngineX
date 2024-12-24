package org.nstep.engine.framework.security.core.service;

/**
 * Security 框架 Service 接口，定义权限相关的校验操作
 * <p>
 * 该接口提供了一些方法，用于检查当前用户是否具有某些权限、角色或授权范围。
 */
public interface SecurityFrameworkService {

    /**
     * 判断当前用户是否拥有指定权限
     *
     * @param permission 权限标识
     * @return 如果当前用户拥有该权限，则返回 true，否则返回 false
     */
    boolean hasPermission(String permission);

    /**
     * 判断当前用户是否拥有任意一个指定权限
     *
     * @param permissions 权限标识数组
     * @return 如果当前用户拥有其中任意一个权限，则返回 true，否则返回 false
     */
    boolean hasAnyPermissions(String... permissions);

    /**
     * 判断当前用户是否拥有指定角色
     * <p>
     * 注意：角色使用的是 SysRoleDO 的 code 标识
     *
     * @param role 角色标识
     * @return 如果当前用户拥有该角色，则返回 true，否则返回 false
     */
    boolean hasRole(String role);

    /**
     * 判断当前用户是否拥有任意一个指定角色
     *
     * @param roles 角色标识数组
     * @return 如果当前用户拥有其中任意一个角色，则返回 true，否则返回 false
     */
    boolean hasAnyRoles(String... roles);

    /**
     * 判断当前用户是否拥有指定授权范围
     *
     * @param scope 授权范围标识
     * @return 如果当前用户拥有该授权范围，则返回 true，否则返回 false
     */
    boolean hasScope(String scope);

    /**
     * 判断当前用户是否拥有任意一个指定授权范围
     *
     * @param scope 授权范围标识数组
     * @return 如果当前用户拥有其中任意一个授权范围，则返回 true，否则返回 false
     */
    boolean hasAnyScopes(String... scope);
}
