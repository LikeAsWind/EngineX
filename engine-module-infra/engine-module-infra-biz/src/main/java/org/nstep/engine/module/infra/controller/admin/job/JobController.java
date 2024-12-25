package org.nstep.engine.module.infra.controller.admin.job;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.error;

/**
 * 管理后台 - 定时任务控制器
 * <p>
 * 该类处理与定时任务相关的操作，当前返回提示信息，告知用户使用的定时任务框架。
 * </p>
 */
@Tag(name = "管理后台 - 定时任务")
@RestController
@RequestMapping("/infra/job")
@Validated
public class JobController {

    /**
     * 获得定时任务分页
     * <p>
     * 该方法用于返回定时任务分页信息。目前的实现仅返回一个提示信息，说明使用的是 XXL-Job 框架。
     * </p>
     *
     * @return 返回错误信息，提示使用的是 XXL-Job 框架
     */
    @GetMapping("/page")
    @Operation(summary = "获得定时任务分页")
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<String> getJobPage() {
        return error(-1, "Cloud 版本使用 XXL-Job 作为定时任务！");
    }

}
