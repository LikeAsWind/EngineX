package org.nstep.engine.module.infra.dal.mysql.codegen;

import org.apache.ibatis.annotations.Mapper;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.mybatis.core.mapper.BaseMapperX;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.table.CodegenTablePageReqVO;
import org.nstep.engine.module.infra.dal.dataobject.codegen.CodegenTableDO;

import java.util.List;

/**
 * 代码生成表Mapper接口，继承自BaseMapperX，提供针对CodegenTableDO的操作。
 */
@Mapper
public interface CodegenTableMapper extends BaseMapperX<CodegenTableDO> {

    /**
     * 根据表名和数据源配置ID查询CodegenTableDO对象。
     *
     * @param tableName          表名。
     * @param dataSourceConfigId 数据源配置ID。
     * @return 查询到的CodegenTableDO对象。
     */
    default CodegenTableDO selectByTableNameAndDataSourceConfigId(String tableName, Long dataSourceConfigId) {
        // 使用selectOne方法，根据表名和数据源配置ID查询单个CodegenTableDO对象
        return selectOne(CodegenTableDO::getTableName, tableName,
                CodegenTableDO::getDataSourceConfigId, dataSourceConfigId);
    }

    /**
     * 分页查询CodegenTableDO对象。
     *
     * @param pageReqVO 分页请求值对象。
     * @return 分页结果。
     */
    default PageResult<CodegenTableDO> selectPage(CodegenTablePageReqVO pageReqVO) {
        // 使用LambdaQueryWrapperX构建查询条件，包含模糊匹配、时间范围匹配和排序
        return selectPage(pageReqVO, new LambdaQueryWrapperX<CodegenTableDO>()
                .likeIfPresent(CodegenTableDO::getTableName, pageReqVO.getTableName()) // 表名模糊匹配
                .likeIfPresent(CodegenTableDO::getTableComment, pageReqVO.getTableComment()) // 表注释模糊匹配
                .likeIfPresent(CodegenTableDO::getClassName, pageReqVO.getClassName()) // 类名模糊匹配
                .betweenIfPresent(CodegenTableDO::getCreateTime, pageReqVO.getCreateTime()) // 创建时间范围匹配
                .orderByDesc(CodegenTableDO::getUpdateTime) // 更新时间降序排序
        );
    }

    /**
     * 根据数据源配置ID查询CodegenTableDO列表。
     *
     * @param dataSourceConfigId 数据源配置ID。
     * @return 根据数据源配置ID查询到的CodegenTableDO列表。
     */
    default List<CodegenTableDO> selectListByDataSourceConfigId(Long dataSourceConfigId) {
        // 使用selectList方法，根据数据源配置ID查询CodegenTableDO列表
        return selectList(CodegenTableDO::getDataSourceConfigId, dataSourceConfigId);
    }

    /**
     * 根据模板类型和主表ID查询CodegenTableDO列表。
     *
     * @param templateType  模板类型。
     * @param masterTableId 主表ID。
     * @return 根据模板类型和主表ID查询到的CodegenTableDO列表。
     */
    default List<CodegenTableDO> selectListByTemplateTypeAndMasterTableId(Integer templateType, Long masterTableId) {
        // 使用selectList方法，根据模板类型和主表ID查询CodegenTableDO列表
        return selectList(CodegenTableDO::getTemplateType, templateType,
                CodegenTableDO::getMasterTableId, masterTableId);
    }

}