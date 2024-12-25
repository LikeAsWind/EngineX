package org.nstep.engine.module.infra.controller.admin.codegen.vo.table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static org.nstep.engine.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 管理后台 - 表定义分页 Request VO
 * <p>
 * 该类用于接收管理后台表定义分页查询的请求数据，支持表名称、表描述、实体类名称等字段的模糊查询，
 * 以及通过创建时间范围进行筛选。继承自 {@link PageParam} 类，支持分页功能。
 * </p>
 */
@Schema(description = "管理后台 - 表定义分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CodegenTablePageReqVO extends PageParam {

    @Schema(description = "表名称，模糊匹配", example = "engine")
    private String tableName; // 表的名称，支持模糊匹配

    @Schema(description = "表描述，模糊匹配", example = "engine")
    private String tableComment; // 表的描述，支持模糊匹配

    @Schema(description = "实体，模糊匹配", example = "Engine")
    private String className; // 实体类名称，支持模糊匹配

    @Schema(description = "创建时间", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime; // 创建时间范围，用于筛选表的创建时间区间
}
