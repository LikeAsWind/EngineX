package org.nstep.engine.module.infra.service.logger;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;
import org.nstep.engine.framework.tenant.core.util.TenantUtils;
import org.nstep.engine.module.infra.api.logger.dto.ApiErrorLogCreateReqDTO;
import org.nstep.engine.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogPageReqVO;
import org.nstep.engine.module.infra.dal.dataobject.logger.ApiErrorLogDO;
import org.nstep.engine.module.infra.dal.mysql.logger.ApiErrorLogMapper;
import org.nstep.engine.module.infra.enums.logger.ApiErrorLogProcessStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.nstep.engine.module.infra.dal.dataobject.logger.ApiErrorLogDO.REQUEST_PARAMS_MAX_LENGTH;
import static org.nstep.engine.module.infra.enums.ErrorCodeConstants.API_ERROR_LOG_NOT_FOUND;
import static org.nstep.engine.module.infra.enums.ErrorCodeConstants.API_ERROR_LOG_PROCESSED;

/**
 * API 错误日志 Service 实现类
 * <p>
 * 该类提供了对 API 错误日志的增删改查功能，包括创建日志、分页查询日志、更新日志处理状态以及清理过期的错误日志。
 */
@Service
@Validated
@Slf4j
public class ApiErrorLogServiceImpl implements ApiErrorLogService {

    @Resource
    private ApiErrorLogMapper apiErrorLogMapper;

    /**
     * 创建 API 错误日志
     * <p>
     * 根据传入的错误日志请求 DTO，创建一个新的错误日志对象，并插入数据库。如果上下文中存在租户信息，则使用租户信息插入；否则，忽略租户信息。
     *
     * @param createDTO 包含错误日志数据的 DTO
     */
    @Override
    public void createApiErrorLog(ApiErrorLogCreateReqDTO createDTO) {
        // 将 DTO 转换为 DO（数据对象）
        ApiErrorLogDO apiErrorLog = BeanUtils.toBean(createDTO, ApiErrorLogDO.class);
        // 设置日志的处理状态为初始化状态
        apiErrorLog.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus());
        // 限制请求参数的最大长度
        apiErrorLog.setRequestParams(StrUtil.maxLength(apiErrorLog.getRequestParams(), REQUEST_PARAMS_MAX_LENGTH));

        // 判断租户 ID 是否存在，如果存在则直接插入数据，否则忽略租户上下文插入
        if (TenantContextHolder.getTenantId() != null) {
            apiErrorLogMapper.insert(apiErrorLog);
        } else {
            // 极端情况下，上下文中没有租户时，忽略租户上下文，避免插入失败
            TenantUtils.executeIgnore(() -> apiErrorLogMapper.insert(apiErrorLog));
        }
    }

    /**
     * 获取 API 错误日志的分页结果
     * <p>
     * 根据分页请求参数查询错误日志，并返回分页结果。
     *
     * @param pageReqVO 包含分页参数的请求对象
     * @return 分页结果
     */
    @Override
    public PageResult<ApiErrorLogDO> getApiErrorLogPage(ApiErrorLogPageReqVO pageReqVO) {
        return apiErrorLogMapper.selectPage(pageReqVO);
    }

    /**
     * 更新 API 错误日志的处理状态
     * <p>
     * 根据日志 ID 更新错误日志的处理状态。如果日志不存在或已处理，则抛出异常。
     *
     * @param id            错误日志 ID
     * @param processStatus 处理状态
     * @param processUserId 处理用户 ID
     */
    @Override
    public void updateApiErrorLogProcess(Long id, Integer processStatus, Long processUserId) {
        // 查询错误日志
        ApiErrorLogDO errorLog = apiErrorLogMapper.selectById(id);

        // 如果日志不存在，抛出异常
        if (errorLog == null) {
            throw exception(API_ERROR_LOG_NOT_FOUND);
        }

        // 如果日志已经处理过，抛出异常
        if (!ApiErrorLogProcessStatusEnum.INIT.getStatus().equals(errorLog.getProcessStatus())) {
            throw exception(API_ERROR_LOG_PROCESSED);
        }

        // 更新日志的处理状态
        apiErrorLogMapper.updateById(ApiErrorLogDO.builder().id(id).processStatus(processStatus)
                .processUserId(processUserId).processTime(LocalDateTime.now()).build());
    }

    /**
     * 清理过期的错误日志
     * <p>
     * 删除超过指定天数的错误日志，直到删除达到设定的最大限制。
     *
     * @param exceedDay   超过多少天的日志需要被清理
     * @param deleteLimit 每次删除的最大条数
     * @return 删除的日志数量
     */
    @Override
    @SuppressWarnings("DuplicatedCode")
    public Integer cleanErrorLog(Integer exceedDay, Integer deleteLimit) {
        int count = 0;
        // 计算过期的日期
        LocalDateTime expireDate = LocalDateTime.now().minusDays(exceedDay);

        // 循环删除，直到没有满足条件的数据
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 删除满足条件的日志
            int deleteCount = apiErrorLogMapper.deleteByCreateTimeLt(expireDate, deleteLimit);
            count += deleteCount;
            // 达到删除预期条数，说明到底了
            if (deleteCount < deleteLimit) {
                break;
            }
        }
        return count;
    }

}
