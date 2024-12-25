package org.nstep.engine.module.infra.framework.file.core.client.ftp;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.nstep.engine.module.infra.framework.file.core.client.FileClientConfig;

/**
 * Ftp 文件客户端的配置类
 * <p>
 * 该类实现了 FileClientConfig 接口，定义了 FtpFileClient 所需的配置信息。
 * 配置类用于存储 FTP 客户端的配置信息，如基础路径、主机地址、用户名、密码等。
 * </p>
 */
@Data  // 自动生成 getter、setter、toString、equals 和 hashCode 方法
public class FtpFileClientConfig implements FileClientConfig {

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

    /**
     * 主机地址
     * <p>
     * 该属性用于指定 FTP 服务器的主机地址。
     * </p>
     */
    @NotEmpty(message = "host 不能为空")  // 验证 host 属性不能为空
    private String host;

    /**
     * 主机端口
     * <p>
     * 该属性用于指定 FTP 服务器的端口号。
     * </p>
     */
    @NotNull(message = "port 不能为空")  // 验证 port 属性不能为空
    private Integer port;

    /**
     * 用户名
     * <p>
     * 该属性用于指定连接 FTP 服务器的用户名。
     * </p>
     */
    @NotEmpty(message = "用户名不能为空")  // 验证 username 属性不能为空
    private String username;

    /**
     * 密码
     * <p>
     * 该属性用于指定连接 FTP 服务器的密码。
     * </p>
     */
    @NotEmpty(message = "密码不能为空")  // 验证 password 属性不能为空
    private String password;

    /**
     * 连接模式
     * <p>
     * 该属性用于指定 FTP 客户端的连接模式。使用 {@link cn.hutool.extra.ftp.FtpMode} 对应的字符串。
     * </p>
     */
    @NotEmpty(message = "连接模式不能为空")  // 验证 mode 属性不能为空
    private String mode;

}
