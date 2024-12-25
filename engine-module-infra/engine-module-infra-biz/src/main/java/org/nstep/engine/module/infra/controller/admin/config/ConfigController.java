package org.nstep.engine.module.infra.controller.admin.config;

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
import org.nstep.engine.framework.excel.core.util.ExcelUtils;
import org.nstep.engine.module.infra.controller.admin.config.vo.ConfigPageReqVO;
import org.nstep.engine.module.infra.controller.admin.config.vo.ConfigRespVO;
import org.nstep.engine.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import org.nstep.engine.module.infra.convert.config.ConfigConvert;
import org.nstep.engine.module.infra.dal.dataobject.config.ConfigDO;
import org.nstep.engine.module.infra.enums.ErrorCodeConstants;
import org.nstep.engine.module.infra.service.config.ConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.nstep.engine.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 参数配置 Controller
 * <p>
 * 该控制器提供了管理后台对参数配置的相关操作接口，包括创建、修改、删除、查询、分页获取、导出等功能。
 * </p>
 */
@Tag(name = "管理后台 - 参数配置")
@RestController
@RequestMapping("/infra/config")
@Validated
public class ConfigController {

    @Resource
    private ConfigService configService;

    /**
     * 创建参数配置
     * <p>
     * 创建一个新的参数配置，返回新创建的参数配置的 ID。
     * </p>
     *
     * @param createReqVO 创建请求数据
     * @return 新创建的参数配置 ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建参数配置")
    @PreAuthorize("@ss.hasPermission('infra:config:create')")
    public CommonResult<Long> createConfig(@Valid @RequestBody ConfigSaveReqVO createReqVO) {
        return success(configService.createConfig(createReqVO));
    }

    /**
     * 修改参数配置
     * <p>
     * 修改现有的参数配置。
     * </p>
     *
     * @param updateReqVO 更新请求数据
     * @return 操作是否成功
     */
    @PutMapping("/update")
    @Operation(summary = "修改参数配置")
    @PreAuthorize("@ss.hasPermission('infra:config:update')")
    public CommonResult<Boolean> updateConfig(@Valid @RequestBody ConfigSaveReqVO updateReqVO) {
        configService.updateConfig(updateReqVO);
        return success(true);
    }

    /**
     * 删除参数配置
     * <p>
     * 根据参数配置的 ID 删除指定的参数配置。
     * </p>
     *
     * @param id 参数配置的 ID
     * @return 操作是否成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除参数配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:config:delete')")
    public CommonResult<Boolean> deleteConfig(@RequestParam("id") Long id) {
        configService.deleteConfig(id);
        return success(true);
    }

    /**
     * 获取参数配置
     * <p>
     * 根据参数配置的 ID 获取指定的参数配置详情。
     * </p>
     *
     * @param id 参数配置的 ID
     * @return 参数配置详情
     */
    @GetMapping(value = "/get")
    @Operation(summary = "获得参数配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:config:query')")
    public CommonResult<ConfigRespVO> getConfig(@RequestParam("id") Long id) {
        return success(ConfigConvert.INSTANCE.convert(configService.getConfig(id)));
    }

    /**
     * 根据参数键名查询参数值
     * <p>
     * 根据给定的参数键名查询对应的参数值，如果该配置不可见，则不允许返回。
     * </p>
     *
     * @param key 参数键名
     * @return 参数值
     */
    @GetMapping(value = "/get-value-by-key")
    @Operation(summary = "根据参数键名查询参数值", description = "不可见的配置，不允许返回给前端")
    @Parameter(name = "key", description = "参数键", required = true, example = "yunai.biz.username")
    public CommonResult<String> getConfigKey(@RequestParam("key") String key) {
        ConfigDO config = configService.getConfigByKey(key);
        if (config == null) {
            return success(null);
        }
        if (!config.getVisible()) {
            throw exception(ErrorCodeConstants.CONFIG_GET_VALUE_ERROR_IF_VISIBLE);
        }
        return success(config.getValue());
    }

    /**
     * 获取参数配置分页
     * <p>
     * 获取符合条件的参数配置分页数据。
     * </p>
     *
     * @param pageReqVO 分页请求数据
     * @return 参数配置分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "获取参数配置分页")
    @PreAuthorize("@ss.hasPermission('infra:config:query')")
    public CommonResult<PageResult<ConfigRespVO>> getConfigPage(@Valid ConfigPageReqVO pageReqVO) {
        PageResult<ConfigDO> page = configService.getConfigPage(pageReqVO);
        return success(ConfigConvert.INSTANCE.convertPage(page));
    }

    /**
     * 导出参数配置
     * <p>
     * 导出符合条件的参数配置信息为 Excel 文件。
     * </p>
     *
     * @param exportReqVO 导出请求数据
     * @param response    HTTP 响应，用于输出 Excel 文件
     * @throws IOException 如果导出过程中发生 IO 异常
     */
    @GetMapping("/export")
    @Operation(summary = "导出参数配置")
    @PreAuthorize("@ss.hasPermission('infra:config:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportConfig(ConfigPageReqVO exportReqVO,
                             HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ConfigDO> list = configService.getConfigPage(exportReqVO).getList();
        // 输出
        ExcelUtils.write(response, "参数配置.xls", "数据", ConfigRespVO.class,
                ConfigConvert.INSTANCE.convertList(list));
    }
}
