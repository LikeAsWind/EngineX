package org.nstep.engine.framework.tenant.core.util;

import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;

import java.util.Map;
import java.util.concurrent.Callable;

import static org.nstep.engine.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * 多租户工具类
 * <p>
 * 提供了在多租户环境下执行逻辑的方法，能够在执行逻辑前后自动设置和恢复租户信息。
 */
public class TenantUtils {

    /**
     * 使用指定租户，执行对应的逻辑
     * <p>
     * 在执行逻辑之前，会将当前租户信息设置为指定的租户编号。如果当前是忽略租户的情况下，
     * 会被强制设置成不忽略租户，执行完逻辑后，租户信息会恢复到执行前的状态。
     *
     * @param tenantId 租户编号
     * @param runnable 执行的逻辑
     */
    public static void execute(Long tenantId, Runnable runnable) {
        Long oldTenantId = TenantContextHolder.getTenantId(); // 保存当前租户编号
        Boolean oldIgnore = TenantContextHolder.isIgnore();   // 保存当前是否忽略租户
        try {
            TenantContextHolder.setTenantId(tenantId); // 设置租户编号
            TenantContextHolder.setIgnore(false);      // 设置不忽略租户
            // 执行逻辑
            runnable.run();
        } finally {
            TenantContextHolder.setTenantId(oldTenantId); // 恢复原租户编号
            TenantContextHolder.setIgnore(oldIgnore);     // 恢复原租户忽略状态
        }
    }

    /**
     * 使用指定租户，执行对应的逻辑
     * <p>
     * 在执行逻辑之前，会将当前租户信息设置为指定的租户编号。如果当前是忽略租户的情况下，
     * 会被强制设置成不忽略租户，执行完逻辑后，租户信息会恢复到执行前的状态。
     *
     * @param tenantId 租户编号
     * @param callable 执行的逻辑，返回值类型为 V
     * @param <V>      返回值类型
     * @return 执行逻辑的返回值
     */
    public static <V> V execute(Long tenantId, Callable<V> callable) {
        Long oldTenantId = TenantContextHolder.getTenantId(); // 保存当前租户编号
        Boolean oldIgnore = TenantContextHolder.isIgnore();   // 保存当前是否忽略租户
        try {
            TenantContextHolder.setTenantId(tenantId); // 设置租户编号
            TenantContextHolder.setIgnore(false);      // 设置不忽略租户
            // 执行逻辑
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e); // 捕获并抛出异常
        } finally {
            TenantContextHolder.setTenantId(oldTenantId); // 恢复原租户编号
            TenantContextHolder.setIgnore(oldIgnore);     // 恢复原租户忽略状态
        }
    }

    /**
     * 忽略租户，执行对应的逻辑
     * <p>
     * 在执行逻辑之前，会将租户忽略标志设置为 true，执行完逻辑后，会恢复原有的忽略状态。
     *
     * @param runnable 执行的逻辑
     */
    public static void executeIgnore(Runnable runnable) {
        Boolean oldIgnore = TenantContextHolder.isIgnore(); // 保存当前是否忽略租户
        try {
            TenantContextHolder.setIgnore(true); // 设置为忽略租户
            // 执行逻辑
            runnable.run();
        } finally {
            TenantContextHolder.setIgnore(oldIgnore); // 恢复原租户忽略状态
        }
    }

    /**
     * 将多租户编号，添加到 HTTP 请求的 header 中
     *
     * @param headers  HTTP 请求的 headers
     * @param tenantId 租户编号
     */
    public static void addTenantHeader(Map<String, String> headers, Long tenantId) {
        if (tenantId != null) {
            headers.put(HEADER_TENANT_ID, tenantId.toString()); // 将租户编号添加到 header 中
        }
    }

}
