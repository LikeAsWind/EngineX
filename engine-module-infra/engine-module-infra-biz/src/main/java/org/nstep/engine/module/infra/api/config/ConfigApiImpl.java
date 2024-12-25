package org.nstep.engine.module.infra.api.config;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.infra.dal.dataobject.config.ConfigDO;
import org.nstep.engine.module.infra.service.config.ConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 配置 API 实现类
 * <p>
 * 该类实现了 ConfigApi 接口，提供了获取配置值的 RESTful API 接口。
 * 通过 Feign 调用该接口时，可以根据配置键获取对应的配置值。
 * </p>
 */
@RestController // 提供 RESTful API 接口，供 Feign 调用
@Validated // 开启 Spring 的参数校验功能
public class ConfigApiImpl implements ConfigApi {

    @Resource
    private ConfigService configService; // 注入 ConfigService，用于处理配置相关的业务逻辑

    /**
     * 根据配置键获取配置值
     *
     * @param key 配置键
     * @return 配置值，如果找不到对应的配置，返回 null
     */
    @Override
    public CommonResult<String> getConfigValueByKey(String key) {
        // 调用 ConfigService 获取配置
        ConfigDO config = configService.getConfigByKey(key);
        // 返回配置值，如果配置不存在，则返回 null
        return success(config != null ? config.getValue() : null);
    }

}
