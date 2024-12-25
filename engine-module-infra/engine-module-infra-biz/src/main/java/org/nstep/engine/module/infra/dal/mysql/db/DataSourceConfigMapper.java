package org.nstep.engine.module.infra.dal.mysql.db;

import org.apache.ibatis.annotations.Mapper;
import org.nstep.engine.framework.mybatis.core.mapper.BaseMapperX;
import org.nstep.engine.module.infra.dal.dataobject.db.DataSourceConfigDO;

/**
 * 数据源配置Mapper接口，继承自BaseMapperX，提供针对DataSourceConfigDO的操作。
 * 该接口目前为空，没有定义具体的方法，可能在后续开发中会添加。
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
    // 该接口中尚未定义任何方法，可以添加自定义的数据源配置相关数据库操作。
}