package org.nstep.engine.module.infra.framework.file.core.client.s3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件预签名地址 Response DTO
 * <p>
 * 该类用于表示文件预签名地址的响应数据传输对象（DTO）。
 * 它包含了文件上传和访问（读取、下载）所需的 URL。
 * </p>
 */
@Data  // 自动生成 getter、setter、toString、equals 和 hashCode 方法
@AllArgsConstructor  // 自动生成包含所有字段的构造函数
@NoArgsConstructor  // 自动生成无参构造函数
public class FilePresignedUrlRespDTO {

    /**
     * 文件上传 URL（用于上传）
     * <p>
     * 该 URL 用于文件上传，通常是预签名的 URL，允许客户端将文件上传到 S3 或其他存储服务。
     * </p>
     */
    private String uploadUrl;

    /**
     * 文件 URL（用于读取、下载等）
     * <p>
     * 该 URL 用于文件的访问，包括读取和下载等操作。
     * </p>
     */
    private String url;

}
