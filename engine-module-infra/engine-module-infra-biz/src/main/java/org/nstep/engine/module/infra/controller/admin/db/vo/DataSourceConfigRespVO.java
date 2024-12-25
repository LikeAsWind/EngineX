package org.nstep.engine.module.infra.controller.admin.db.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 数据源配置 Response VO
 * <p>
 * 该类用于返回数据源配置的详细信息，包括数据源的主键编号、名称、连接信息、用户名以及创建时间等字段。
 * </p>
 */
@Schema(description = "管理后台 - 数据源配置 Response VO")
@Data
public class DataSourceConfigRespVO {

    /**
     * 主键编号
     * <p>
     * 唯一标识数据源配置的主键编号。
     * </p>
     */
    @Schema(description = "主键编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer id;

    /**
     * 数据源名称
     * <p>
     * 数据源的名称，用于标识不同的数据源。
     * </p>
     */
    @Schema(description = "数据源名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "test")
    private String name;

    /**
     * 数据源连接
     * <p>
     * 数据源的连接字符串，通常用于 JDBC 连接数据库。
     * </p>
     */
    @Schema(description = "数据源连接", requiredMode = Schema.RequiredMode.REQUIRED, example = "jdbc:mysql://127.0.0.1:3306/engine")
    private String url;

    /**
     * 用户名
     * <p>
     * 用于连接数据库的用户名。
     * </p>
     */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "root")
    private String username;

    /**
     * 创建时间
     * <p>
     * 数据源配置的创建时间。
     * </p>
     */
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
