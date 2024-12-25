package org.nstep.engine.module.infra.convert.file;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.nstep.engine.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import org.nstep.engine.module.infra.dal.dataobject.file.FileConfigDO;

/**
 * 文件配置转换器接口，用于将文件配置相关的请求值对象转换为数据对象。
 */
@Mapper
public interface FileConfigConvert {

    /**
     * 单例实例。
     */
    FileConfigConvert INSTANCE = Mappers.getMapper(FileConfigConvert.class);

    /**
     * 将文件配置保存请求值对象转换为文件配置数据对象。
     * 在转换过程中，忽略config属性。
     *
     * @param bean 文件配置保存请求值对象。
     * @return 转换后的文件配置数据对象。
     */
    @Mapping(target = "config", ignore = true)
    FileConfigDO convert(FileConfigSaveReqVO bean);

}