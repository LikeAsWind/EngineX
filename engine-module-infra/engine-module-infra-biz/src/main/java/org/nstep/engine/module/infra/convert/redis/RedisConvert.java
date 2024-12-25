package org.nstep.engine.module.infra.convert.redis;

import cn.hutool.core.util.StrUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.nstep.engine.module.infra.controller.admin.redis.vo.RedisMonitorRespVO;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Redis转换器接口，用于构建Redis监控响应值对象。
 */
@Mapper
public interface RedisConvert {

    /**
     * 单例实例。
     */
    RedisConvert INSTANCE = Mappers.getMapper(RedisConvert.class);

    /**
     * 构建Redis监控响应值对象。
     *
     * @param info Redis统计信息。
     * @param dbSize Redis数据库大小。
     * @param commandStats Redis命令统计信息。
     * @return 构建后的Redis监控响应值对象。
     */
    default RedisMonitorRespVO build(Properties info, Long dbSize, Properties commandStats) {
        // 创建Redis监控响应值对象的builder，并设置info和dbSize
        RedisMonitorRespVO respVO = RedisMonitorRespVO.builder().info(info).dbSize(dbSize)
                // 初始化命令统计列表
                .commandStats(new ArrayList<>(commandStats.size())).build();

        // 遍历命令统计信息，并构建每个命令的统计对象
        commandStats.forEach((key, value) -> {
            // 提取命令名称
            String commandName = StrUtil.subAfter((String) key, "cmdstat_", false);
            // 提取命令调用次数
            String callsStr = StrUtil.subBetween((String) value, "calls=", ",");
            Long calls = callsStr != null ? Long.parseLong(callsStr) : 0L;
            // 提取命令使用时间（微秒）
            String usecStr = StrUtil.subBetween((String) value, "usec=", ",");
            Long usec = usecStr != null ? Long.parseLong(usecStr) : 0L;

            // 添加命令统计对象到列表中
            respVO.getCommandStats().add(RedisMonitorRespVO.CommandStat.builder()
                    .command(commandName)
                    .calls(calls)
                    .usec(usec)
                    .build());
        });

        // 返回构建后的Redis监控响应值对象
        return respVO;
    }

}