package org.nstep.engine.module.infra.convert.codegen;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.apache.ibatis.type.JdbcType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.nstep.engine.framework.common.util.collection.CollectionUtils;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.CodegenDetailRespVO;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.CodegenPreviewRespVO;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.column.CodegenColumnRespVO;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.table.CodegenTableRespVO;
import org.nstep.engine.module.infra.dal.dataobject.codegen.CodegenColumnDO;
import org.nstep.engine.module.infra.dal.dataobject.codegen.CodegenTableDO;

import java.util.List;
import java.util.Map;

/**
 * 代码生成转换器接口，用于将数据库表信息转换为代码生成所需的数据对象。
 */
@Mapper
public interface CodegenConvert {

    /**
     * 单例实例。
     */
    CodegenConvert INSTANCE = Mappers.getMapper(CodegenConvert.class);

    // ========== TableInfo 相关 ==========

    /**
     * 将TableInfo对象转换为CodegenTableDO对象。
     *
     * @param bean TableInfo对象。
     * @return 转换后的CodegenTableDO对象。
     */
    @Mappings({
            @Mapping(source = "name", target = "tableName"),
            @Mapping(source = "comment", target = "tableComment"),
    })
    CodegenTableDO convert(TableInfo bean);

    /**
     * 将TableField列表转换为CodegenColumnDO列表。
     *
     * @param list TableField列表。
     * @return 转换后的CodegenColumnDO列表。
     */
    List<CodegenColumnDO> convertList(List<TableField> list);

    /**
     * 将TableField对象转换为CodegenColumnDO对象。
     *
     * @param bean TableField对象。
     * @return 转换后的CodegenColumnDO对象。
     */
    @Mappings({
            @Mapping(source = "name", target = "columnName"),
            @Mapping(source = "metaInfo.jdbcType", target = "dataType", qualifiedByName = "getDataType"),
            @Mapping(source = "comment", target = "columnComment"),
            @Mapping(source = "metaInfo.nullable", target = "nullable"),
            @Mapping(source = "keyFlag", target = "primaryKey"),
            @Mapping(source = "columnType.type", target = "javaType"),
            @Mapping(source = "propertyName", target = "javaField"),
    })
    CodegenColumnDO convert(TableField bean);

    /**
     * 获取JdbcType对应的数据类型名称。
     *
     * @param jdbcType JdbcType枚举值。
     * @return 数据类型名称。
     */
    @Named("getDataType")
    default String getDataType(JdbcType jdbcType) {
        return jdbcType.name();
    }

    // ========== 其它 ==========

    /**
     * 将CodegenTableDO对象和CodegenColumnDO列表转换为CodegenDetailRespVO对象。
     *
     * @param table   CodegenTableDO对象。
     * @param columns CodegenColumnDO列表。
     * @return 转换后的CodegenDetailRespVO对象。
     */
    default CodegenDetailRespVO convert(CodegenTableDO table, List<CodegenColumnDO> columns) {
        CodegenDetailRespVO respVO = new CodegenDetailRespVO();
        respVO.setTable(BeanUtils.toBean(table, CodegenTableRespVO.class));
        respVO.setColumns(BeanUtils.toBean(columns, CodegenColumnRespVO.class));
        return respVO;
    }

    /**
     * 将代码映射转换为CodegenPreviewRespVO列表。
     *
     * @param codes 代码映射。
     * @return 转换后的CodegenPreviewRespVO列表。
     */
    default List<CodegenPreviewRespVO> convert(Map<String, String> codes) {
        return CollectionUtils.convertList(codes.entrySet(),
                entry -> {
                    CodegenPreviewRespVO codegenPreviewRespVO = new CodegenPreviewRespVO();
                    codegenPreviewRespVO.setFilePath(entry.getKey());
                    codegenPreviewRespVO.setCode(entry.getValue());
                    return codegenPreviewRespVO;
                });
    }

}