package org.nstep.engine.module.infra.service.logger;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;
import org.nstep.engine.framework.tenant.core.util.TenantUtils;
import org.nstep.engine.module.infra.api.logger.dto.ApiAccessLogCreateReqDTO;
import org.nstep.engine.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogPageReqVO;
import org.nstep.engine.module.infra.dal.dataobject.logger.ApiAccessLogDO;
import org.nstep.engine.module.infra.dal.mysql.logger.ApiAccessLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static org.nstep.engine.module.infra.dal.dataobject.logger.ApiAccessLogDO.REQUEST_PARAMS_MAX_LENGTH;
import static org.nstep.engine.module.infra.dal.dataobject.logger.ApiAccessLogDO.RESULT_MSG_MAX_LENGTH;

/**
 * API 访问日志 Service 实现类
 * <p>
 * 该类实现了 ApiAccessLogService 接口，提供了 API 访问日志的增删查功能。
 * 主要功能包括：
 * 1. 创建 API 访问日志
 * 2. 分页查询 API 访问日志
 * 3. 清理超过指定天数的日志
 */
@Slf4j
@Service
@Validated
public class ApiAccessLogServiceImpl implements ApiAccessLogService {

    @Resource
    private ApiAccessLogMapper apiAccessLogMapper;

    /**
     * 创建 API 访问日志
     * <p>
     * 该方法将传入的日志创建请求数据转换为数据库实体，并插入数据库。如果上下文中存在租户 ID，则插入时使用租户上下文，否则忽略租户上下文进行插入。
     *
     * @param createDTO API 访问日志创建请求 DTO
     */
    @Override
    public void createApiAccessLog(ApiAccessLogCreateReqDTO createDTO) {
        // 将 DTO 转换为 DO
        ApiAccessLogDO apiAccessLog = BeanUtils.toBean(createDTO, ApiAccessLogDO.class);
        // 限制请求参数和返回结果的最大长度
        apiAccessLog.setRequestParams(StrUtil.maxLength(apiAccessLog.getRequestParams(), REQUEST_PARAMS_MAX_LENGTH));
        apiAccessLog.setResultMsg(StrUtil.maxLength(apiAccessLog.getResultMsg(), RESULT_MSG_MAX_LENGTH));

        // 判断是否有租户 ID
        if (TenantContextHolder.getTenantId() != null) {
            // 插入日志数据
            apiAccessLogMapper.insert(apiAccessLog);
        } else {
            // 如果没有租户上下文，忽略租户执行插入
            TenantUtils.executeIgnore(() -> apiAccessLogMapper.insert(apiAccessLog));
        }
    }

    /**
     * 分页查询 API 访问日志
     * <p>
     * 该方法根据请求的分页参数查询 API 访问日志数据，并返回分页结果。
     *
     * @param pageReqVO 分页请求 VO
     * @return 分页结果，包含 API 访问日志数据
     */
    @Override
    public PageResult<ApiAccessLogDO> getApiAccessLogPage(ApiAccessLogPageReqVO pageReqVO) {
        // 调用 Mapper 查询分页数据
        return apiAccessLogMapper.selectPage(pageReqVO);
    }

    /**
     * 清理超过指定天数的访问日志
     * <p>
     * 该方法会删除超过指定天数的日志记录，删除时会限制每次删除的条数，避免对数据库造成过大压力。
     * 循环删除直到没有满足条件的数据。
     *
     * @param exceedDay   需要删除的日志超过的天数
     * @param deleteLimit 每次删除的最大条数
     * @return 删除的日志条数
     */
    @Override
    @SuppressWarnings("DuplicatedCode")
    public Integer cleanAccessLog(Integer exceedDay, Integer deleteLimit) {
        int count = 0;
        // 计算过期时间，超过 exceedDay 天的日志将被删除
        LocalDateTime expireDate = LocalDateTime.now().minusDays(exceedDay);

        // 循环删除，直到没有满足条件的数据
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 删除符合条件的日志数据
            int deleteCount = apiAccessLogMapper.deleteByCreateTimeLt(expireDate, deleteLimit);
            count += deleteCount;
            // 如果删除的条数小于限制条数，说明没有更多数据可以删除
            if (deleteCount < deleteLimit) {
                break;
            }
        }
        return count;
    }

}
