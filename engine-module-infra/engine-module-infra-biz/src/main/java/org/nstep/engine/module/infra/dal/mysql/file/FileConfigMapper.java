package org.nstep.engine.module.infra.dal.mysql.file;

import org.apache.ibatis.annotations.Mapper;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.mybatis.core.mapper.BaseMapperX;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.module.infra.controller.admin.file.vo.config.FileConfigPageReqVO;
import org.nstep.engine.module.infra.dal.dataobject.file.FileConfigDO;

/**
 * 文件配置Mapper接口，继承自BaseMapperX，提供文件配置相关的数据库操作。
 */
@Mapper
public interface FileConfigMapper extends BaseMapperX<FileConfigDO> {

    /**
     * 分页查询文件配置信息。
     *
     * @param reqVO 分页查询参数，包含文件配置的名称、存储类型等过滤条件。
     * @return 分页结果，包含符合条件的文件配置信息。
     */
    default PageResult<FileConfigDO> selectPage(FileConfigPageReqVO reqVO) {
        // 构建查询条件，根据reqVO中的条件动态添加查询逻辑
        return selectPage(reqVO, new LambdaQueryWrapperX<FileConfigDO>()
                .likeIfPresent(FileConfigDO::getName, reqVO.getName()) // 如果reqVO中包含名称，则添加名称的模糊查询条件
                .eqIfPresent(FileConfigDO::getStorage, reqVO.getStorage()) // 如果reqVO中包含存储类型，则添加存储类型的等值查询条件
                .betweenIfPresent(FileConfigDO::getCreateTime, reqVO.getCreateTime()) // 如果reqVO中包含创建时间范围，则添加创建时间的区间查询条件
                .orderByDesc(FileConfigDO::getId)); // 按照ID降序排序
    }

    /**
     * 查询主文件配置信息。
     *
     * @return 主文件配置信息，如果存在则返回，否则返回null。
     */
    default FileConfigDO selectByMaster() {
        // 根据主文件配置标识查询文件配置信息
        return selectOne(FileConfigDO::getMaster, true);
    }
}