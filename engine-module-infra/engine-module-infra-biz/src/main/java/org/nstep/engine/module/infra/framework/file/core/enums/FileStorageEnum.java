package org.nstep.engine.module.infra.framework.file.core.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nstep.engine.module.infra.framework.file.core.client.FileClient;
import org.nstep.engine.module.infra.framework.file.core.client.FileClientConfig;
import org.nstep.engine.module.infra.framework.file.core.client.db.DBFileClient;
import org.nstep.engine.module.infra.framework.file.core.client.db.DBFileClientConfig;
import org.nstep.engine.module.infra.framework.file.core.client.ftp.FtpFileClient;
import org.nstep.engine.module.infra.framework.file.core.client.ftp.FtpFileClientConfig;
import org.nstep.engine.module.infra.framework.file.core.client.local.LocalFileClient;
import org.nstep.engine.module.infra.framework.file.core.client.local.LocalFileClientConfig;
import org.nstep.engine.module.infra.framework.file.core.client.s3.S3FileClient;
import org.nstep.engine.module.infra.framework.file.core.client.s3.S3FileClientConfig;
import org.nstep.engine.module.infra.framework.file.core.client.sftp.SftpFileClient;
import org.nstep.engine.module.infra.framework.file.core.client.sftp.SftpFileClientConfig;

/**
 * 文件存储器枚举
 */
@AllArgsConstructor
@Getter
public enum FileStorageEnum {

    DB(1, DBFileClientConfig.class, DBFileClient.class),

    LOCAL(10, LocalFileClientConfig.class, LocalFileClient.class),
    FTP(11, FtpFileClientConfig.class, FtpFileClient.class),
    SFTP(12, SftpFileClientConfig.class, SftpFileClient.class),

    S3(20, S3FileClientConfig.class, S3FileClient.class),
    ;

    /**
     * 存储器
     */
    private final Integer storage;

    /**
     * 配置类
     */
    private final Class<? extends FileClientConfig> configClass;
    /**
     * 客户端类
     */
    private final Class<? extends FileClient> clientClass;

    public static FileStorageEnum getByStorage(Integer storage) {
        return ArrayUtil.firstMatch(o -> o.getStorage().equals(storage), values());
    }

}
