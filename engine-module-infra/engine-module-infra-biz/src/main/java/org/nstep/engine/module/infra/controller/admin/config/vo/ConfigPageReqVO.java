package org.nstep.engine.module.infra.controller.admin.config.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static org.nstep.engine.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 管理后台 - 参数配置分页 Request VO
 * <p>
 * 用于分页查询参数配置的请求数据传输对象，包含了数据源名称、参数键名、参数类型和创建时间等查询条件。
 * </p>
 */
@Schema(description = "管理后台 - 参数配置分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConfigPageReqVO extends PageParam {

    /**
     * 数据源名称，模糊匹配
     */
    @Schema(description = "数据源名称，模糊匹配", example = "名称")
    private String name;

    /**
     * 参数键名，模糊匹配
     */
    @Schema(description = "参数键名，模糊匹配", example = "yunai.db.username")
    private String key;

    /**
     * 参数类型，参见 SysConfigTypeEnum 枚举
     */
    @Schema(description = "参数类型，参见 SysConfigTypeEnum 枚举", example = "1")
    private Integer type;

    /**
     * 创建时间范围，支持查询某一时间段内创建的参数配置
     */
    @Schema(description = "创建时间", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
