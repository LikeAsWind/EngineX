package org.nstep.engine.module.infra.controller.admin.logger;

import io.swagger.v3.oas.annotations.Operation;
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
import org.nstep.engine.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogPageReqVO;
import org.nstep.engine.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;
import org.nstep.engine.module.infra.dal.dataobject.logger.ApiAccessLogDO;
import org.nstep.engine.module.infra.service.logger.ApiAccessLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static org.nstep.engine.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - API 访问日志控制器
 * 提供 API 访问日志的分页查询和导出 Excel 功能。
 */
@Tag(name = "管理后台 - API 访问日志")
@RestController
@RequestMapping("/infra/api-access-log")
@Validated
public class ApiAccessLogController {

    @Resource
    private ApiAccessLogService apiAccessLogService; // API 访问日志服务

    /**
     * 获取 API 访问日志分页数据
     *
     * @param pageReqVO 分页请求参数
     * @return 返回分页结果，包括日志列表和分页信息
     */
    @GetMapping("/page")
    @Operation(summary = "获得API 访问日志分页")
    @PreAuthorize("@ss.hasPermission('infra:api-access-log:query')")
    public CommonResult<PageResult<ApiAccessLogRespVO>> getApiAccessLogPage(@Valid ApiAccessLogPageReqVO pageReqVO) {
        // 调用服务层方法获取分页数据
        PageResult<ApiAccessLogDO> pageResult = apiAccessLogService.getApiAccessLogPage(pageReqVO);
        // 将分页数据转换为响应对象并返回
        return success(BeanUtils.toBean(pageResult, ApiAccessLogRespVO.class));
    }

    /**
     * 导出 API 访问日志为 Excel 文件
     *
     * @param exportReqVO 导出请求参数
     * @param response    HTTP 响应，用于返回 Excel 文件
     * @throws IOException 如果导出过程中发生 I/O 错误
     */
    @GetMapping("/export-excel")
    @Operation(summary = "导出API 访问日志 Excel")
    @PreAuthorize("@ss.hasPermission('infra:api-access-log:export')")
    @ApiAccessLog(operateType = EXPORT) // 记录操作日志
    public void exportApiAccessLogExcel(@Valid ApiAccessLogPageReqVO exportReqVO,
                                        HttpServletResponse response) throws IOException {
        // 设置导出不分页，获取所有数据
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        // 获取 API 访问日志数据列表
        List<ApiAccessLogDO> list = apiAccessLogService.getApiAccessLogPage(exportReqVO).getList();
        // 导出数据到 Excel 文件
        ExcelUtils.write(response, "API 访问日志.xls", "数据", ApiAccessLogRespVO.class,
                BeanUtils.toBean(list, ApiAccessLogRespVO.class));
    }

}
