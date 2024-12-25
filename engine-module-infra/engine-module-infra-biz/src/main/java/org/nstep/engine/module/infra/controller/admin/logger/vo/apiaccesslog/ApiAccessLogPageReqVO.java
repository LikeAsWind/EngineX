package org.nstep.engine.module.infra.controller.admin.logger.vo.apiaccesslog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static org.nstep.engine.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 管理后台 - API 访问日志分页请求 VO
 * <p>
 * 该类用于接收 API 访问日志分页查询请求的参数。
 * </p>
 */
@Schema(description = "管理后台 - API 访问日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiAccessLogPageReqVO extends PageParam {

    /**
     * 用户编号
     * <p>
     * 用于筛选特定用户的 API 访问日志。
     * </p>
     */
    @Schema(description = "用户编号", example = "666")
    private Long userId;

    /**
     * 用户类型
     * <p>
     * 用于筛选特定类型用户的 API 访问日志。
     * </p>
     */
    @Schema(description = "用户类型", example = "2")
    private Integer userType;

    /**
     * 应用名
     * <p>
     * 用于筛选特定应用的 API 访问日志。
     * </p>
     */
    @Schema(description = "应用名", example = "dashboard")
    private String applicationName;

    /**
     * 请求地址，模糊匹配
     * <p>
     * 用于根据请求的 URL 地址筛选日志。
     * </p>
     */
    @Schema(description = "请求地址，模糊匹配", example = "/xxx/yyy")
    private String requestUrl;

    /**
     * 开始时间
     * <p>
     * 用于筛选指定时间范围内的日志。
     * </p>
     */
    @Schema(description = "开始时间", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] beginTime;

    /**
     * 执行时长，单位：毫秒
     * <p>
     * 用于筛选大于等于指定时长的 API 访问日志。
     * </p>
     */
    @Schema(description = "执行时长,大于等于，单位：毫秒", example = "100")
    private Integer duration;

    /**
     * 结果码
     * <p>
     * 用于筛选特定结果码的日志，0 表示成功，非 0 表示失败。
     * </p>
     */
    @Schema(description = "结果码", example = "0")
    private Integer resultCode;

}
