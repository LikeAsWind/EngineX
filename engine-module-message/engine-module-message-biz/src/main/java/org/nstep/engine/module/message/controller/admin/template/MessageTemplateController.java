package org.nstep.engine.module.message.controller.admin.template;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.nstep.engine.module.message.controller.admin.template.vo.TemplatePageReqVO;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateRespVO;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateSaveReqVO;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.service.template.TemplateService;
import org.nstep.engine.module.message.util.ContentHolderUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.nstep.engine.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 消息模板信息控制器
 * 该控制器处理所有与消息模板相关的请求，包括创建、更新、删除、查询、分页查询、导出等操作。
 */
@Tag(name = "消息后台 - 消息模板信息")
@RestController
@RequestMapping("/message/template")
@Validated // 启用验证功能，确保请求参数符合要求
public class MessageTemplateController {

    @Resource
    private TemplateService templateService; // 消息模板服务

    @Resource
    private ContentHolderUtil contentHolderUtil; // 用于处理消息模板内容的工具类

    /**
     * 创建消息模板信息
     *
     * @param createReqVO 新建模板请求对象
     * @return 返回创建的模板ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建消息模板信息")
    @PreAuthorize("@ss.hasPermission('message:template:create')") // 权限控制，确保用户有权限
    public CommonResult<Long> createTemplate(@Valid @RequestBody TemplateSaveReqVO createReqVO) {
        return success(templateService.createTemplate(createReqVO)); // 调用服务创建模板并返回ID
    }

    /**
     * 更新消息模板信息
     *
     * @param updateReqVO 更新模板请求对象
     * @return 返回更新结果
     */
    @PutMapping("/update")
    @Operation(summary = "更新消息模板信息")
    @PreAuthorize("@ss.hasPermission('message:template:update')")
    public CommonResult<Boolean> updateTemplate(@Valid @RequestBody TemplateSaveReqVO updateReqVO) {
        templateService.updateTemplate(updateReqVO); // 调用服务更新模板
        return success(true); // 返回成功标识
    }

    /**
     * 删除消息模板信息
     *
     * @param id 模板ID
     * @return 返回删除结果
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除消息模板信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('message:template:delete')")
    public CommonResult<Boolean> deleteTemplate(@RequestParam("id") Long id) {
        templateService.deleteTemplate(id); // 调用服务删除模板
        return success(true); // 返回成功标识
    }

    /**
     * 查询消息模板信息
     *
     * @param id 模板ID
     * @return 返回模板信息
     */
    @GetMapping("/get")
    @Operation(summary = "获得消息模板信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('message:template:query')")
    public CommonResult<TemplateRespVO> getTemplate(@RequestParam("id") Long id) {
        TemplateDO template = templateService.getTemplate(id); // 调用服务获取模板信息
        return success(BeanUtils.toBean(template, TemplateRespVO.class)); // 返回模板响应对象
    }

    /**
     * 分页查询消息模板信息
     *
     * @param pageReqVO 分页请求对象
     * @return 返回分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询创建者的消息模板信息")
    @PreAuthorize("@ss.hasPermission('message:template:query')")
    public CommonResult<PageResult<TemplateRespVO>> getTemplatePage(@Valid TemplatePageReqVO pageReqVO) {
        PageResult<TemplateDO> pageResult = templateService.getTemplatePage(pageReqVO, true); // 获取分页结果
        return success(BeanUtils.toBean(pageResult, TemplateRespVO.class)); // 返回分页响应对象
    }

    /**
     * 分页查询审核消息模板列表
     *
     * @param pageReqVO 分页请求对象
     * @return 返回分页结果
     */
    @GetMapping("/audit/page")
    @Operation(summary = "分页查询审核消息模板列表")
    @PreAuthorize("@ss.hasPermission('message:template:query')")
    public CommonResult<PageResult<TemplateRespVO>> getAuditTemplatePage(@Valid TemplatePageReqVO pageReqVO) {
        PageResult<TemplateDO> pageResult = templateService.getTemplatePage(pageReqVO, false); // 获取审核状态的分页结果
        return success(BeanUtils.toBean(pageResult, TemplateRespVO.class)); // 返回分页响应对象
    }

    /**
     * 导出消息模板信息到 Excel 文件
     *
     * @param pageReqVO 分页请求对象
     * @param response  HttpServletResponse，用于写入 Excel 文件
     * @throws IOException 导出过程中可能抛出的异常
     */
    @GetMapping("/export-excel")
    @Operation(summary = "导出消息模板信息 Excel")
    @PreAuthorize("@ss.hasPermission('message:template:export')")
    @ApiAccessLog(operateType = EXPORT) // 操作日志，记录导出操作
    public void exportTemplateExcel(@Valid TemplatePageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE); // 设置分页大小为不限制
        List<TemplateDO> list = templateService.getTemplatePage(pageReqVO, false).getList(); // 获取模板列表
        // 导出 Excel
        ExcelUtils.write(response, "消息模板信息.xls", "数据", TemplateRespVO.class, BeanUtils.toBean(list, TemplateRespVO.class));
    }

    /**
     * 查询所有占位符名称集合
     *
     * @param id 消息模板ID
     * @return 返回占位符名称列表
     */
    @GetMapping("/variables/{id}")
    @Operation(summary = "查询所有占位符名称集合")
    @PreAuthorize("@ss.hasPermission('message:template:listVariables')")
    public CommonResult<List<String>> listVariables(@PathVariable Long id) {
        TemplateDO template = templateService.getTemplate(id); // 获取消息模板
        List<String> variables = contentHolderUtil.getVariables(template); // 提取占位符名称
        return success(variables); // 返回占位符名称列表
    }
}
