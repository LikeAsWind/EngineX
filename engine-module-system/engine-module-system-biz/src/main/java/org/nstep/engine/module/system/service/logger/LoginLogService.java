package org.nstep.engine.module.system.service.logger;

import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.module.system.api.logger.dto.LoginLogCreateReqDTO;
import org.nstep.engine.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import org.nstep.engine.module.system.dal.dataobject.logger.LoginLogDO;

/**
 * 登录日志 Service 接口
 */
public interface LoginLogService {

    /**
     * 获得登录日志分页
     *
     * @param pageReqVO 分页条件
     * @return 登录日志分页
     */
    PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO);

    /**
     * 创建登录日志
     *
     * @param reqDTO 日志信息
     */
    void createLoginLog(@Valid LoginLogCreateReqDTO reqDTO);

}