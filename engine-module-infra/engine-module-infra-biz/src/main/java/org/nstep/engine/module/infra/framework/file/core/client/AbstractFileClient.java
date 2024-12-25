package org.nstep.engine.module.infra.framework.file.core.client;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件客户端的抽象类，提供模板方法，减少子类的冗余代码
 * 该类负责管理文件客户端的基本配置和初始化逻辑，子类可以根据具体的文件存储服务实现文件上传、下载等操作。
 */
@Slf4j
public abstract class AbstractFileClient<Config extends FileClientConfig> implements FileClient {

    /**
     * 配置编号，用于标识不同的文件客户端配置。
     */
    private final Long id;

    /**
     * 文件配置，包含文件客户端的具体配置信息。
     */
    protected Config config;

    /**
     * 构造函数，初始化文件客户端的配置。
     *
     * @param id     配置编号
     * @param config 文件客户端的配置
     */
    public AbstractFileClient(Long id, Config config) {
        this.id = id;
        this.config = config;
    }

    /**
     * 初始化方法，调用子类的自定义初始化逻辑。
     * 该方法提供了一个模板方法，在子类中实现 `doInit` 方法来完成具体的初始化操作。
     */
    public final void init() {
        doInit();
        log.debug("[init][配置({}) 初始化完成]", config);
    }

    /**
     * 子类实现的自定义初始化方法。
     * 子类需要实现该方法来完成具体的初始化操作。
     */
    protected abstract void doInit();

    /**
     * 刷新文件客户端的配置，如果配置发生变化，则重新初始化。
     *
     * @param config 新的文件客户端配置
     */
    public final void refresh(Config config) {
        // 判断新配置是否与当前配置相同，如果相同则不进行任何操作
        if (config.equals(this.config)) {
            return;
        }
        log.info("[refresh][配置({})发生变化，重新初始化]", config);
        this.config = config;
        // 重新初始化
        this.init();
    }

    /**
     * 获取配置编号。
     *
     * @return 配置编号
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * 格式化文件的 URL 访问地址，生成访问文件的完整 URL。
     * 使用场景：local、ftp、db 等文件存储服务，通过 FileController 获取文件内容。
     *
     * @param domain 自定义域名
     * @param path   文件路径
     * @return 格式化后的 URL 访问地址
     */
    protected String formatFileUrl(String domain, String path) {
        return StrUtil.format("{}/admin-api/infra/file/{}/get/{}", domain, getId(), path);
    }

}
