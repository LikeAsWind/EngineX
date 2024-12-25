package org.nstep.engine.module.infra.framework.file.core.client.s3;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.nstep.engine.module.infra.framework.file.core.client.FileClientConfig;

/**
 * S3 文件客户端的配置类
 */
@Data
public class S3FileClientConfig implements FileClientConfig {

    public static final String ENDPOINT_QINIU = "qiniucs.com";
    public static final String ENDPOINT_ALIYUN = "aliyuncs.com";
    public static final String ENDPOINT_TENCENT = "myqcloud.com";
    public static final String ENDPOINT_VOLCES = "volces.com"; // 火山云（字节）

    /**
     * 节点地址
     * 1. MinIO：<a href="https://www.nstep.cn/Spring-Boot/MinIO">MinIO 文档</a>。例如说，http://127.0.0.1:9000
     * 2. 阿里云：<a href="https://help.aliyun.com/document_detail/31837.html">阿里云文档</a>
     * 3. 腾讯云：<a href="https://cloud.tencent.com/document/product/436/6224">腾讯云文档</a>
     * 4. 七牛云：<a href="https://developer.qiniu.com/kodo/4088/s3-access-domainname">七牛云文档</a>
     * 5. 华为云：<a href="https://developer.huaweicloud.com/endpoint?OBS">华为云文档</a>
     */
    @NotNull(message = "endpoint 不能为空")
    private String endpoint;

    /**
     * 自定义域名
     * 1. MinIO：通过 Nginx 配置
     * 2. 阿里云：<a href="https://help.aliyun.com/document_detail/31836.html">阿里云文档</a>
     * 3. 腾讯云：<a href="https://cloud.tencent.com/document/product/436/11142">腾讯云文档</a>
     * 4. 七牛云：<a href="https://developer.qiniu.com/kodo/8556/set-the-custom-source-domain-name">七牛云文档</a>
     * 5. 华为云：<a href="https://support.huaweicloud.com/usermanual-obs/obs_03_0032.html">华为云文档</a>
     */
    @URL(message = "domain 必须是 URL 格式")
    private String domain;

    /**
     * 存储 Bucket
     */
    @NotNull(message = "bucket 不能为空")
    private String bucket;

    /**
     * 访问 Key
     * 1. MinIO：<a href="https://www.nstep.cn/Spring-Boot/MinIO">MinIO 文档</a>
     * 2. 阿里云：<a href="https://ram.console.aliyun.com/manage/ak">阿里云文档</a>
     * 3. 腾讯云：<a href="https://console.cloud.tencent.com/cam/capi">腾讯云文档</a>
     * 4. 七牛云：<a href="https://portal.qiniu.com/user/key">七牛云文档</a>
     * 5. 华为云：<a href="https://support.huaweicloud.com/qs-obs/obs_qs_0005.html">华为云文档</a>
     */
    @NotNull(message = "accessKey 不能为空")
    private String accessKey;

    /**
     * 访问 Secret
     */
    @NotNull(message = "accessSecret 不能为空")
    private String accessSecret;

    /**
     * 校验域名是否合法
     * <p>
     * 该方法用于校验域名是否有效，特别是针对七牛云的情况，七牛云必须配置域名。
     * </p>
     *
     * @return 如果域名有效则返回 true，否则返回 false
     */
    @SuppressWarnings("RedundantIfStatement")
    @AssertTrue(message = "domain 不能为空")
    @JsonIgnore
    public boolean isDomainValid() {
        // 如果是七牛云，必须配置 domain
        if (StrUtil.contains(endpoint, ENDPOINT_QINIU) && StrUtil.isEmpty(domain)) {
            return false;  // 七牛云未配置域名时返回 false
        }
        return true;  // 否则返回 true
    }

}
