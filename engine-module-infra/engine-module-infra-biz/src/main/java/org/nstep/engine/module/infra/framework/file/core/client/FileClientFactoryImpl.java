package org.nstep.engine.module.infra.framework.file.core.client;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.infra.framework.file.core.enums.FileStorageEnum;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 文件客户端工厂实现类
 * 该类实现了 {@link FileClientFactory} 接口，负责创建、更新和获取文件客户端实例。
 */
@Slf4j
public class FileClientFactoryImpl implements FileClientFactory {

    /**
     * 存储文件客户端的 Map
     * key：配置编号
     * value：文件客户端实例
     */
    private final ConcurrentMap<Long, AbstractFileClient<?>> clients = new ConcurrentHashMap<>();

    /**
     * 根据配置编号获取文件客户端实例
     *
     * @param configId 配置编号
     * @return 文件客户端实例
     */
    @Override
    public FileClient getFileClient(Long configId) {
        AbstractFileClient<?> client = clients.get(configId);
        if (client == null) {
            log.error("[getFileClient][配置编号({}) 找不到客户端]", configId);
        }
        return client;
    }

    /**
     * 创建或更新文件客户端
     *
     * @param configId 配置编号
     * @param storage  存储器的枚举值 {@link FileStorageEnum}
     * @param config   文件客户端配置
     * @param <Config> 配置类型，必须是实现 {@link FileClientConfig} 的类
     */
    @Override
    @SuppressWarnings("unchecked")
    public <Config extends FileClientConfig> void createOrUpdateFileClient(Long configId, Integer storage, Config config) {
        AbstractFileClient<Config> client = (AbstractFileClient<Config>) clients.get(configId);
        if (client == null) {
            // 如果文件客户端不存在，则创建并初始化
            client = this.createFileClient(configId, storage, config);
            client.init();
            clients.put(client.getId(), client); // 将创建的客户端存入 Map
        } else {
            // 如果文件客户端已经存在，则刷新配置
            client.refresh(config);
        }
    }

    /**
     * 创建文件客户端实例
     *
     * @param configId 配置编号
     * @param storage  存储器的枚举值 {@link FileStorageEnum}
     * @param config   文件客户端配置
     * @param <Config> 配置类型，必须是实现 {@link FileClientConfig} 的类
     * @return 创建的文件客户端实例
     */
    @SuppressWarnings("unchecked")
    private <Config extends FileClientConfig> AbstractFileClient<Config> createFileClient(
            Long configId, Integer storage, Config config) {
        // 根据存储类型获取对应的枚举值
        FileStorageEnum storageEnum = FileStorageEnum.getByStorage(storage);
        Assert.notNull(storageEnum, String.format("文件配置(%s) 为空", storageEnum)); // 如果存储类型为空，则抛出异常

        // 使用反射创建文件客户端实例
        return (AbstractFileClient<Config>) ReflectUtil.newInstance(storageEnum.getClientClass(), configId, config);
    }
}
