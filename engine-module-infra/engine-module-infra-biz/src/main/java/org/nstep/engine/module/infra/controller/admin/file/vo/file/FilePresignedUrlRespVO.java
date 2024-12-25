package org.nstep.engine.module.infra.controller.admin.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理后台 - 文件预签名地址响应 VO
 * <p>
 * 该类用于返回文件上传的预签名 URL 以及文件访问 URL。
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "管理后台 - 文件预签名地址 Response VO")
@Data
public class FilePresignedUrlRespVO {

    /**
     * 配置编号
     * <p>
     * 用于标识与文件上传相关的配置编号。
     * </p>
     */
    @Schema(description = "配置编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    /**
     * 文件上传 URL
     * <p>
     * 这是一个预签名的 URL，前端可以通过该 URL 上传文件。
     * </p>
     */
    @Schema(description = "文件上传 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://s3.cn-south-1.qiniucs.com/engine/758d3a5387507358c7236de4c8f96de1c7f5097ff6a7722b34772fb7b76b140f.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=3TvrJ70gl2Gt6IBe7_IZT1F6i_k0iMuRtyEv4EyS%2F20240217%2Fcn-south-1%2Fs3%2Faws4_request&X-Amz-Date=20240217T123222Z&X-Amz-Expires=600&X-Amz-SignedHeaders=host&X-Amz-Signature=a29f33770ab79bf523ccd4034d0752ac545f3c2a3b17baa1eb4e280cfdccfda5")
    private String uploadUrl;

    /**
     * 文件访问 URL
     * <p>
     * 文件上传后，前端可以通过该 URL 访问已上传的文件。
     * </p>
     */
    @Schema(description = "文件访问 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://test")
    private String url;

}
