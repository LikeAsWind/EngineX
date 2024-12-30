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

@Tag(name = "消息后台 - 消息模板信息")
@RestController
@RequestMapping("/message/template")
@Validated
public class MessageTemplateController {

    @Resource
    private TemplateService templateService;

    @Resource
    private ContentHolderUtil contentHolderUtil;

    @PostMapping("/create")
    @Operation(summary = "创建消息模板信息")
    @PreAuthorize("@ss.hasPermission('message:template:create')")
    public CommonResult<Long> createTemplate(@Valid @RequestBody TemplateSaveReqVO createReqVO) {
        return success(templateService.createTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新消息模板信息")
    @PreAuthorize("@ss.hasPermission('message:template:update')")
    public CommonResult<Boolean> updateTemplate(@Valid @RequestBody TemplateSaveReqVO updateReqVO) {
        templateService.updateTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除消息模板信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('message:template:delete')")
    public CommonResult<Boolean> deleteTemplate(@RequestParam("id") Long id) {
        templateService.deleteTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得消息模板信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('message:template:query')")
    public CommonResult<TemplateRespVO> getTemplate(@RequestParam("id") Long id) {
        TemplateDO template = templateService.getTemplate(id);
        return success(BeanUtils.toBean(template, TemplateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询创建者的消息模板信息")
    @PreAuthorize("@ss.hasPermission('message:template:query')")
    public CommonResult<PageResult<TemplateRespVO>> getTemplatePage(@Valid TemplatePageReqVO pageReqVO) {
        PageResult<TemplateDO> pageResult = templateService.getTemplatePage(pageReqVO, true);
        return success(BeanUtils.toBean(pageResult, TemplateRespVO.class));
    }

    @GetMapping("/audit/page")
    @Operation(summary = "分页查询审核消息模板列表")
    @PreAuthorize("@ss.hasPermission('message:template:query')")
    public CommonResult<PageResult<TemplateRespVO>> getAuditTemplatePage(@Valid TemplatePageReqVO pageReqVO) {
        PageResult<TemplateDO> pageResult = templateService.getTemplatePage(pageReqVO, false);
        return success(BeanUtils.toBean(pageResult, TemplateRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出消息模板信息 Excel")
    @PreAuthorize("@ss.hasPermission('message:template:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTemplateExcel(@Valid TemplatePageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TemplateDO> list = templateService.getTemplatePage(pageReqVO, false).getList();
        // 导出 Excel
        ExcelUtils.write(response, "消息模板信息.xls", "数据", TemplateRespVO.class, BeanUtils.toBean(list, TemplateRespVO.class));
    }

    /**
     * 查询所有占位符名称集合
     * <p>
     * 该方法接收一个消息模板的ID作为参数，通过调用messageTemplateService的getById方法获取对应的消息模板对象。
     * 然后，使用contentHolderUtil的getVariables方法从消息模板中提取所有占位符名称，并将这些名称存储在一个列表中。
     * 最后，方法返回一个包含占位符名称列表的TableDataInfo对象。
     *
     * @param id 消息模板的ID
     * @return 包含占位符名称列表的TableDataInfo对象
     */
    @GetMapping("/variables/{id}")
    @Operation(summary = "查询所有占位符名称集合")
    @PreAuthorize("@ss.hasPermission('message:template:listVariables')")
    public CommonResult<List<String>> listVariables(@PathVariable Long id) {
        // 根据ID获取消息模板对象
        TemplateDO template = templateService.getTemplate(id);
        // 从消息模板中提取所有占位符名称
        List<String> variables = contentHolderUtil.getVariables(template);
        return success(variables);
    }

}