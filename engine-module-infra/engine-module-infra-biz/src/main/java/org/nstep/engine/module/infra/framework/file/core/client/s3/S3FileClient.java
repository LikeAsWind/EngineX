package org.nstep.engine.module.infra.framework.file.core.client.s3;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import io.minio.*;
import io.minio.http.Method;
import org.nstep.engine.module.infra.framework.file.core.client.AbstractFileClient;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

/**
 * 基于 S3 协议的文件客户端，实现 MinIO、阿里云、腾讯云、七牛云、华为云等云服务
 * <p>
 * 该客户端实现了基于 S3 协议的文件存储功能，支持与多个云服务提供商（如 MinIO、阿里云、腾讯云等）进行交互。
 * 使用 MinIO 客户端库进行文件的上传、删除、下载以及预签名 URL 的生成等操作。
 * </p>
 */
public class S3FileClient extends AbstractFileClient<S3FileClientConfig> {

    private MinioClient client;

    /**
     * 构造方法，初始化 S3FileClient 实例
     *
     * @param id     文件客户端的 ID
     * @param config 配置信息
     */
    public S3FileClient(Long id, S3FileClientConfig config) {
        super(id, config);  // 调用父类构造方法，初始化文件客户端
    }

    /**
     * 初始化方法，用于初始化 S3FileClient 所需的资源
     * <p>
     * 在该方法中，构建了 MinioClient 客户端实例，并设置相关的配置信息，如 endpoint、认证密钥等。
     * </p>
     */
    @Override
    protected void doInit() {
        // 补全 domain，如果未配置，则自动构建 domain
        if (StrUtil.isEmpty(config.getDomain())) {
            config.setDomain(buildDomain());
        }
        // 初始化 MinioClient 客户端
        client = MinioClient.builder()
                .endpoint(buildEndpointURL()) // 设置 endpoint URL
                .region(buildRegion()) // 设置 region
                .credentials(config.getAccessKey(), config.getAccessSecret()) // 设置认证密钥
                .build();
        // 启用虚拟样式端点（适配某些云服务商）
        enableVirtualStyleEndpoint();
    }

    /**
     * 基于 endpoint 构建调用云服务的 URL 地址
     *
     * @return 完整的 Endpoint URL
     */
    private String buildEndpointURL() {
        // 如果已经是 http 或者 https，则不进行拼接，适配 MinIO
        if (HttpUtil.isHttp(config.getEndpoint()) || HttpUtil.isHttps(config.getEndpoint())) {
            return config.getEndpoint();
        }
        return StrUtil.format("https://{}", config.getEndpoint());  // 默认构建 https URL
    }

    /**
     * 基于 bucket + endpoint 构建访问的 Domain 地址
     *
     * @return 完整的 Domain 地址
     */
    private String buildDomain() {
        // 如果已经是 http 或者 https，则不进行拼接，适配 MinIO
        if (HttpUtil.isHttp(config.getEndpoint()) || HttpUtil.isHttps(config.getEndpoint())) {
            return StrUtil.format("{}/{}", config.getEndpoint(), config.getBucket());
        }
        // 阿里云、腾讯云、华为云适配，七牛云需要自定义域名
        return StrUtil.format("https://{}.{}", config.getBucket(), config.getEndpoint());
    }

    /**
     * 基于 bucket 构建 region 地区
     *
     * @return region 地区
     */
    private String buildRegion() {
        // 阿里云必须有 region，否则会报错
        if (config.getEndpoint().contains(S3FileClientConfig.ENDPOINT_ALIYUN)) {
            return StrUtil.subBefore(config.getEndpoint(), '.', false)
                    .replaceAll("-internal", "") // 去除内网 Endpoint 的后缀
                    .replaceAll("https://", "");
        }
        // 腾讯云必须有 region，否则会报错
        if (config.getEndpoint().contains(S3FileClientConfig.ENDPOINT_TENCENT)) {
            return StrUtil.subAfter(config.getEndpoint(), "cos.", false)
                    .replaceAll("." + S3FileClientConfig.ENDPOINT_TENCENT, ""); // 去除 Endpoint
        }
        return null;  // 默认不设置 region
    }

    /**
     * 开启 VirtualStyle 模式，适配腾讯云和火山云等云服务
     */
    private void enableVirtualStyleEndpoint() {
        if (StrUtil.containsAny(config.getEndpoint(),
                S3FileClientConfig.ENDPOINT_TENCENT,  // 腾讯云
                S3FileClientConfig.ENDPOINT_VOLCES)) { // 火山云
            client.enableVirtualStyleEndpoint();  // 启用虚拟样式端点
        }
    }

    /**
     * 上传文件到 S3 存储
     * <p>
     * 该方法将文件内容上传到指定的 S3 存储桶中，并返回文件的访问 URL。
     * </p>
     *
     * @param content 文件内容的字节数组
     * @param path    文件存储路径
     * @param type    文件类型
     * @return 文件的访问 URL
     * @throws Exception 上传过程中可能抛出的异常
     */
    @Override
    public String upload(byte[] content, String path, String type) throws Exception {
        // 执行文件上传
        client.putObject(PutObjectArgs.builder()
                .bucket(config.getBucket()) // 必须传递 bucket
                .contentType(type)  // 设置文件类型
                .object(path)  // 设置相对路径作为文件的 key
                .stream(new ByteArrayInputStream(content), content.length, -1)  // 设置文件内容
                .build());
        // 拼接并返回文件的访问路径
        return config.getDomain() + "/" + path;
    }

    /**
     * 删除指定路径的文件
     * <p>
     * 该方法从 S3 存储中删除指定路径的文件。
     * </p>
     *
     * @param path 文件路径
     * @throws Exception 删除过程中可能抛出的异常
     */
    @Override
    public void delete(String path) throws Exception {
        client.removeObject(RemoveObjectArgs.builder()
                .bucket(config.getBucket())  // 必须传递 bucket
                .object(path)  // 设置相对路径作为文件的 key
                .build());
    }

    /**
     * 获取指定路径的文件内容
     * <p>
     * 该方法从 S3 存储中读取文件内容并返回。
     * </p>
     *
     * @param path 文件路径
     * @return 文件内容的字节数组
     * @throws Exception 读取过程中可能抛出的异常
     */
    @Override
    public byte[] getContent(String path) throws Exception {
        GetObjectResponse response = client.getObject(GetObjectArgs.builder()
                .bucket(config.getBucket())  // 必须传递 bucket
                .object(path)  // 设置相对路径作为文件的 key
                .build());
        return IoUtil.readBytes(response);  // 读取文件内容并返回
    }

    /**
     * 获取文件的预签名 URL
     * <p>
     * 该方法生成一个预签名的 URL，用于上传或访问文件。
     * </p>
     *
     * @param path 文件路径
     * @return 文件的预签名 URL 响应
     * @throws Exception 生成预签名 URL 过程中可能抛出的异常
     */
    @Override
    public FilePresignedUrlRespDTO getPresignedObjectUrl(String path) throws Exception {
        String uploadUrl = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.PUT)  // 设置 HTTP 方法为 PUT（用于上传）
                .bucket(config.getBucket())  // 必须传递 bucket
                .object(path)  // 设置相对路径作为文件的 key
                .expiry(10, TimeUnit.MINUTES)  // 设置过期时间为 10 分钟
                .build());
        return new FilePresignedUrlRespDTO(uploadUrl, config.getDomain() + "/" + path);
    }

}
