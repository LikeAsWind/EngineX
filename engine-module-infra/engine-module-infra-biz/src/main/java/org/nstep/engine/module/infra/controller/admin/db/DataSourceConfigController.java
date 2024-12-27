package org.nstep.engine.module.infra.controller.admin.db;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.infra.controller.admin.db.vo.DataSourceConfigRespVO;
import org.nstep.engine.module.infra.controller.admin.db.vo.DataSourceConfigSaveReqVO;
import org.nstep.engine.module.infra.dal.dataobject.db.DataSourceConfigDO;
import org.nstep.engine.module.infra.service.db.DataSourceConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 数据源配置 Controller
 * <p>
 * 该控制器处理数据源配置的创建、更新、删除、查询等操作。包括通过数据源配置 VO 接收请求参数，
 * 并通过服务层进行业务处理，最后返回相应的结果。
 * </p>
 */
@Tag(name = "管理后台 - 数据源配置")
@RestController
@RequestMapping("/infra/data-source-config")
@Validated
public class DataSourceConfigController {

    @Resource
    private DataSourceConfigService dataSourceConfigService;

    /**
     * 创建数据源配置
     * <p>
     * 该方法用于创建一个新的数据源配置。通过传入的数据源配置请求参数，调用服务层进行创建操作。
     * </p>
     *
     * @param createReqVO 创建请求参数
     * @return 创建结果
     */
    @PostMapping("/create")
    @Operation(summary = "创建数据源配置")
    @PreAuthorize("@ss.hasPermission('infra:data-source-config:create')")
    public CommonResult<Long> createDataSourceConfig(@Valid @RequestBody DataSourceConfigSaveReqVO createReqVO) {
        return success(dataSourceConfigService.createDataSourceConfig(createReqVO));
    }

    /**
     * 更新数据源配置
     * <p>
     * 该方法用于更新已有的数据源配置。通过传入的数据源配置请求参数，调用服务层进行更新操作。
     * </p>
     *
     * @param updateReqVO 更新请求参数
     * @return 更新结果
     */
    @PutMapping("/update")
    @Operation(summary = "更新数据源配置")
    @PreAuthorize("@ss.hasPermission('infra:data-source-config:update')")
    public CommonResult<Boolean> updateDataSourceConfig(@Valid @RequestBody DataSourceConfigSaveReqVO updateReqVO) {
        dataSourceConfigService.updateDataSourceConfig(updateReqVO);
        return success(true);
    }

    /**
     * 删除数据源配置
     * <p>
     * 该方法用于删除指定的数据源配置。通过传入的数据源配置的 ID，调用服务层进行删除操作。
     * </p>
     *
     * @param id 数据源配置的 ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除数据源配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:data-source-config:delete')")
    public CommonResult<Boolean> deleteDataSourceConfig(@RequestParam("id") Long id) {
        dataSourceConfigService.deleteDataSourceConfig(id);
        return success(true);
    }

    /**
     * 获取数据源配置
     * <p>
     * 该方法用于根据 ID 获取指定的数据源配置。通过传入的数据源配置的 ID，调用服务层进行查询操作，
     * 并返回对应的数据源配置。
     * </p>
     *
     * @param id 数据源配置的 ID
     * @return 数据源配置详情
     */
    @GetMapping("/get")
    @Operation(summary = "获得数据源配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:data-source-config:query')")
    public CommonResult<DataSourceConfigRespVO> getDataSourceConfig(@RequestParam("id") Long id) {
        DataSourceConfigDO config = dataSourceConfigService.getDataSourceConfig(id);
        return success(BeanUtils.toBean(config, DataSourceConfigRespVO.class));
    }

    /**
     * 获取所有数据源配置列表
     * <p>
     * 该方法用于获取所有的数据源配置列表。调用服务层获取数据源配置的列表，并返回给前端。
     * </p>
     *
     * @return 数据源配置列表
     */
    @GetMapping("/list")
    @Operation(summary = "获得数据源配置列表")
    @PreAuthorize("@ss.hasPermission('infra:data-source-config:query')")
    public CommonResult<List<DataSourceConfigRespVO>> getDataSourceConfigList() {
        List<DataSourceConfigDO> list = dataSourceConfigService.getDataSourceConfigList(true);
        return success(BeanUtils.toBean(list, DataSourceConfigRespVO.class));
    }
}
