package org.nstep.engine.module.infra.controller.admin.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static org.nstep.engine.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 管理后台 - 文件分页请求 VO
 * <p>
 * 该类用于接收文件分页请求，支持文件路径、文件类型以及创建时间范围等筛选条件。
 * </p>
 */
@Schema(description = "管理后台 - 文件分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FilePageReqVO extends PageParam {

    /**
     * 文件路径，模糊匹配
     * <p>
     * 用户可以根据文件路径进行模糊匹配查询文件。
     * </p>
     */
    @Schema(description = "文件路径，模糊匹配", example = "engine")
    private String path;

    /**
     * 文件类型，模糊匹配
     * <p>
     * 用户可以根据文件类型进行模糊匹配查询文件。
     * </p>
     */
    @Schema(description = "文件类型，模糊匹配", example = "jpg")
    private String type;

    /**
     * 创建时间范围
     * <p>
     * 用户可以指定文件的创建时间范围进行筛选，格式为：[开始时间, 结束时间]。
     * </p>
     */
    @Schema(description = "创建时间", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
