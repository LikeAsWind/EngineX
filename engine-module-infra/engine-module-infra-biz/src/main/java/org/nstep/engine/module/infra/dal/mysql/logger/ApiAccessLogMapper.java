package org.nstep.engine.module.infra.dal.mysql.logger;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.mybatis.core.mapper.BaseMapperX;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogPageReqVO;
import org.nstep.engine.module.infra.dal.dataobject.logger.ApiAccessLogDO;

import java.time.LocalDateTime;

/**
 * API 访问日志 Mapper接口，继承自BaseMapperX，提供API访问日志相关的数据库操作。
 */
@Mapper
public interface ApiAccessLogMapper extends BaseMapperX<ApiAccessLogDO> {

    /**
     * 分页查询API访问日志信息。
     *
     * @param reqVO 分页查询参数，包含用户ID、用户类型、应用名称、请求URL等过滤条件。
     * @return 分页结果，包含符合条件的API访问日志信息。
     */
    default PageResult<ApiAccessLogDO> selectPage(ApiAccessLogPageReqVO reqVO) {
        // 构建查询条件，根据reqVO中的条件动态添加查询逻辑
        return selectPage(reqVO, new LambdaQueryWrapperX<ApiAccessLogDO>()
                .eqIfPresent(ApiAccessLogDO::getUserId, reqVO.getUserId()) // 如果reqVO中包含用户ID，则添加用户ID的等值查询条件
                .eqIfPresent(ApiAccessLogDO::getUserType, reqVO.getUserType()) // 如果reqVO中包含用户类型，则添加用户类型的等值查询条件
                .eqIfPresent(ApiAccessLogDO::getApplicationName, reqVO.getApplicationName()) // 如果reqVO中包含应用名称，则添加应用名称的等值查询条件
                .likeIfPresent(ApiAccessLogDO::getRequestUrl, reqVO.getRequestUrl()) // 如果reqVO中包含请求URL，则添加请求URL的模糊查询条件
                .betweenIfPresent(ApiAccessLogDO::getBeginTime, reqVO.getBeginTime()) // 如果reqVO中包含开始时间，则添加开始时间的区间查询条件
                .geIfPresent(ApiAccessLogDO::getDuration, reqVO.getDuration()) // 如果reqVO中包含持续时间，则添加持续时间的大于等于查询条件
                .eqIfPresent(ApiAccessLogDO::getResultCode, reqVO.getResultCode()) // 如果reqVO中包含结果代码，则添加结果代码的等值查询条件
                .orderByDesc(ApiAccessLogDO::getId)); // 按照ID降序排序
    }

    /**
     * 物理删除指定时间之前的日志。
     *
     * @param createTime 最大时间，删除创建时间小于该时间的日志。
     * @param limit      删除条数限制，防止一次性删除过多记录。
     * @return 删除的记录条数。
     */
    @Delete("DELETE FROM infra_api_access_log WHERE create_time < #{createTime} LIMIT #{limit}")
    Integer deleteByCreateTimeLt(@Param("createTime") LocalDateTime createTime, @Param("limit") Integer limit);

}