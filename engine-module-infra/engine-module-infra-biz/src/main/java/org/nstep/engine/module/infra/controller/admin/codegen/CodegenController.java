package org.nstep.engine.module.infra.controller.admin.codegen;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ZipUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.CodegenCreateListReqVO;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.CodegenDetailRespVO;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.CodegenPreviewRespVO;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.CodegenUpdateReqVO;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.table.CodegenTablePageReqVO;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.table.CodegenTableRespVO;
import org.nstep.engine.module.infra.controller.admin.codegen.vo.table.DatabaseTableRespVO;
import org.nstep.engine.module.infra.convert.codegen.CodegenConvert;
import org.nstep.engine.module.infra.dal.dataobject.codegen.CodegenColumnDO;
import org.nstep.engine.module.infra.dal.dataobject.codegen.CodegenTableDO;
import org.nstep.engine.module.infra.service.codegen.CodegenService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;
import static org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static org.nstep.engine.module.infra.framework.file.core.utils.FileTypeUtils.writeAttachment;

/**
 * 管理后台 - 代码生成器 Controller
 * <p>
 * 提供代码生成相关的接口，包括表和字段的管理、代码预览、下载等功能。
 * </p>
 */
@Tag(name = "管理后台 - 代码生成器")
@RestController
@RequestMapping("/infra/codegen")
@Validated
public class CodegenController {

    @Resource
    private CodegenService codegenService;

