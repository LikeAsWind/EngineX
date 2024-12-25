package org.nstep.engine.module.infra.dal.mysql.logger;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.mybatis.core.mapper.BaseMapperX;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogPageReqVO;
import org.nstep.engine.module.infra.dal.dataobject.logger.ApiErrorLogDO;

import java.time.LocalDateTime;

/**
 * API错误日志Mapper接口，继承自BaseMapperX，提供API错误日志相关的数据库操作。
 */
@Mapper
public interface ApiErrorLogMapper extends BaseMapperX<ApiErrorLogDO> {

    /**
     * 分页查询API错误日志信息。
     *
     * @param reqVO 分页查询参数，包含用户ID、用户类型、应用名称、请求URL等过滤条件。
     * @return 分页结果，包含符合条件的API错误日志信息。
     */
    default PageResult<ApiErrorLogDO> selectPage(ApiErrorLogPageReqVO reqVO) {
        // 构建查询条件，根据reqVO中的条件动态添加查询逻辑
        return selectPage(reqVO, new LambdaQueryWrapperX<ApiErrorLogDO>()
                .eqIfPresent(ApiErrorLogDO::getUserId, reqVO.getUserId()) // 如果reqVO中包含用户ID，则添加用户ID的等值查询条件
                .eqIfPresent(ApiErrorLogDO::getUserType, reqVO.getUserType()) // 如果reqVO中包含用户类型，则添加用户类型的等值查询条件
                .eqIfPresent(ApiErrorLogDO::getApplicationName, reqVO.getApplicationName()) // 如果reqVO中包含应用名称，则添加应用名称的等值查询条件
                .likeIfPresent(ApiErrorLogDO::getRequestUrl, reqVO.getRequestUrl()) // 如果reqVO中包含请求URL，则添加请求URL的模糊查询条件
                .betweenIfPresent(ApiErrorLogDO::getExceptionTime, reqVO.getExceptionTime()) // 如果reqVO中包含异常时间，则添加异常时间的区间查询条件
                .eqIfPresent(ApiErrorLogDO::getProcessStatus, reqVO.getProcessStatus()) // 如果reqVO中包含处理状态，则添加处理状态的等值查询条件
                .orderByDesc(ApiErrorLogDO::getId)); // 按照ID降序排序
    }

    /**
     * 物理删除指定时间之前的API错误日志。
     *
     * @param createTime 最大时间，删除创建时间小于该时间的日志。
     * @param limit      删除条数限制，防止一次性删除过多记录。
     * @return 删除的记录条数。
     */
    @Delete("DELETE FROM infra_api_error_log WHERE create_time < #{createTime} LIMIT #{limit}")
    Integer deleteByCreateTimeLt(@Param("createTime") LocalDateTime createTime, @Param("limit") Integer limit);

}