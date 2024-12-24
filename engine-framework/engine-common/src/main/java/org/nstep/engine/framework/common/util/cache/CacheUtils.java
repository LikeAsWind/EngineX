package org.nstep.engine.framework.common.util.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.time.Duration;
import java.util.concurrent.Executors;

/**
 * Cache 工具类
 */
public class CacheUtils {

    /**
     * 构建异步刷新的 LoadingCache 对象
     * <p>
     * 注意：如果你的缓存和 ThreadLocal 有关系，要么自己处理 ThreadLocal 的传递，要么使用 {@link #buildCache(Duration, CacheLoader)} 方法
     * <p>
     * 或者简单理解：
     * 1、和“人”相关的，使用 {@link #buildCache(Duration, CacheLoader)} 方法
     * 2、和“全局”、“系统”相关的，使用当前缓存方法
     *
     * @param duration 过期时间
     * @param loader   CacheLoader 对象
     * @return LoadingCache 对象
     */
    public static <K, V> LoadingCache<K, V> buildAsyncReloadingCache(Duration duration, CacheLoader<K, V> loader) {
        // 使用 CacheBuilder 创建一个新的缓存构建器
        return CacheBuilder.newBuilder()
                // 设置缓存项在写入后经过指定的持续时间后刷新
                .refreshAfterWrite(duration)
                // 使用 asyncReloading 方法构建一个异步加载缓存
                // 可能要思考下，未来要不要做成可配置
                .build(CacheLoader.asyncReloading(loader, Executors.newCachedThreadPool()));
    }

    /**
     * 构建同步刷新的 LoadingCache 对象
     *
     * @param duration 过期时间
     * @param loader   CacheLoader 对象
     * @return LoadingCache 对象
     */
    public static <K, V> LoadingCache<K, V> buildCache(Duration duration, CacheLoader<K, V> loader) {
        // 使用 CacheBuilder 创建一个新的缓存构建器
        return CacheBuilder.newBuilder()
                // 设置缓存项在写入后经过指定的持续时间后刷新
                .refreshAfterWrite(duration)
                // 构建一个同步加载缓存
                .build(loader);
    }

}
