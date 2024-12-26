package org.nstep.engine.module.infra.controller.admin.redis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.infra.controller.admin.redis.vo.RedisMonitorRespVO;
import org.nstep.engine.module.infra.convert.redis.RedisConvert;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - Redis 监控的控制器类。
 */
@Tag(name = "管理后台 - Redis 监控")
@RestController
@RequestMapping("/infra/redis")
public class RedisController {

    /**
     * 自动注入的StringRedisTemplate实例，用于操作Redis。
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取Redis监控信息的接口。
     *
     * @return 返回一个包含Redis监控信息的响应对象。
     */
    @GetMapping("/get-monitor-info")
    @Operation(summary = "获得 Redis 监控信息")
    @PreAuthorize("@ss.hasPermission('infra:redis:get-monitor-info')")
    public CommonResult<RedisMonitorRespVO> getRedisMonitorInfo() {
        // 通过RedisTemplate执行回调，获取Redis的统计信息。
        Properties info = stringRedisTemplate.execute((RedisCallback<Properties>) RedisServerCommands::info);
        // 获取Redis数据库的大小。
        Long dbSize = stringRedisTemplate.execute(RedisServerCommands::dbSize);
        // 获取Redis命令统计信息。
        Properties commandStats = stringRedisTemplate.execute((
                RedisCallback<Properties>) connection -> connection.serverCommands().info("commandstats"));
        // 断言commandStats不为null，以避免编译器警告。
        assert commandStats != null;
        // 将获取到的信息转换并封装成响应对象返回。
        return success(RedisConvert.INSTANCE.build(info, dbSize, commandStats));
    }


}