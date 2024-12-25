package org.nstep.engine.module.infra.framework.file.core.client;

import org.nstep.engine.module.infra.framework.file.core.enums.FileStorageEnum;

/**
 * 文件客户端工厂接口
 * 该接口用于获取和创建文件客户端实例。
 */
public interface FileClientFactory {

    /**
     * 根据配置编号获得对应的文件客户端。
     *
     * @param configId 配置编号
     * @return 文件客户端实例
     */
    FileClient getFileClient(Long configId);

    /**
     * 创建或更新文件客户端。
     *
     * @param configId 配置编号
     * @param storage  存储器的枚举 {@link FileStorageEnum}，指定使用的存储类型
     * @param config   文件客户端配置
     * @param <Config> 配置类型，必须是实现 {@link FileClientConfig} 的类
     */
    <Config extends FileClientConfig> void createOrUpdateFileClient(Long configId, Integer storage, Config config);
}
