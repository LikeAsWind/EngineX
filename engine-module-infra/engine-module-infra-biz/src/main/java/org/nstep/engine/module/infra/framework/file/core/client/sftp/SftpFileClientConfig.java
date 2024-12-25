package org.nstep.engine.module.infra.framework.file.core.client.sftp;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.nstep.engine.module.infra.framework.file.core.client.FileClientConfig;

/**
 * SFTP 文件客户端的配置类，包含连接 SFTP 服务器所需的所有配置项。
 */
@Data
public class SftpFileClientConfig implements FileClientConfig {

    /**
     * 基础路径，指定文件存储的根目录路径。
     * 必须提供，不能为空。
     */
    @NotEmpty(message = "基础路径不能为空")
    private String basePath;

    /**
     * 自定义域名，用于访问文件的域名。
     * 必须提供，不能为空，且必须是有效的 URL 格式。
     */
    @NotEmpty(message = "domain 不能为空")
    @URL(message = "domain 必须是 URL 格式")
    private String domain;

    /**
     * SFTP 主机地址，用于连接 SFTP 服务器。
     * 必须提供，不能为空。
     */
    @NotEmpty(message = "host 不能为空")
    private String host;

    /**
     * SFTP 主机端口，指定连接的端口。
     * 必须提供，不能为空。
     */
    @NotNull(message = "port 不能为空")
    private Integer port;

    /**
     * SFTP 用户名，用于身份验证。
     * 必须提供，不能为空。
     */
    @NotEmpty(message = "用户名不能为空")
    private String username;

    /**
     * SFTP 密码，用于身份验证。
     * 必须提供，不能为空。
     */
    @NotEmpty(message = "密码不能为空")
    private String password;

}
