package org.nstep.engine.framework.operatelog.core.service;

import com.mzt.logapi.beans.LogRecord;
import com.mzt.logapi.service.ILogRecordService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.util.monitor.TracerUtils;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.nstep.engine.framework.security.core.LoginUser;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.module.system.api.logger.OperateLogApi;
import org.nstep.engine.module.system.api.logger.dto.OperateLogCreateReqDTO;

import java.util.List;

/**
 * 操作日志 ILogRecordService 实现类
 * <p>
 * 基于 {@link OperateLogApi} 实现，记录操作日志。
 * 该类实现了操作日志记录的核心功能，主要通过调用远程的操作日志 API 来记录操作日志。
 * </p>
 */
@Slf4j // 使用 Lombok 提供的 @Slf4j 注解，自动生成日志对象
public class LogRecordServiceImpl implements ILogRecordService {

    @Resource
    private OperateLogApi operateLogApi; // 注入 OperateLogApi，用于远程调用操作日志服务

    /**
     * 填充用户信息到请求 DTO 中
     * <p>
     * 使用 SecurityFrameworkUtils 获取当前登录用户信息，并将用户相关字段填充到请求 DTO 中。
     * 该方法考虑到可能的非 Web 场景（如 RPC、MQ、Job 等）。
     * </p>
     *
     * @param reqDTO 操作日志请求 DTO
     */
    private static void fillUserFields(OperateLogCreateReqDTO reqDTO) {
        // 使用 SecurityFrameworkUtils 获取当前登录用户
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return; // 如果没有登录用户，直接返回
        }
        reqDTO.setUserId(loginUser.getId()); // 填充用户 ID
        reqDTO.setUserType(loginUser.getUserType()); // 填充用户类型
    }

    /**
     * 填充模块信息到请求 DTO 中
     * <p>
     * 根据日志记录的内容，填充模块信息、业务编号、操作内容等字段。
     * </p>
     *
     * @param reqDTO    操作日志请求 DTO
     * @param logRecord 日志记录对象，包含操作的详细信息
     */
    public static void fillModuleFields(OperateLogCreateReqDTO reqDTO, LogRecord logRecord) {
        reqDTO.setType(logRecord.getType()); // 大模块类型，例如：CRM 客户
        reqDTO.setSubType(logRecord.getSubType()); // 操作名称，例如：转移客户
        reqDTO.setBizId(Long.parseLong(logRecord.getBizNo())); // 业务编号，例如：客户编号
        reqDTO.setAction(logRecord.getAction()); // 操作内容，例如：修改编号为 1 的用户信息
        reqDTO.setExtra(logRecord.getExtra()); // 拓展字段，记录一些复杂的业务信息，如订单编号等
    }

    /**
     * 填充请求信息到操作日志请求 DTO 中
     * <p>
     * 获取当前请求的 HTTP 方法、请求 URL、客户端 IP 和 User-Agent 信息，并填充到请求 DTO 中。
     * </p>
     *
     * @param reqDTO 操作日志请求 DTO
     */
    private static void fillRequestFields(OperateLogCreateReqDTO reqDTO) {
        // 获得当前 HTTP 请求对象
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return; // 如果没有请求对象，直接返回
        }
        // 填充请求信息
        reqDTO.setRequestMethod(request.getMethod()); // 请求方法（GET、POST 等）
        reqDTO.setRequestUrl(request.getRequestURI()); // 请求 URI
        reqDTO.setUserIp(ServletUtils.getClientIP(request)); // 客户端 IP 地址
        reqDTO.setUserAgent(ServletUtils.getUserAgent(request)); // 客户端 User-Agent
    }

    /**
     * 记录操作日志
     * <p>
     * 该方法通过填充请求 DTO 中的相关信息，并调用远程操作日志 API 进行日志记录。
     * </p>
     *
     * @param logRecord 日志记录对象，包含操作的详细信息
     */
    @Override
    public void record(LogRecord logRecord) {
        OperateLogCreateReqDTO reqDTO = new OperateLogCreateReqDTO();
        try {
            reqDTO.setTraceId(TracerUtils.getTraceId()); // 设置追踪 ID，用于分布式追踪
            // 填充用户信息
            fillUserFields(reqDTO);
            // 填充模块信息
            fillModuleFields(reqDTO, logRecord);
            // 填充请求信息
            fillRequestFields(reqDTO);

            // 异步记录操作日志
            operateLogApi.createOperateLogAsync(reqDTO);
        } catch (Throwable ex) {
            // 异常捕获，记录日志
            log.error("[record][url({}) log({}) 发生异常]", reqDTO.getRequestUrl(), reqDTO, ex);
        }
    }

    /**
     * 查询操作日志（不支持）
     * <p>
     * 该方法抛出 {@link UnsupportedOperationException}，因为操作日志的查询应该通过 {@link OperateLogApi} 进行。
     * </p>
     *
     * @param bizNo 业务编号
     * @param type  日志类型
     * @return 不支持操作
     */
    @Override
    public List<LogRecord> queryLog(String bizNo, String type) {
        throw new UnsupportedOperationException("使用 OperateLogApi 进行操作日志的查询");
    }

    /**
     * 根据业务编号查询操作日志（不支持）
     * <p>
     * 该方法抛出 {@link UnsupportedOperationException}，因为操作日志的查询应该通过 {@link OperateLogApi} 进行。
     * </p>
     *
     * @param bizNo   业务编号
     * @param type    日志类型
     * @param subType 子类型
     * @return 不支持操作
     */
    @Override
    public List<LogRecord> queryLogByBizNo(String bizNo, String type, String subType) {
        throw new UnsupportedOperationException("使用 OperateLogApi 进行操作日志的查询");
    }

}
