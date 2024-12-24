package org.nstep.engine.framework.tenant.core.redis;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.redis.core.TimeoutRedisCacheManager;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.Set;

@Slf4j
public class TenantRedisCacheManager extends TimeoutRedisCacheManager {

    // 存储需要忽略多租户处理的缓存名称的集合
    private final Set<String> ignoreCaches;

    // 构造函数，初始化时传入缓存写入器、默认缓存配置和需要忽略的缓存名称集合
    public TenantRedisCacheManager(RedisCacheWriter cacheWriter,
                                   RedisCacheConfiguration defaultCacheConfiguration,
                                   Set<String> ignoreCaches) {
        // 调用父类构造函数，初始化缓存写入器和默认缓存配置
        super(cacheWriter, defaultCacheConfiguration);
        // 初始化忽略缓存集合
        this.ignoreCaches = ignoreCaches;
    }

    // 重写 getCache 方法，返回指定名称的缓存
    @Override
    public Cache getCache(@NotNull String name) {
        // 如果多租户功能开启且当前租户 ID 不为 null，且缓存名称不在忽略列表中
        if (!TenantContextHolder.isIgnore()  // 检查是否忽略多租户
                && TenantContextHolder.getTenantId() != null  // 确保当前租户 ID 不为 null
                && !CollUtil.contains(ignoreCaches, name)) {  // 确保缓存名称不在忽略列表中
            // 拼接租户后缀，格式为 name + ":" + tenantId
            name = name + ":" + TenantContextHolder.getTenantId();
        }

        // 调用父类的 getCache 方法，返回最终的缓存对象
        return super.getCache(name);
    }
}
