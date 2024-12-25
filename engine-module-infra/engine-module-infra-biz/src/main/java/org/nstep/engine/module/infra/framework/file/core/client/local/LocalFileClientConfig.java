package org.nstep.engine.module.infra.framework.file.core.client.local;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.nstep.engine.module.infra.framework.file.core.client.FileClientConfig;

/**
 * 本地文件客户端的配置类
 * <p>
 * 该类实现了 FileClientConfig 接口，定义了 LocalFileClient 所需的配置信息。
 * 配置类用于存储本地文件客户端的配置信息，如基础路径、域名等。
 * </p>
 */
@Data  // 自动生成 getter、setter、toString、equals 和 hashCode 方法
public class LocalFileClientConfig implements FileClientConfig {

    /**
     * 基础路径
     * <p>
     * 该属性用于指定文件存储的基础路径，上传的文件将以此路径为基础。
     * </p>
     */
    @NotEmpty(message = "基础路径不能为空")  // 验证 basePath 属性不能为空
    private String basePath;

    /**
     * 自定义域名
     * <p>
     * 该属性用于指定文件存储的自定义域名，必须符合 URL 格式。
     * </p>
     */
    @NotEmpty(message = "domain 不能为空")  // 验证 domain 属性不能为空
    @URL(message = "domain 必须是 URL 格式")  // 验证 domain 属性必须是有效的 URL 格式
    private String domain;

}
