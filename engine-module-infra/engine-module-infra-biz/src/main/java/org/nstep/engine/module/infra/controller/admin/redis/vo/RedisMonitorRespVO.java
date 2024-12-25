package org.nstep.engine.module.infra.controller.admin.redis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Properties;

/**
 * 管理后台 - Redis 监控信息响应对象
 * 用于返回 Redis 监控相关的各种信息，包括 Redis 指令结果、数据库大小和命令统计等。
 */
@Schema(description = "管理后台 - Redis 监控信息 Response VO")
@Data
@Builder
@AllArgsConstructor
public class RedisMonitorRespVO {

    /**
     * Redis info 指令结果，包含 Redis 配置信息和状态信息
     * 具体字段请参考 Redis 官方文档
     */
    @Schema(description = "Redis info 指令结果,具体字段，查看 Redis 文档", requiredMode = Schema.RequiredMode.REQUIRED)
    private Properties info;

    /**
     * Redis 数据库的 key 数量
     */
    @Schema(description = "Redis key 数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long dbSize;

    /**
     * Redis 命令统计数据，包括每个命令的调用次数和 CPU 消耗等
     */
    @Schema(description = "CommandStat 数组", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CommandStat> commandStats;

    /**
     * Redis 命令统计信息
     * 包含命令名称、调用次数和 CPU 消耗时间等
     */
    @Schema(description = "Redis 命令统计结果")
    @Data
    @Builder
    @AllArgsConstructor
    public static class CommandStat {

        /**
         * Redis 命令的名称
         * 如 "get", "set" 等
         */
        @Schema(description = "Redis 命令", requiredMode = Schema.RequiredMode.REQUIRED, example = "get")
        private String command;

        /**
         * Redis 命令的调用次数
         */
        @Schema(description = "调用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
        private Long calls;

        /**
         * Redis 命令的 CPU 消耗时间，单位为微秒
         */
        @Schema(description = "消耗 CPU 秒数", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
        private Long usec;

    }

}
