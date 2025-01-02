package org.nstep.engine.module.message.handler.cron;


import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.dal.mysql.template.TemplateMapper;
import org.nstep.engine.module.message.dto.content.CronTaskContent;
import org.nstep.engine.module.message.dto.content.ProcessContent;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.nstep.engine.module.message.process.management.BusinessProcess;
import org.nstep.engine.module.message.process.management.ProcessTemplate;
import org.nstep.engine.module.message.service.template.MessageManagementService;
import org.nstep.engine.module.message.util.DataUtil;
import org.springframework.stereotype.Component;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;


/**
 * 定时任务处理实现类
 * 该类实现了定时任务处理接口 CronTaskHandler，负责处理定时任务的执行逻辑。
 * 主要通过模板管理器、消息发送服务等组件来执行任务。
 */
@Component
public class CronTaskHandlerImpl implements CronTaskHandler {

    @Resource
    private ProcessTemplate cronTaskTemplate;  // 定时任务模板，包含任务流程的定义

    @Resource
    private TemplateMapper templateMapper;    // 模板管理器，用于从数据库中查询任务模板

    @Resource
    private MessageManagementService sendMessageService;  // 消息发送服务，用于发送消息

    @Resource
    private DataUtil dataUtil;  // 数据工具类，用于记录任务执行状态

    /**
     * 处理定时任务
     *
     * @param id     定时任务的ID，用于查询任务模板
     * @param sender 任务的发送者ID
     */
    @Override
    public void Handler(Long id, Long sender) {
        // 根据任务ID查询模板
        TemplateDO messageTemplate = templateMapper.selectById(id);

        // 构建任务处理内容对象
        ProcessContent content = CronTaskContent.builder().messageTemplate(messageTemplate).sender(sender).build();

        // 执行任务流程
        for (BusinessProcess process : cronTaskTemplate.getProcesses()) {
            // 处理当前流程
            content = process.process(content);

            // 如果流程需要中止，则记录错误信息并抛出异常
            if (content.getIsNeedBreak()) {
                recordError(messageTemplate, content.getResponse(), sender);
                throw exception(ErrorCodeConstants.EMPTY_OBJECT, content.getResponse().getMsg());
            }
        }

        // 进入消息发送流程
        CommonResult<?> result = sendMessageService.send(content);

        // 如果消息发送失败，记录错误信息并抛出异常
        if (result.isError()) {
            recordError(messageTemplate, result, sender);
            throw exception(ErrorCodeConstants.EMPTY_OBJECT, content.getResponse().getMsg());
        }
    }

    /**
     * 记录定时任务执行中的错误信息
     *
     * @param messageTemplate 任务模板
     * @param result          执行结果
     * @param sender          任务发送者ID
     */
    private void recordError(TemplateDO messageTemplate, CommonResult<?> result, Long sender) {
        // 记录任务失败的状态和错误信息
        dataUtil.recordCronTaskStatus(MessageDataConstants.CRON_TASK_FAIL, messageTemplate.getId(), sender, result.getMsg());
    }
}
