package org.nstep.engine.module.infra.controller.admin.logger.vo.apierrorlog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static org.nstep.engine.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 管理后台 - API 错误日志分页请求 VO
 * <p>
 * 该类用于封装 API 错误日志分页请求的参数。
 * </p>
 */
@Schema(description = "管理后台 - API 错误日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiErrorLogPageReqVO extends PageParam {

    /**
     * 用户编号
     * <p>
     * 表示发起错误请求的用户编号。
     * </p>
     */
    @Schema(description = "用户编号", example = "666")
    private Long userId;

    /**
     * 用户类型
     * <p>
     * 表示用户类型，1 代表普通用户，其他值可参考具体业务定义。
     * </p>
     */
    @Schema(description = "用户类型", example = "1")
    private Integer userType;

    /**
     * 应用名
     * <p>
     * 表示发起错误请求的应用名称。
     * </p>
     */
    @Schema(description = "应用名", example = "dashboard")
    private String applicationName;

    /**
     * 请求地址
     * <p>
     * 表示请求的 URL 地址。
     * </p>
     */
    @Schema(description = "请求地址", example = "/xx/yy")
    private String requestUrl;

    /**
     * 异常发生时间
     * <p>
     * 表示异常发生的时间范围，支持查询某一时间段内的错误日志。
     * </p>
     */
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "异常发生时间")
    private LocalDateTime[] exceptionTime;

    /**
     * 处理状态
     * <p>
     * 表示错误日志的处理状态，例如 0 表示未处理，1 表示已处理。
     * </p>
     */
    @Schema(description = "处理状态", example = "0")
    private Integer processStatus;

}
