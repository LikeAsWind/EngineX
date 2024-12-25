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
 * 该枚举定义了不同的文件存储类型，每个类型关联了对应的配置类和客户端类。
 */
@AllArgsConstructor
@Getter
public enum FileStorageEnum {

    DB(1, DBFileClientConfig.class, DBFileClient.class), // 数据库存储

    LOCAL(10, LocalFileClientConfig.class, LocalFileClient.class), // 本地文件存储
    FTP(11, FtpFileClientConfig.class, FtpFileClient.class), // FTP 存储
    SFTP(12, SftpFileClientConfig.class, SftpFileClient.class), // SFTP 存储

    S3(20, S3FileClientConfig.class, S3FileClient.class), // S3 存储

    ;

    /**
     * 存储器类型的编号
     */
    private final Integer storage;

    /**
     * 存储类型对应的配置类
     */
    private final Class<? extends FileClientConfig> configClass;

    /**
     * 存储类型对应的客户端类
     */
    private final Class<? extends FileClient> clientClass;

    /**
     * 根据存储类型编号获取对应的存储类型枚举
     *
     * @param storage 存储类型编号
     * @return 对应的存储类型枚举
     */
    public static FileStorageEnum getByStorage(Integer storage) {
        return ArrayUtil.firstMatch(o -> o.getStorage().equals(storage), values());
    }

}
