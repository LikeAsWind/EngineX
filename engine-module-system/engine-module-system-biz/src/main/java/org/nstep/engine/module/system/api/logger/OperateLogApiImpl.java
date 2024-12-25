package org.nstep.engine.module.system.api.logger;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.system.api.logger.dto.OperateLogCreateReqDTO;
import org.nstep.engine.module.system.api.logger.dto.OperateLogPageReqDTO;
import org.nstep.engine.module.system.api.logger.dto.OperateLogRespDTO;
import org.nstep.engine.module.system.dal.dataobject.logger.OperateLogDO;
import org.nstep.engine.module.system.service.logger.OperateLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 操作日志 API 实现类
 * <p>
 * 该类提供了操作日志相关的 RESTful API 接口，供 Feign 调用。
 * 主要功能包括创建操作日志和分页查询操作日志。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class OperateLogApiImpl implements OperateLogApi {

    @Resource
    private OperateLogService operateLogService; // 操作日志服务，处理操作日志相关的业务逻辑

    /**
     * 创建操作日志
     *
     * @param createReqDTO 操作日志创建请求 DTO
     * @return 创建成功的响应
     */
    @Override
    public CommonResult<Boolean> createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        // 调用操作日志服务创建操作日志
        operateLogService.createOperateLog(createReqDTO);
        // 返回成功响应
        return success(true);
    }

    /**
     * 分页查询操作日志
     *
     * @param pageReqDTO 分页查询请求 DTO
     * @return 操作日志分页查询结果
     */
    @Override
    public CommonResult<PageResult<OperateLogRespDTO>> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        // 调用操作日志服务进行分页查询
        PageResult<OperateLogDO> operateLogPage = operateLogService.getOperateLogPage(pageReqDTO);
        // 将操作日志数据对象转换为响应 DTO 并返回
        return success(BeanUtils.toBean(operateLogPage, OperateLogRespDTO.class));
    }

}
