package org.nstep.engine.module.system.api.logger;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.logger.dto.LoginLogCreateReqDTO;
import org.nstep.engine.module.system.service.logger.LoginLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 登录日志 API 实现类
 * <p>
 * 该类提供了登录日志相关的 RESTful API 接口，供 Feign 调用。
 * 主要功能包括创建登录日志。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class LoginLogApiImpl implements LoginLogApi {

    @Resource
    private LoginLogService loginLogService; // 登录日志服务，处理登录日志相关的业务逻辑

    /**
     * 创建登录日志
     *
     * @param reqDTO 登录日志创建请求 DTO
     * @return 创建成功的响应
     */
    @Override
    public CommonResult<Boolean> createLoginLog(LoginLogCreateReqDTO reqDTO) {
        // 调用登录日志服务创建登录日志
        loginLogService.createLoginLog(reqDTO);
        // 返回成功响应
        return success(true);
    }

}
