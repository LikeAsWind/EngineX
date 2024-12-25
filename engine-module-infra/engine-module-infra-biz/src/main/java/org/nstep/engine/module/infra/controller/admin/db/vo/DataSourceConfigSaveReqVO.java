package org.nstep.engine.module.infra.controller.admin.db.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 数据源配置创建/修改 Request VO
 * <p>
 * 该类用于接收管理后台创建或修改数据源配置的请求参数，包括数据源的主键编号、名称、连接信息、用户名和密码等字段。
 * </p>
 */
@Schema(description = "管理后台 - 数据源配置创建/修改 Request VO")
@Data
public class DataSourceConfigSaveReqVO {

    /**
     * 主键编号
     * <p>
     * 用于唯一标识数据源配置的主键编号，仅在修改时需要提供。
     * </p>
     */
    @Schema(description = "主键编号", example = "1024")
    private Long id;

    /**
     * 数据源名称
     * <p>
     * 数据源的名称，用于标识不同的数据源。
     * </p>
     */
    @Schema(description = "数据源名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "test")
    @NotNull(message = "数据源名称不能为空")
    private String name;

    /**
     * 数据源连接
     * <p>
     * 数据源的连接字符串，通常用于 JDBC 连接数据库。
     * </p>
     */
    @Schema(description = "数据源连接", requiredMode = Schema.RequiredMode.REQUIRED, example = "jdbc:mysql://127.0.0.1:3306/engine")
    @NotNull(message = "数据源连接不能为空")
    private String url;

    /**
     * 用户名
     * <p>
     * 用于连接数据库的用户名。
     * </p>
     */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "root")
    @NotNull(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     * <p>
     * 用于连接数据库的密码。
     * </p>
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotNull(message = "密码不能为空")
    private String password;
}
