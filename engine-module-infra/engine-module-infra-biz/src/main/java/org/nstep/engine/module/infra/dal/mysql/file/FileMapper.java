package org.nstep.engine.module.infra.dal.mysql.file;

import org.apache.ibatis.annotations.Mapper;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.mybatis.core.mapper.BaseMapperX;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import org.nstep.engine.module.infra.dal.dataobject.file.FileDO;

/**
 * 文件操作 Mapper接口，继承自BaseMapperX，提供文件相关的数据库操作。
 */
@Mapper
public interface FileMapper extends BaseMapperX<FileDO> {

    /**
     * 分页查询文件信息。
     *
     * @param reqVO 分页查询参数，包含文件的路径、类型等过滤条件。
     * @return 分页结果，包含符合条件的文件信息。
     */
    default PageResult<FileDO> selectPage(FilePageReqVO reqVO) {
        // 构建查询条件，根据reqVO中的条件动态添加查询逻辑
        return selectPage(reqVO, new LambdaQueryWrapperX<FileDO>()
                .likeIfPresent(FileDO::getPath, reqVO.getPath()) // 如果reqVO中包含路径，则添加路径的模糊查询条件
                .likeIfPresent(FileDO::getType, reqVO.getType()) // 如果reqVO中包含类型，则添加类型的模糊查询条件
                .betweenIfPresent(FileDO::getCreateTime, reqVO.getCreateTime()) // 如果reqVO中包含创建时间范围，则添加创建时间的区间查询条件
                .orderByDesc(FileDO::getId)); // 按照ID降序排序
    }

}