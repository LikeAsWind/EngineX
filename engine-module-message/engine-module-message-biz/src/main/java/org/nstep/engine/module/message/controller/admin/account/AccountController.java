package org.nstep.engine.module.message.controller.admin.account;

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
import org.nstep.engine.module.message.controller.admin.account.vo.AccountPageReqVO;
import org.nstep.engine.module.message.controller.admin.account.vo.AccountRespVO;
import org.nstep.engine.module.message.controller.admin.account.vo.AccountSaveReqVO;
import org.nstep.engine.module.message.dal.dataobject.account.AccountDO;
import org.nstep.engine.module.message.service.account.AccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.nstep.engine.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 渠道配置信息控制器
 * 提供对渠道配置信息的增、删、改、查等操作接口，支持分页查询和导出 Excel 功能。
 */
@Tag(name = "管理后台 - 渠道配置信息")
@RestController
@RequestMapping("/channel/account") // 设置请求路径
@Validated // 启用参数校验
public class AccountController {

    @Resource
    private AccountService accountService; // 注入服务层

    /**
     * 创建渠道配置信息
     * 通过 POST 请求创建新的渠道配置信息
     *
     * @param createReqVO 渠道配置信息新增请求对象
     * @return 返回创建成功后的主键 ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建渠道配置信息") // Swagger 注解，提供接口说明
    @PreAuthorize("@ss.hasPermission('channel:account:create')") // 权限校验
    public CommonResult<Long> createAccount(@Valid @RequestBody AccountSaveReqVO createReqVO) {
        return success(accountService.createAccount(createReqVO)); // 调用服务层方法并返回结果
    }

    /**
     * 更新渠道配置信息
     * 通过 PUT 请求更新现有的渠道配置信息
     *
     * @param updateReqVO 渠道配置信息更新请求对象
     * @return 返回操作是否成功的布尔值
     */
    @PutMapping("/update")
    @Operation(summary = "更新渠道配置信息") // Swagger 注解，提供接口说明
    @PreAuthorize("@ss.hasPermission('channel:account:update')") // 权限校验
    public CommonResult<Boolean> updateAccount(@Valid @RequestBody AccountSaveReqVO updateReqVO) {
        accountService.updateAccount(updateReqVO); // 调用服务层更新方法
        return success(true); // 返回成功结果
    }

    /**
     * 删除渠道配置信息
     * 通过 DELETE 请求删除指定的渠道配置信息
     *
     * @param id 渠道配置信息的主键 ID
     * @return 返回操作是否成功的布尔值
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除渠道配置信息") // Swagger 注解，提供接口说明
    @Parameter(name = "id", description = "编号", required = true) // 参数描述
    @PreAuthorize("@ss.hasPermission('channel:account:delete')") // 权限校验
    public CommonResult<Boolean> deleteAccount(@RequestParam("id") Long id) {
        accountService.deleteAccount(id); // 调用服务层删除方法
        return success(true); // 返回成功结果
    }

    /**
     * 批量删除渠道配置信息
     * 通过 DELETE 请求删除指定的渠道配置信息
     *
     * @param ids 渠道配置信息的主键 IDS
     * @return 返回操作是否成功的布尔值
     */
    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "批量删除渠道配置信息") // Swagger 注解，提供接口说明
    @Parameter(name = "id", description = "编号", required = true) // 参数描述
    @PreAuthorize("@ss.hasPermission('channel:account:deletes')") // 权限校验
    public CommonResult<Boolean> deleteAccounts(@PathVariable Long[] ids) {
        accountService.deleteAccounts(ids); // 调用服务层删除方法
        return success(true); // 返回成功结果
    }

    /**
     * 获取渠道配置信息
     * 通过 GET 请求获取指定 ID 的渠道配置信息
     *
     * @param id 渠道配置信息的主键 ID
     * @return 返回渠道配置信息
     */
    @GetMapping("/get")
    @Operation(summary = "获得渠道配置信息") // Swagger 注解，提供接口说明
    @Parameter(name = "id", description = "编号", required = true, example = "1024") // 参数描述
    @PreAuthorize("@ss.hasPermission('channel:account:query')") // 权限校验
    public CommonResult<AccountRespVO> getAccount(@RequestParam("id") Long id) {
        AccountDO account = accountService.getAccount(id); // 调用服务层获取渠道配置信息
        return success(BeanUtils.toBean(account, AccountRespVO.class)); // 转换并返回结果
    }

    /**
     * 获取渠道配置信息分页列表
     * 通过 GET 请求获取渠道配置信息的分页列表
     *
     * @param pageReqVO 分页请求对象
     * @return 返回分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "获得渠道配置信息分页") // Swagger 注解，提供接口说明
    @PreAuthorize("@ss.hasPermission('channel:account:query')") // 权限校验
    public CommonResult<PageResult<AccountRespVO>> getAccountPage(@Valid AccountPageReqVO pageReqVO) {
        PageResult<AccountDO> pageResult = accountService.getAccountPage(pageReqVO); // 调用服务层分页查询方法
        return success(BeanUtils.toBean(pageResult, AccountRespVO.class)); // 转换并返回分页结果
    }

    /**
     * 导出渠道配置信息到 Excel
     * 通过 GET 请求导出渠道配置信息为 Excel 文件
     *
     * @param pageReqVO 分页请求对象
     * @param response  HTTP 响应对象，用于写入 Excel 文件
     * @throws IOException 如果文件写入过程中发生错误
     */
    @GetMapping("/export-excel")
    @Operation(summary = "导出渠道配置信息 Excel") // Swagger 注解，提供接口说明
    @PreAuthorize("@ss.hasPermission('channel:account:export')") // 权限校验
    @ApiAccessLog(operateType = EXPORT) // 操作日志注解
    public void exportAccountExcel(@Valid AccountPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE); // 设置不分页，导出所有数据
        List<AccountDO> list = accountService.getAccountPage(pageReqVO).getList(); // 获取所有渠道配置信息
        // 导出 Excel 文件
        ExcelUtils.write(response, "渠道配置信息.xls", "数据", AccountRespVO.class, BeanUtils.toBean(list, AccountRespVO.class));
    }


    @GetMapping("/get")
    @Operation(summary = "获取当前用户所有渠道账号") // Swagger 注解，提供接口说明
    @Parameter(name = "sendChannel", description = "渠道类型", required = true, example = "10") // 参数描述
    @PreAuthorize("@ss.hasPermission('channel:account:query')") // 权限校验
    public CommonResult<List<AccountRespVO>> list4CurrUser(@RequestParam("sendChannel") Integer sendChannel) {
        // 调用服务层获取渠道配置信息
        List<AccountRespVO> accountList = accountService.list4CurrUser(sendChannel);
        // 返回成功结果
        return success(accountList);
    }


}
