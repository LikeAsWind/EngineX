package org.nstep.engine.framework.env.core.context;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发环境上下文类，用于存储和管理当前线程的环境标签。
 * <p>
 * 该类利用 `ThreadLocal` 来存储每个线程独立的环境标签列表，确保每个线程都有独立的标签上下文。
 * 通过该类可以在多线程环境中动态地设置、获取和移除环境标签（例如：不同的环境标识，如开发、生产环境等）。
 */
public class EnvContextHolder {

    /**
     * 标签的上下文，使用 `ThreadLocal` 存储每个线程的环境标签列表。
     * <p>
     * 使用 {@link List} 类型的原因是：可能存在多层设置或清理标签的需求。
     * 每个线程都有一个独立的标签列表，可以通过 `add` 方法添加标签，通过 `remove` 方法清理标签。
     * `TransmittableThreadLocal` 是一种线程局部变量，能够在多线程环境中传递数据。
     */
    private static final ThreadLocal<List<String>> TAG_CONTEXT = TransmittableThreadLocal.withInitial(ArrayList::new);

    /**
     * 获取当前线程的环境标签。
     * <p>
     * 该方法返回标签上下文中的最后一个标签（栈顶的标签），即当前活跃的环境标签。
     * 如果标签列表为空，返回 `null`。
     *
     * @return 当前线程的环境标签
     */
    public static String getTag() {
        return CollUtil.getLast(TAG_CONTEXT.get());
    }

    /**
     * 设置当前线程的环境标签。
     * <p>
     * 该方法将给定的标签添加到当前线程的标签上下文中。
     * 通过该方法，可以为当前线程设置新的环境标签。
     *
     * @param tag 要设置的环境标签
     */
    public static void setTag(String tag) {
        TAG_CONTEXT.get().add(tag);
    }

    /**
     * 移除当前线程的最后一个环境标签。
     * <p>
     * 该方法将当前线程标签上下文中的最后一个标签移除，用于清理标签。
     * 如果标签列表为空，则不执行任何操作。
     */
    public static void removeTag() {
        List<String> tags = TAG_CONTEXT.get();
        if (CollUtil.isEmpty(tags)) {
            return;
        }
        tags.remove(tags.size() - 1); // 移除栈顶标签
    }

}
