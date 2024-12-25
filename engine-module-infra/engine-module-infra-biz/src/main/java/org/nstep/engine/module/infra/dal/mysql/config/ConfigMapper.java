package org.nstep.engine.module.infra.dal.mysql.config;

import org.apache.ibatis.annotations.Mapper;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.mybatis.core.mapper.BaseMapperX;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.module.infra.controller.admin.config.vo.ConfigPageReqVO;
import org.nstep.engine.module.infra.dal.dataobject.config.ConfigDO;

/**
 * 配置Mapper接口，继承自BaseMapperX，提供针对ConfigDO的操作。
 */
@Mapper
public interface ConfigMapper extends BaseMapperX<ConfigDO> {

    /**
     * 根据配置键查询配置对象。
     *
     * @param key 配置键。
     * @return 查询到的ConfigDO对象。
     */
    default ConfigDO selectByKey(String key) {
        // 使用selectOne方法，根据配置键查询单个ConfigDO对象
        return selectOne(ConfigDO::getConfigKey, key);
    }

    /**
     * 分页查询配置对象。
     *
     * @param reqVO 分页请求值对象。
     * @return 分页结果。
     */
    default PageResult<ConfigDO> selectPage(ConfigPageReqVO reqVO) {
        // 使用LambdaQueryWrapperX构建查询条件，包含模糊匹配、相等匹配和时间范围匹配
        return selectPage(reqVO, new LambdaQueryWrapperX<ConfigDO>()
                .likeIfPresent(ConfigDO::getName, reqVO.getName()) // 根据名称模糊匹配
                .likeIfPresent(ConfigDO::getConfigKey, reqVO.getKey()) // 根据配置键模糊匹配
                .eqIfPresent(ConfigDO::getType, reqVO.getType()) // 根据类型进行相等匹配
                .betweenIfPresent(ConfigDO::getCreateTime, reqVO.getCreateTime())); // 根据创建时间范围匹配
    }

}