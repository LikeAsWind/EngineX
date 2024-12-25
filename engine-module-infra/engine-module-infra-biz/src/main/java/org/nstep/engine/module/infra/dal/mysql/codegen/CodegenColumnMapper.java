package org.nstep.engine.module.infra.dal.mysql.codegen;

import org.apache.ibatis.annotations.Mapper;
import org.nstep.engine.framework.mybatis.core.mapper.BaseMapperX;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.module.infra.dal.dataobject.codegen.CodegenColumnDO;

import java.util.List;

/**
 * 代码生成列Mapper接口，继承自BaseMapperX，提供针对CodegenColumnDO的操作。
 */
@Mapper
public interface CodegenColumnMapper extends BaseMapperX<CodegenColumnDO> {

    /**
     * 根据表ID查询列信息列表。
     *
     * @param tableId 表ID。
     * @return 列信息列表。
     */
    default List<CodegenColumnDO> selectListByTableId(Long tableId) {
        // 使用LambdaQueryWrapperX构建查询条件，匹配表ID，并按顺序位置升序排序
        return selectList(new LambdaQueryWrapperX<CodegenColumnDO>()
                .eq(CodegenColumnDO::getTableId, tableId) // 等于条件
                .orderByAsc(CodegenColumnDO::getOrdinalPosition)); // 升序排序
    }

    /**
     * 根据表ID删除列信息列表。
     *
     * @param tableId 表ID。
     */
    default void deleteListByTableId(Long tableId) {
        // 使用LambdaQueryWrapperX构建删除条件，匹配表ID
        delete(new LambdaQueryWrapperX<CodegenColumnDO>()
                .eq(CodegenColumnDO::getTableId, tableId)); // 等于条件
    }

}