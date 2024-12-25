package org.nstep.engine.module.infra.controller.admin.file.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static org.nstep.engine.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 管理后台 - 文件配置分页请求 VO
 * <p>
 * 该类用于接收文件配置分页查询请求的参数。继承自 `PageParam`，支持分页功能，
 * 通过传入的配置名、存储器类型以及创建时间范围，进行文件配置的分页查询。
 * </p>
 */
@Schema(description = "管理后台 - 文件配置分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FileConfigPageReqVO extends PageParam {

    /**
     * 配置名
     * <p>
     * 用于根据文件配置的名称进行模糊查询。示例：S3 - 阿里云。
     * </p>
     */
    @Schema(description = "配置名", example = "S3 - 阿里云")
    private String name;

    /**
     * 存储器类型
     * <p>
     * 用于根据存储器类型进行筛选。示例：1（代表阿里云存储器）。
     * </p>
     */
    @Schema(description = "存储器", example = "1")
    private Integer storage;

    /**
     * 创建时间范围
     * <p>
     * 用于根据文件配置的创建时间进行范围查询。传入一个数组，表示查询的时间区间。
     * 格式：`[2022-07-01 00:00:00, 2022-07-01 23:59:59]`。
     * </p>
     */
    @Schema(description = "创建时间", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