    /**
     * 获得数据库自带的表定义列表，过滤掉已经导入的表
     *
     * @param dataSourceConfigId 数据源配置的编号
     * @param name               表名，模糊匹配
     * @param comment            描述，模糊匹配
     * @return 数据库表定义列表
     */
    @GetMapping("/db/table/list")
    @Operation(summary = "获得数据库自带的表定义列表", description = "会过滤掉已经导入 Codegen 的表")
    @Parameters({
            @Parameter(name = "dataSourceConfigId", description = "数据源配置的编号", required = true, example = "1"),
            @Parameter(name = "name", description = "表名，模糊匹配", example = "engine"),
            @Parameter(name = "comment", description = "描述，模糊匹配", example = "engine")
    })
    @PreAuthorize("@ss.hasPermission('infra:codegen:query')")
    public CommonResult<List<DatabaseTableRespVO>> getDatabaseTableList(
            @RequestParam(value = "dataSourceConfigId") Long dataSourceConfigId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "comment", required = false) String comment) {
        return success(codegenService.getDatabaseTableList(dataSourceConfigId, name, comment));
    }

    /**
     * 获得表定义列表
     *
     * @param dataSourceConfigId 数据源配置的编号
     * @return 表定义列表
     */
    @GetMapping("/table/list")
    @Operation(summary = "获得表定义列表")
    @Parameter(name = "dataSourceConfigId", description = "数据源配置的编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('infra:codegen:query')")
    public CommonResult<List<CodegenTableRespVO>> getCodegenTableList(@RequestParam(value = "dataSourceConfigId") Long dataSourceConfigId) {
        List<CodegenTableDO> list = codegenService.getCodegenTableList(dataSourceConfigId);
        return success(BeanUtils.toBean(list, CodegenTableRespVO.class));
    }

    /**
     * 获得表定义分页
     *
     * @param pageReqVO 分页请求参数
     * @return 表定义分页结果
     */
    @GetMapping("/table/page")
    @Operation(summary = "获得表定义分页")
    @PreAuthorize("@ss.hasPermission('infra:codegen:query')")
    public CommonResult<PageResult<CodegenTableRespVO>> getCodegenTablePage(@Valid CodegenTablePageReqVO pageReqVO) {
        PageResult<CodegenTableDO> pageResult = codegenService.getCodegenTablePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CodegenTableRespVO.class));
    }

    /**
     * 获得表和字段的明细
     *
     * @param tableId 表编号
     * @return 表和字段的详细信息
     */
    @GetMapping("/detail")
    @Operation(summary = "获得表和字段的明细")
    @Parameter(name = "tableId", description = "表编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:codegen:query')")
    public CommonResult<CodegenDetailRespVO> getCodegenDetail(@RequestParam("tableId") Long tableId) {
        CodegenTableDO table = codegenService.getCodegenTable(tableId);
        List<CodegenColumnDO> columns = codegenService.getCodegenColumnListByTableId(tableId);
        return success(CodegenConvert.INSTANCE.convert(table, columns));
    }

    /**
     * 基于数据库的表结构，创建代码生成器的表和字段定义
     *
     * @param reqVO 创建请求参数
     * @return 创建的表和字段定义编号列表
     */
    @Operation(summary = "基于数据库的表结构，创建代码生成器的表和字段定义")
    @PostMapping("/create-list")
    @PreAuthorize("@ss.hasPermission('infra:codegen:create')")
    public CommonResult<List<Long>> createCodegenList(@Valid @RequestBody CodegenCreateListReqVO reqVO) {
        return success(codegenService.createCodegenList(getLoginUserId(), reqVO));
    }

    /**
     * 更新数据库的表和字段定义
     *
     * @param updateReqVO 更新请求参数
     * @return 更新结果
     */
    @Operation(summary = "更新数据库的表和字段定义")
    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermission('infra:codegen:update')")
    public CommonResult<Boolean> updateCodegen(@Valid @RequestBody CodegenUpdateReqVO updateReqVO) {
        codegenService.updateCodegen(updateReqVO);
        return success(true);
    }

    /**
     * 基于数据库的表结构，同步数据库的表和字段定义
     *
     * @param tableId 表编号
     * @return 同步结果
     */
    @Operation(summary = "基于数据库的表结构，同步数据库的表和字段定义")
    @PutMapping("/sync-from-db")
    @Parameter(name = "tableId", description = "表编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:codegen:update')")
    public CommonResult<Boolean> syncCodegenFromDB(@RequestParam("tableId") Long tableId) {
        codegenService.syncCodegenFromDB(tableId);
        return success(true);
    }

    /**
     * 删除数据库的表和字段定义
     *
     * @param tableId 表编号
     * @return 删除结果
     */
    @Operation(summary = "删除数据库的表和字段定义")
    @DeleteMapping("/delete")
    @Parameter(name = "tableId", description = "表编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:codegen:delete')")
    public CommonResult<Boolean> deleteCodegen(@RequestParam("tableId") Long tableId) {
        codegenService.deleteCodegen(tableId);
        return success(true);
    }

    /**
     * 预览生成代码
     *
     * @param tableId 表编号
     * @return 代码预览列表
     */
    @Operation(summary = "预览生成代码")
    @GetMapping("/preview")
    @Parameter(name = "tableId", description = "表编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:codegen:preview')")
    public CommonResult<List<CodegenPreviewRespVO>> previewCodegen(@RequestParam("tableId") Long tableId) {
        Map<String, String> codes = codegenService.generationCodes(tableId);
        return success(CodegenConvert.INSTANCE.convert(codes));
    }

    /**
     * 下载生成代码
     *
     * @param tableId  表编号
     * @param response HttpServletResponse 用于输出文件
     * @throws IOException 异常
     */
    @Operation(summary = "下载生成代码")
    @GetMapping("/download")
    @Parameter(name = "tableId", description = "表编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:codegen:download')")
    public void downloadCodegen(@RequestParam("tableId") Long tableId,
                                HttpServletResponse response) throws IOException {
        Map<String, String> codes = codegenService.generationCodes(tableId);
        String[] paths = codes.keySet().toArray(new String[0]);
        ByteArrayInputStream[] ins = codes.values().stream().map(IoUtil::toUtf8Stream).toArray(ByteArrayInputStream[]::new);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipUtil.zip(outputStream, paths, ins);
        writeAttachment(response, "codegen.zip", outputStream.toByteArray());
    }

}
