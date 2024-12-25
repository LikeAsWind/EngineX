package org.nstep.engine.module.infra.convert.config;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.module.infra.controller.admin.config.vo.ConfigRespVO;
import org.nstep.engine.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import org.nstep.engine.module.infra.dal.dataobject.config.ConfigDO;

import java.util.List;

/**
 * 配置转换器接口，用于将配置相关的数据对象转换为响应值对象。
 */
@Mapper
public interface ConfigConvert {

    /**
     * 单例实例。
     */
    ConfigConvert INSTANCE = Mappers.getMapper(ConfigConvert.class);

    /**
     * 将配置分页结果转换为响应值对象的分页结果。
     *
     * @param page 配置分页结果。
     * @return 转换后的响应值对象的分页结果。
     */
    PageResult<ConfigRespVO> convertPage(PageResult<ConfigDO> page);

    /**
     * 将配置数据对象列表转换为响应值对象列表。
     *
     * @param list 配置数据对象列表。
     * @return 转换后的响应值对象列表。
     */
    List<ConfigRespVO> convertList(List<ConfigDO> list);

    /**
     * 将配置数据对象转换为响应值对象。
     *
     * @param bean 配置数据对象。
     * @return 转换后的响应值对象。
     */
    @Mapping(source = "configKey", target = "key")
    ConfigRespVO convert(ConfigDO bean);

    /**
     * 将配置保存请求值对象转换为配置数据对象。
     *
     * @param bean 配置保存请求值对象。
     * @return 转换后的配置数据对象。
     */
    @Mapping(source = "key", target = "configKey")
    ConfigDO convert(ConfigSaveReqVO bean);

}