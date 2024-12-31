package org.nstep.engine.module.message.service.template;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.dal.mysql.template.TemplateMapper;
import org.nstep.engine.module.message.process.management.ProcessTemplate;
import org.nstep.engine.module.message.dto.content.ProcessContent;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.nstep.engine.module.message.process.management.BusinessProcess;
import org.nstep.engine.module.message.service.xxljob.XxlJobService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 消息管理服务实现类
 * <p>
 * 实现了消息发送的责任链逻辑，通过责任链模式依次执行各个业务处理器。
 * 同时提供了启动和停止消息任务的功能。
 *
 * @author yangzhitong
 */
@Service
@Validated
public class MessageManagementServiceImpl implements MessageManagementService {

    /**
     * 消息发送的责任链模板
     * <p>
     * 用于存储并管理一系列的业务处理器，按照顺序执行形成责任链。
     */
    @Resource
    private ProcessTemplate sendMessageTemplate;

    @Resource
    private TemplateMapper templateMapper;

    @Resource
    private XxlJobService xxlJobService;

    /**
     * 发送消息
     * <p>
     * 根据责任链模式依次执行业务处理器，并根据上下文状态决定是否中断责任链。
     *
     * @param content 消息发送的上下文对象，包含处理所需的所有信息。
     * @return 返回处理结果，包含成功或失败的详细信息。
     */
    @Override
    public CommonResult<?> send(ProcessContent content) {
        // 获取责任链中的所有业务处理器
        List<BusinessProcess> processes = sendMessageTemplate.getProcesses();
        for (BusinessProcess process : processes) {
            // 调用每个业务处理器的处理方法
            content = process.process(content);
            if (content.getIsNeedBreak()) {
                // 如果标记需要中断责任链，直接退出循环
                break;
            }
        }
        // 返回最终的处理结果
        return content.getResponse();
    }

    /**
     * 启动消息任务
     * <p>
     * 检查消息模板的状态并调用 XxlJobService 执行任务启动。
     *
     * @param id 消息模板的唯一标识 ID。
     */
    @Override
    public void start(Long id) {
        checkTemplateStatus(id);
        xxlJobService.start(id);
    }

    /**
     * 停止消息任务
     * <p>
     * 检查消息模板的状态并调用 XxlJobService 执行任务停止。
     *
     * @param id 消息模板的唯一标识 ID。
     */
    @Override
    public void stop(Long id) {
        checkTemplateStatus(id);
        xxlJobService.stop(id);
    }

    /**
     * 检查模板是否可使用
     * <p>
     * 验证模板是否处于允许使用状态以及审核是否通过。
     *
     * @param id 模板 ID。
     */
    private void checkTemplateStatus(Long id) {
        // 根据 ID 查询模板
        TemplateDO messageTemplate = templateMapper.selectById(id);
        // 检查模板是否被其他用户使用
        if (!MessageDataConstants.PERMITTED_USE.equals(String.valueOf(messageTemplate.getCurrentId()))) {
            if (!Objects.equals(SecurityFrameworkUtils.getLoginUserId(), messageTemplate.getCurrentId())) {
                throw exception(ErrorCodeConstants.TEMPLATE_ALREADY_USED); // 如果模板被占用，抛出异常
            }
        }
        // 检查模板是否已审核通过
        if (!MessageDataConstants.AUDIT_PASS.equals(messageTemplate.getAuditStatus())) {
            throw exception(ErrorCodeConstants.TEMPLATE_NOT_APPROVED);
        }
    }
}
