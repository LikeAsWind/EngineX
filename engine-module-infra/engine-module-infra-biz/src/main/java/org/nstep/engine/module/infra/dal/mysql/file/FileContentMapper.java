package org.nstep.engine.module.infra.dal.mysql.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nstep.engine.module.infra.dal.dataobject.file.FileContentDO;

import java.util.List;

/**
 * 文件内容Mapper接口，继承自BaseMapper，提供文件内容相关的数据库操作。
 */
@Mapper
public interface FileContentMapper extends BaseMapper<FileContentDO> {

    /**
     * 根据配置ID和路径删除文件内容。
     *
     * @param configId 文件配置的ID。
     * @param path     文件的路径。
     */
    default void deleteByConfigIdAndPath(Long configId, String path) {
        // 构建删除条件，根据配置ID和路径精确匹配文件内容记录进行删除
        this.delete(new LambdaQueryWrapper<FileContentDO>()
                .eq(FileContentDO::getConfigId, configId) // 配置ID必须匹配
                .eq(FileContentDO::getPath, path)); // 路径必须匹配
    }

    /**
     * 根据配置ID和路径查询文件内容列表。
     *
     * @param configId 文件配置的ID。
     * @param path     文件的路径。
     * @return 符合条件的文件内容列表。
     */
    default List<FileContentDO> selectListByConfigIdAndPath(Long configId, String path) {
        // 构建查询条件，根据配置ID和路径精确匹配文件内容记录
        return selectList(new LambdaQueryWrapper<FileContentDO>()
                .eq(FileContentDO::getConfigId, configId) // 配置ID必须匹配
                .eq(FileContentDO::getPath, path)); // 路径必须匹配
    }

}