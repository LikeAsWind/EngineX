package org.nstep.engine.framework.tenant.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 多租户上下文 Holder
 * <p>
 * 用于存储当前线程的租户信息。通过 ThreadLocal 来确保每个线程拥有独立的租户信息，
 * 从而支持多租户的隔离性。支持设置和获取租户 ID，以及设置是否忽略租户信息的标志。
 */
public class TenantContextHolder {

    /**
     * 当前租户编号
     * <p>
     * 使用 ThreadLocal 来存储每个线程的租户 ID，确保线程安全。
     */
    private static final ThreadLocal<Long> TENANT_ID = new TransmittableThreadLocal<>();

    /**
     * 是否忽略租户
     * <p>
     * 使用 ThreadLocal 来存储每个线程是否忽略租户上下文的标志。
     * 在某些情况下（如某些全局操作），可以通过设置此标志来忽略租户上下文。
     */
    private static final ThreadLocal<Boolean> IGNORE = new TransmittableThreadLocal<>();

    /**
     * 获得当前租户编号
     *
     * @return 租户编号，如果当前线程没有设置租户编号，返回 null。
     */
    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * 设置当前租户编号
     *
     * @param tenantId 租户编号
     */
    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * 获得租户编号。如果不存在，则抛出 NullPointerException 异常
     *
     * @return 租户编号
     * @throws NullPointerException 如果当前线程没有设置租户编号，则抛出异常
     */
    public static Long getRequiredTenantId() {
        Long tenantId = getTenantId();
        if (tenantId == null) {
            throw new NullPointerException("TenantContextHolder 不存在租户编号！可参考文档：");
        }
        return tenantId;
    }

    /**
     * 当前是否忽略租户上下文
     *
     * @return 如果当前线程设置了忽略租户上下文标志，返回 true；否则返回 false
     */
    public static boolean isIgnore() {
        return Boolean.TRUE.equals(IGNORE.get());
    }

    /**
     * 设置当前是否忽略租户上下文
     *
     * @param ignore 是否忽略租户上下文
     */
    public static void setIgnore(Boolean ignore) {
        IGNORE.set(ignore);
    }

    /**
     * 清除当前线程的租户上下文信息
     * <p>
     * 清除租户 ID 和是否忽略租户上下文的标志。通常在请求处理完毕后调用。
     */
    public static void clear() {
        TENANT_ID.remove();
        IGNORE.remove();
    }

}

