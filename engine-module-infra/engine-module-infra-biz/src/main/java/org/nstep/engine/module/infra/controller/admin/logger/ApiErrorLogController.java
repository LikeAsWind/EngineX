package org.nstep.engine.module.infra.controller.admin.logger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.nstep.engine.framework.apilog.core.annotation.ApiAccessLog;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.framework.excel.core.util.ExcelUtils;
import org.nstep.engine.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogPageReqVO;
import org.nstep.engine.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;
import org.nstep.engine.module.infra.dal.dataobject.logger.ApiErrorLogDO;
import org.nstep.engine.module.infra.service.logger.ApiErrorLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.nstep.engine.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static org.nstep.engine.framework.common.pojo.CommonResult.success;
import static org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 管理后台 - API 错误日志控制器
 * 提供 API 错误日志的状态更新、分页查询和导出 Excel 功能。
 */
@Tag(name = "管理后台 - API 错误日志")
@RestController
@RequestMapping("/infra/api-error-log")
@Validated
public class ApiErrorLogController {

    @Resource
    private ApiErrorLogService apiErrorLogService; // API 错误日志服务

    /**
     * 更新 API 错误日志的处理状态
     *
     * @param id            错误日志的编号
     * @param processStatus 处理状态
     * @return 返回更新是否成功的结果
     */
    @PutMapping("/update-status")
    @Operation(summary = "更新 API 错误日志的状态")
    @Parameters({
            @Parameter(name = "id", description = "编号", required = true, example = "1024"),
            @Parameter(name = "processStatus", description = "处理状态", required = true, example = "1")
    })
    @PreAuthorize("@ss.hasPermission('infra:api-error-log:update-status')")
    public CommonResult<Boolean> updateApiErrorLogProcess(@RequestParam("id") Long id,
                                                          @RequestParam("processStatus") Integer processStatus) {
        // 调用服务层方法更新错误日志状态
        apiErrorLogService.updateApiErrorLogProcess(id, processStatus, getLoginUserId());
        // 返回成功的结果
        return success(true);
    }

    /**
     * 获取 API 错误日志的分页数据
     *
     * @param pageReqVO 分页请求参数
     * @return 返回分页结果，包括日志列表和分页信息
     */
    @GetMapping("/page")
    @Operation(summary = "获得 API 错误日志分页")
    @PreAuthorize("@ss.hasPermission('infra:api-error-log:query')")
    public CommonResult<PageResult<ApiErrorLogRespVO>> getApiErrorLogPage(@Valid ApiErrorLogPageReqVO pageReqVO) {
        // 调用服务层方法获取分页数据
        PageResult<ApiErrorLogDO> pageResult = apiErrorLogService.getApiErrorLogPage(pageReqVO);
        // 将分页数据转换为响应对象并返回
        return success(BeanUtils.toBean(pageResult, ApiErrorLogRespVO.class));
    }

    /**
     * 导出 API 错误日志为 Excel 文件
     *
     * @param exportReqVO 导出请求参数
     * @param response    HTTP 响应，用于返回 Excel 文件
     * @throws IOException 如果导出过程中发生 I/O 错误
     */
    @GetMapping("/export-excel")
    @Operation(summary = "导出 API 错误日志 Excel")
    @PreAuthorize("@ss.hasPermission('infra:api-error-log:export')")
    @ApiAccessLog(operateType = EXPORT) // 记录操作日志
    public void exportApiErrorLogExcel(@Valid ApiErrorLogPageReqVO exportReqVO,
                                       HttpServletResponse response) throws IOException {
        // 设置导出不分页，获取所有数据
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        // 获取 API 错误日志数据列表
        List<ApiErrorLogDO> list = apiErrorLogService.getApiErrorLogPage(exportReqVO).getList();
        // 导出数据到 Excel 文件
        ExcelUtils.write(response, "API 错误日志.xls", "数据", ApiErrorLogRespVO.class,
                BeanUtils.toBean(list, ApiErrorLogRespVO.class));
    }

}
