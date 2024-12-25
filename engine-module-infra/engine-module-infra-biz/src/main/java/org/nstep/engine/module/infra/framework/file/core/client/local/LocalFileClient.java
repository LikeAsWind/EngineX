package org.nstep.engine.module.infra.framework.file.core.client.local;

import cn.hutool.core.io.FileUtil;
import org.nstep.engine.module.infra.framework.file.core.client.AbstractFileClient;

import java.io.File;

/**
 * 本地文件客户端
 * <p>
 * 该类继承自 AbstractFileClient，提供本地文件存储的上传、删除和读取功能。
 * 通过与本地文件系统交互，管理文件的存储和读取。
 * </p>
 */
public class LocalFileClient extends AbstractFileClient<LocalFileClientConfig> {

    /**
     * 构造方法，初始化 LocalFileClient 实例
     *
     * @param id     文件客户端的 ID
     * @param config 配置信息
     */
    public LocalFileClient(Long id, LocalFileClientConfig config) {
        super(id, config);  // 调用父类构造方法，初始化文件客户端
    }

    /**
     * 初始化方法，用于初始化 LocalFileClient 所需的资源
     * <p>
     * 在该方法中，确保文件路径以系统文件分隔符结尾，以适应不同操作系统的路径风格。
     * </p>
     */
    @Override
    protected void doInit() {
        // 补全风格。例如说 Linux 是 /，Windows 是 \
        if (!config.getBasePath().endsWith(File.separator)) {
            config.setBasePath(config.getBasePath() + File.separator);  // 如果路径没有以文件分隔符结尾，添加分隔符
        }
    }

    /**
     * 上传文件内容到本地文件系统
     * <p>
     * 该方法将文件内容写入到本地文件系统的指定路径，并返回文件的访问路径。
     * </p>
     *
     * @param content 文件内容的字节数组
     * @param path    文件存储路径
     * @param type    文件类型
     * @return 文件的访问路径
     */
    @Override
    public String upload(byte[] content, String path, String type) {
        // 获取文件的完整路径
        String filePath = getFilePath(path);
        FileUtil.writeBytes(content, filePath);  // 将文件内容写入到本地文件
        // 拼接并返回文件的访问路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    /**
     * 删除指定路径的文件
     * <p>
     * 该方法从本地文件系统中删除指定路径的文件。
     * </p>
     *
     * @param path 文件路径
     */
    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);  // 获取文件的完整路径
        FileUtil.del(filePath);  // 删除文件
    }

    /**
     * 获取指定路径的文件内容
     * <p>
     * 该方法从本地文件系统读取文件内容并返回。
     * </p>
     *
     * @param path 文件路径
     * @return 文件内容的字节数组
     */
    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);  // 获取文件的完整路径
        return FileUtil.readBytes(filePath);  // 读取文件内容并返回
    }

    /**
     * 获取文件的完整路径
     * <p>
     * 该方法将配置的基本路径与文件路径拼接，形成完整的文件路径。
     * </p>
     *
     * @param path 文件路径
     * @return 完整的文件路径
     */
    private String getFilePath(String path) {
        return config.getBasePath() + path;  // 拼接基本路径和文件路径
    }

}
