package org.nstep.engine.module.infra.framework.file.core.client.ftp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpException;
import cn.hutool.extra.ftp.FtpMode;
import org.nstep.engine.module.infra.framework.file.core.client.AbstractFileClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Ftp 文件客户端
 * <p>
 * 该类继承自 AbstractFileClient，提供基于 FTP 协议的文件上传、删除和获取功能。
 * 通过配置的 FTP 服务器，管理文件的存储和读取。
 * </p>
 */
public class FtpFileClient extends AbstractFileClient<FtpFileClientConfig> {

    // FTP 客户端对象，用于与 FTP 服务器进行交互
    private Ftp ftp;

    /**
     * 构造方法，初始化 FtpFileClient 实例
     *
     * @param id     文件客户端的 ID
     * @param config 配置信息
     */
    public FtpFileClient(Long id, FtpFileClientConfig config) {
        super(id, config);  // 调用父类构造方法，初始化文件客户端
    }

    /**
     * 初始化方法，用于初始化 FtpFileClient 所需的资源
     * <p>
     * 在该方法中，配置的路径会进行处理，确保路径格式正确。
     * 然后初始化 FTP 客户端对象，准备与 FTP 服务器进行交互。
     * </p>
     */
    @Override
    protected void doInit() {
        // 把配置的 \ 替换成 /, 如果路径配置 \a\test, 替换成 /a/test, 替换方法已经处理 null 情况
        config.setBasePath(StrUtil.replace(config.getBasePath(), StrUtil.BACKSLASH, StrUtil.SLASH));
        // FTP 的路径是以 / 结尾
        if (!config.getBasePath().endsWith(StrUtil.SLASH)) {
            config.setBasePath(config.getBasePath() + StrUtil.SLASH);
        }
        // 初始化 Ftp 对象
        this.ftp = new Ftp(config.getHost(), config.getPort(), config.getUsername(), config.getPassword(),
                CharsetUtil.CHARSET_UTF_8, null, null, FtpMode.valueOf(config.getMode()));
    }

    /**
     * 上传文件内容到 FTP 服务器
     * <p>
     * 该方法将文件内容上传到 FTP 服务器的指定路径，并返回文件的访问路径。
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
        String fileName = FileUtil.getName(filePath);  // 获取文件名
        String dir = StrUtil.removeSuffix(filePath, fileName);  // 获取目录路径
        ftp.reconnectIfTimeout();  // 如果连接超时，重新连接
        // 执行文件上传操作
        boolean success = ftp.upload(dir, fileName, new ByteArrayInputStream(content));
        if (!success) {
            throw new FtpException(StrUtil.format("上传文件到目标目录 ({}) 失败", filePath));  // 上传失败，抛出异常
        }
        // 拼接并返回文件的访问路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    /**
     * 删除指定路径的文件
     * <p>
     * 该方法通过 FTP 客户端删除指定路径的文件。
     * </p>
     *
     * @param path 文件路径
     */
    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);  // 获取文件的完整路径
        ftp.reconnectIfTimeout();  // 如果连接超时，重新连接
        ftp.delFile(filePath);  // 删除文件
    }

    /**
     * 获取指定路径的文件内容
     * <p>
     * 该方法从 FTP 服务器下载文件内容并返回。
     * </p>
     *
     * @param path 文件路径
     * @return 文件内容的字节数组
     */
    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);  // 获取文件的完整路径
        String fileName = FileUtil.getName(filePath);  // 获取文件名
        String dir = StrUtil.removeSuffix(filePath, fileName);  // 获取目录路径
        ByteArrayOutputStream out = new ByteArrayOutputStream();  // 用于存储下载的文件内容
        ftp.reconnectIfTimeout();  // 如果连接超时，重新连接
        ftp.download(dir, fileName, out);  // 下载文件内容
        return out.toByteArray();  // 返回文件内容的字节数组
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
