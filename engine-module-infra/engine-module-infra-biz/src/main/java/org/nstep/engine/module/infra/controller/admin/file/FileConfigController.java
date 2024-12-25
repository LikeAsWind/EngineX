package org.nstep.engine.module.infra.controller.admin.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.infra.controller.admin.file.vo.config.FileConfigPageReqVO;
import org.nstep.engine.module.infra.controller.admin.file.vo.config.FileConfigRespVO;
import org.nstep.engine.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import org.nstep.engine.module.infra.dal.dataobject.file.FileConfigDO;
import org.nstep.engine.module.infra.service.file.FileConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 文件配置控制器
 * <p>
 * 该类用于处理与文件配置相关的所有请求，包括创建、更新、删除、分页查询等操作。
 * </p>
 */
@Tag(name = "管理后台 - 文件配置")
@RestController
@RequestMapping("/infra/file-config")
@Validated
public class FileConfigController {

    @Resource
    private FileConfigService fileConfigService;

    /**
     * 创建文件配置
     * <p>
     * 该方法用于创建新的文件配置。
     * </p>
     *
     * @param createReqVO 文件配置创建请求 VO
     * @return 返回创建的文件配置 ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建文件配置")
    @PreAuthorize("@ss.hasPermission('infra:file-config:create')")
    public CommonResult<Long> createFileConfig(@Valid @RequestBody FileConfigSaveReqVO createReqVO) {
        return success(fileConfigService.createFileConfig(createReqVO));
    }

    /**
     * 更新文件配置
     * <p>
     * 该方法用于更新现有的文件配置。
     * </p>
     *
     * @param updateReqVO 文件配置更新请求 VO
     * @return 返回操作是否成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新文件配置")
    @PreAuthorize("@ss.hasPermission('infra:file-config:update')")
    public CommonResult<Boolean> updateFileConfig(@Valid @RequestBody FileConfigSaveReqVO updateReqVO) {
        fileConfigService.updateFileConfig(updateReqVO);
        return success(true);
    }

    /**
     * 更新文件配置为主配置
     * <p>
     * 该方法用于将指定的文件配置设置为主配置。
     * </p>
     *
     * @param id 文件配置 ID
     * @return 返回操作是否成功
     */
    @PutMapping("/update-master")
    @Operation(summary = "更新文件配置为 Master")
    @PreAuthorize("@ss.hasPermission('infra:file-config:update')")
    public CommonResult<Boolean> updateFileConfigMaster(@RequestParam("id") Long id) {
        fileConfigService.updateFileConfigMaster(id);
        return success(true);
    }

    /**
     * 删除文件配置
     * <p>
     * 该方法用于删除指定的文件配置。
     * </p>
     *
     * @param id 文件配置 ID
     * @return 返回操作是否成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除文件配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file-config:delete')")
    public CommonResult<Boolean> deleteFileConfig(@RequestParam("id") Long id) {
        fileConfigService.deleteFileConfig(id);
        return success(true);
    }

    /**
     * 获取文件配置
     * <p>
     * 该方法用于获取指定的文件配置详情。
     * </p>
     *
     * @param id 文件配置 ID
     * @return 返回文件配置详情
     */
    @GetMapping("/get")
    @Operation(summary = "获得文件配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:file-config:query')")
    public CommonResult<FileConfigRespVO> getFileConfig(@RequestParam("id") Long id) {
        FileConfigDO config = fileConfigService.getFileConfig(id);
        return success(BeanUtils.toBean(config, FileConfigRespVO.class));
    }

    /**
     * 获取文件配置分页
     * <p>
     * 该方法用于分页查询文件配置列表。
     * </p>
     *
     * @param pageVO 文件配置分页请求 VO
     * @return 返回文件配置分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "获得文件配置分页")
    @PreAuthorize("@ss.hasPermission('infra:file-config:query')")
    public CommonResult<PageResult<FileConfigRespVO>> getFileConfigPage(@Valid FileConfigPageReqVO pageVO) {
        PageResult<FileConfigDO> pageResult = fileConfigService.getFileConfigPage(pageVO);
        return success(BeanUtils.toBean(pageResult, FileConfigRespVO.class));
    }

    /**
     * 测试文件配置是否正确
     * <p>
     * 该方法用于测试文件配置是否有效，并返回相应的 URL。
     * </p>
     *
     * @param id 文件配置 ID
     * @return 返回测试结果 URL
     * @throws Exception 如果配置无效，则抛出异常
     */
    @GetMapping("/test")
    @Operation(summary = "测试文件配置是否正确")
    @PreAuthorize("@ss.hasPermission('infra:file-config:query')")
    public CommonResult<String> testFileConfig(@RequestParam("id") Long id) throws Exception {
        String url = fileConfigService.testFileConfig(id);
        return success(url);
    }
}
