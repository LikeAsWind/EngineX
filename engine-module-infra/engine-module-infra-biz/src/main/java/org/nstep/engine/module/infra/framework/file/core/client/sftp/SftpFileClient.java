package org.nstep.engine.module.infra.framework.file.core.client.sftp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.ssh.Sftp;
import org.nstep.engine.framework.common.util.io.FileUtils;
import org.nstep.engine.module.infra.framework.file.core.client.AbstractFileClient;

import java.io.File;

/**
 * SFTP 文件客户端，支持通过 SFTP 协议上传、删除、获取文件内容等操作。
 */
public class SftpFileClient extends AbstractFileClient<SftpFileClientConfig> {

    private Sftp sftp;

    /**
     * 构造函数，初始化 SFTP 客户端。
     *
     * @param id     文件客户端的唯一标识符
     * @param config SFTP 客户端配置
     */
    public SftpFileClient(Long id, SftpFileClientConfig config) {
        super(id, config);
    }

    /**
     * 初始化 SFTP 客户端，设置基础路径并创建 SFTP 连接。
     */
    @Override
    protected void doInit() {
        // 补全基础路径，确保路径末尾有分隔符
        if (!config.getBasePath().endsWith(File.separator)) {
            config.setBasePath(config.getBasePath() + File.separator);
        }
        // 初始化 SFTP 客户端对象
        this.sftp = new Sftp(config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
    }

    /**
     * 上传文件内容到指定路径。
     *
     * @param content 文件内容
     * @param path    文件路径
     * @param type    文件类型
     * @return 文件的访问 URL
     */
    @Override
    public String upload(byte[] content, String path, String type) {
        // 获取文件的完整路径
        String filePath = getFilePath(path);
        // 将字节内容写入临时文件
        File file = FileUtils.createTempFile(content);
        // 上传文件
        sftp.upload(filePath, file);
        // 返回文件的 URL
        return super.formatFileUrl(config.getDomain(), path);
    }

    /**
     * 删除指定路径的文件。
     *
     * @param path 文件路径
     */
    @Override
    public void delete(String path) {
        // 获取文件的完整路径
        String filePath = getFilePath(path);
        // 删除文件
        sftp.delFile(filePath);
    }

    /**
     * 获取指定路径文件的内容。
     *
     * @param path 文件路径
     * @return 文件内容的字节数组
     */
    @Override
    public byte[] getContent(String path) {
        // 获取文件的完整路径
        String filePath = getFilePath(path);
        // 创建临时文件用于存储下载的内容
        File destFile = FileUtils.createTempFile();
        // 下载文件内容
        sftp.download(filePath, destFile);
        // 返回文件内容的字节数组
        return FileUtil.readBytes(destFile);
    }

    /**
     * 获取文件的完整路径，包括基础路径。
     *
     * @param path 文件相对路径
     * @return 文件的完整路径
     */
    private String getFilePath(String path) {
        return config.getBasePath() + path;
    }

}
