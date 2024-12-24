package org.nstep.engine.framework.datapermission.core.aop;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.nstep.engine.framework.datapermission.core.annotation.DataPermission;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link org.nstep.engine.framework.datapermission.core.annotation.DataPermission} 注解的 Context 上下文
 * 用于存储和管理DataPermission注解的上下文环境。
 */
public class DataPermissionContextHolder {

    /**
     * 使用ThreadLocal来保证线程安全，存储每个线程中的DataPermission注解列表。
     * 使用LinkedList的原因是因为可能存在方法的嵌套调用，需要支持入栈和出栈操作。
     */
    private static final ThreadLocal<LinkedList<DataPermission>> DATA_PERMISSIONS = TransmittableThreadLocal.withInitial(LinkedList::new);

    /**
     * 获取当前线程中最新的DataPermission注解。
     *
     * @return 返回DataPermission注解，如果没有则可能返回null。
     */
    public static DataPermission get() {
        return DATA_PERMISSIONS.get().peekLast();
    }

    /**
     * 将DataPermission注解入栈到当前线程的上下文中。
     *
     * @param dataPermission DataPermission注解
     */
    public static void add(DataPermission dataPermission) {
        DATA_PERMISSIONS.get().addLast(dataPermission);
    }

    /**
     * 将DataPermission注解出栈，并返回出栈的注解。
     */
    public static void remove() {
        DataPermission dataPermission = DATA_PERMISSIONS.get().removeLast();
        // 如果出栈后列表为空，则清除ThreadLocal中的引用，释放资源。
        if (DATA_PERMISSIONS.get().isEmpty()) {
            DATA_PERMISSIONS.remove();
        }
    }

    /**
     * 获取当前线程中所有的DataPermission注解。
     *
     * @return 返回一个包含所有DataPermission注解的列表。
     */
    public static List<DataPermission> getAll() {
        return DATA_PERMISSIONS.get();
    }

    /**
     * 清空当前线程中的DataPermission上下文环境。
     * <p>
     * 这个方法主要用于单元测试，以便在测试结束后清理上下文环境。
     */
    public static void clear() {
        DATA_PERMISSIONS.remove();
    }

}