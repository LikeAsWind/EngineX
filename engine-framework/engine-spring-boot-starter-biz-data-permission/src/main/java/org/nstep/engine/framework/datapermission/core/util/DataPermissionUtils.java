package org.nstep.engine.framework.datapermission.core.util;

import lombok.SneakyThrows;
import org.nstep.engine.framework.datapermission.core.annotation.DataPermission;
import org.nstep.engine.framework.datapermission.core.aop.DataPermissionContextHolder;

import java.util.concurrent.Callable;

/**
 * 数据权限工具类，提供忽略数据权限的执行逻辑。
 */
public class DataPermissionUtils {

    private static DataPermission DATA_PERMISSION_DISABLE;

    /**
     * 获取一个禁用数据权限的注解实例。
     * 这个方法使用SneakyThrows来静默处理异常，确保即使发生异常也能返回一个禁用的数据权限注解。
     */
    @DataPermission(enable = false)
    @SneakyThrows
    private static DataPermission getDisableDataPermissionDisable() {
        if (DATA_PERMISSION_DISABLE == null) {
            DATA_PERMISSION_DISABLE = DataPermissionUtils.class
                    .getDeclaredMethod("getDisableDataPermissionDisable")
                    .getAnnotation(DataPermission.class);
        }
        return DATA_PERMISSION_DISABLE;
    }

    /**
     * 忽略数据权限，执行对应的逻辑。
     *
     * @param runnable 要执行的逻辑，不返回任何结果。
     */
    public static void executeIgnore(Runnable runnable) {
        // 获取禁用数据权限的注解。
        DataPermission dataPermission = getDisableDataPermissionDisable();
        // 将禁用数据权限的注加入到上下文中。
        DataPermissionContextHolder.add(dataPermission);
        try {
            // 执行传入的Runnable逻辑。
            runnable.run();
        } finally {
            // 执行完毕后，从上下文中移除禁用数据权限的注解。
            DataPermissionContextHolder.remove();
        }
    }

    /**
     * 忽略数据权限，执行对应的逻辑，并返回结果。
     *
     * @param callable 要执行的逻辑，返回一个结果。
     * @return 执行Callable逻辑的结果。
     */
    @SneakyThrows
    public static <T> T executeIgnore(Callable<T> callable) {
        DataPermission dataPermission = getDisableDataPermissionDisable();
        DataPermissionContextHolder.add(dataPermission);
        try {
            // 执行 callable
            return callable.call();
        } finally {
            DataPermissionContextHolder.remove();
        }
    }

}
