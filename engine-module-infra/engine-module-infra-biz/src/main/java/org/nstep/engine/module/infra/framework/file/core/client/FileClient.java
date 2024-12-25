package org.nstep.engine.module.infra.framework.file.core.client;

import org.nstep.engine.module.infra.framework.file.core.client.s3.FilePresignedUrlRespDTO;

/**
 * 文件客户端接口
 * 提供文件操作的基本方法，包括文件上传、删除、获取文件内容和获取预签名 URL。
 * 具体的实现类将根据不同的存储系统（如 S3、FTP、SFTP 等）来实现这些方法。
 */
public interface FileClient {

    /**
     * 获取文件客户端的编号。
     *
     * @return 客户端编号
     */
    Long getId();

    /**
     * 上传文件。
     *
     * @param content 文件内容，字节流
     * @param path    文件的相对路径
     * @param type    文件的类型（如文件格式等）
     * @return 完整的文件访问路径（HTTP 地址）
     * @throws Exception 上传过程中发生的异常
     */
    String upload(byte[] content, String path, String type) throws Exception;

    /**
     * 删除文件。
     *
     * @param path 文件的相对路径
     * @throws Exception 删除过程中发生的异常
     */
    void delete(String path) throws Exception;

    /**
     * 获取文件内容。
     *
     * @param path 文件的相对路径
     * @return 文件的字节内容
     * @throws Exception 获取文件内容过程中发生的异常
     */
    byte[] getContent(String path) throws Exception;

    /**
     * 获取文件的预签名 URL。
     * 该方法返回一个预签名的 URL，允许客户端在不暴露凭证的情况下直接访问文件。
     *
     * @param path 文件的相对路径
     * @return 文件的预签名 URL
     * @throws Exception 获取预签名 URL 时发生的异常
     */
    default FilePresignedUrlRespDTO getPresignedObjectUrl(String path) throws Exception {
        // 默认实现抛出异常，表示该操作不支持
        throw new UnsupportedOperationException("不支持的操作");
    }

}
