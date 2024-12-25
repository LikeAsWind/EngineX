package org.nstep.engine.module.infra.framework.file.core.client.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.nstep.engine.module.infra.dal.dataobject.file.FileContentDO;
import org.nstep.engine.module.infra.dal.mysql.file.FileContentMapper;
import org.nstep.engine.module.infra.framework.file.core.client.AbstractFileClient;

import java.util.Comparator;
import java.util.List;

/**
 * 基于 DB 存储的文件客户端的配置类
 * <p>
 * 该类继承自 AbstractFileClient，提供基于数据库存储的文件上传、删除和获取功能。
 * 通过与数据库交互，管理文件的存储和读取。
 * </p>
 */
public class DBFileClient extends AbstractFileClient<DBFileClientConfig> {

    // 文件内容的数据库映射器，用于操作文件内容的数据库记录
    private FileContentMapper fileContentMapper;

    /**
     * 构造方法，初始化 DBFileClient 实例
     *
     * @param id     文件客户端的 ID
     * @param config 配置信息
     */
    public DBFileClient(Long id, DBFileClientConfig config) {
        super(id, config);  // 调用父类构造方法，初始化文件客户端
    }

    /**
     * 初始化方法，用于初始化 DBFileClient 所需的资源
     * <p>
     * 在该方法中，通过 Spring 工具类获取 FileContentMapper 实例，用于操作文件内容的数据库。
     * </p>
     */
    @Override
    protected void doInit() {
        fileContentMapper = SpringUtil.getBean(FileContentMapper.class);  // 获取 FileContentMapper Bean
    }

    /**
     * 上传文件内容到数据库
     * <p>
     * 该方法将文件内容存储到数据库中，并返回文件的访问路径。
     * </p>
     *
     * @param content 文件内容的字节数组
     * @param path    文件存储路径
     * @param type    文件类型
     * @return 文件的访问路径
     */
    @Override
    public String upload(byte[] content, String path, String type) {
        FileContentDO contentDO = new FileContentDO();
        contentDO.setConfigId(getId());  // 设置配置 ID
        contentDO.setPath(path);  // 设置文件路径
        contentDO.setContent(content);  // 设置文件内容
        fileContentMapper.insert(contentDO);  // 将文件内容插入数据库
        // 拼接并返回文件的访问路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    /**
     * 删除指定路径的文件
     * <p>
     * 该方法通过配置 ID 和文件路径从数据库中删除文件记录。
     * </p>
     *
     * @param path 文件路径
     */
    @Override
    public void delete(String path) {
        fileContentMapper.deleteByConfigIdAndPath(getId(), path);  // 删除指定路径的文件记录
    }

    /**
     * 获取指定路径的文件内容
     * <p>
     * 该方法从数据库中查询文件内容，并返回最新上传的文件内容。
     * </p>
     *
     * @param path 文件路径
     * @return 文件内容的字节数组，如果文件不存在则返回 null
     */
    @Override
    public byte[] getContent(String path) {
        List<FileContentDO> list = fileContentMapper.selectListByConfigIdAndPath(getId(), path);  // 查询文件记录
        if (CollUtil.isEmpty(list)) {
            return null;  // 如果没有找到文件，返回 null
        }
        // 排序文件记录，拿 id 最大的，即最后上传的文件
        list.sort(Comparator.comparing(FileContentDO::getId));
        return CollUtil.getLast(list).getContent();  // 返回最新上传的文件内容
    }

}
