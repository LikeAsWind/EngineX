package org.nstep.engine.module.system.api.logger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.module.system.api.logger.dto.OperateLogCreateReqDTO;
import org.nstep.engine.module.system.api.logger.dto.OperateLogPageReqDTO;
import org.nstep.engine.module.system.api.logger.dto.OperateLogRespDTO;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 操作日志服务接口
 * <p>
 * 该接口提供了创建和查询操作日志的功能。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 操作日志")
public interface OperateLogApi {

    String PREFIX = ApiConstants.PREFIX + "/operate-log";

    /**
     * 创建操作日志
     * <p>
     * 该方法接收一个操作日志创建请求，保存操作日志信息。
     *
     * @param createReqDTO 操作日志创建请求数据
     * @return 创建结果，返回一个布尔值表示是否成功
     */
    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建操作日志")
    CommonResult<Boolean> createOperateLog(@Valid @RequestBody OperateLogCreateReqDTO createReqDTO);

    /**
     * 【异步】创建操作日志
     * <p>
     * 该方法异步创建操作日志，避免阻塞调用线程。
     *
     * @param createReqDTO 操作日志创建请求数据
     */
    @Async
    default void createOperateLogAsync(OperateLogCreateReqDTO createReqDTO) {
        createOperateLog(createReqDTO).checkError();
    }

    /**
     * 获取指定模块的指定数据的操作日志分页
     * <p>
     * 该方法用于分页查询操作日志，根据模块和数据筛选日志。
     *
     * @param pageReqDTO 分页请求数据
     * @return 操作日志分页结果
     */
    @GetMapping(PREFIX + "/page")
    @Operation(summary = "获取指定模块的指定数据的操作日志分页")
    CommonResult<PageResult<OperateLogRespDTO>> getOperateLogPage(@SpringQueryMap OperateLogPageReqDTO pageReqDTO);

}
