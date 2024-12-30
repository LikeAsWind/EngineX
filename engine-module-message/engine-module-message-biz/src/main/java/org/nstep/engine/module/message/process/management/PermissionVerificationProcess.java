package org.nstep.engine.module.message.process.management;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateSendReqVO;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.dal.mysql.template.TemplateMapper;
import org.nstep.engine.module.message.domain.content.ProcessContent;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.springframework.stereotype.Component;


/**
 * 消息发送权限校验
 * <p>
 * 该类实现了 BusinessProcess 接口，用于校验消息模板的权限和状态。
 * 在责任链模式中作为一个处理节点，校验失败时会中断责任链。
 */
@Component
@Slf4j
public class PermissionVerificationProcess implements BusinessProcess {

    @Resource
    private TemplateMapper templateMapper;

    /**
     * 核心处理方法
     * <p>
     * 校验消息模板是否已通过审核，未通过审核时中断责任链。
     *
     * @param context 责任链上下文对象，包含消息发送的相关信息。
     * @return 返回更新后的上下文对象。
     */
    @Override
    public ProcessContent process(ProcessContent context) {
        // 获取发送请求中的消息模板 ID
        TemplateSendReqVO sendForm = (TemplateSendReqVO) context;

        // 根据模板 ID 查询模板信息
        TemplateDO messageTemplate = templateMapper.selectById(sendForm.getMessageTemplateId());

        // 校验模板审核状态，若未通过审核则中断责任链并返回错误信息
        if (!MessageDataConstants.AUDIT_PASS.equals(messageTemplate.getAuditStatus())) {
            log.warn("模板审核未通过，模板ID：{}", sendForm.getMessageTemplateId());
            context.setIsNeedBreak(true); // 设置中断标志
            context.setResponse(
                    CommonResult.error(
                            ErrorCodeConstants.TEMPLATE_NOT_APPROVED.getCode(), // 错误码
                            ErrorCodeConstants.TEMPLATE_NOT_APPROVED.getMsg()  // 错误信息
                    ));
            return context;
        }

        // 如果校验通过，继续责任链的后续处理
        return context;
    }
}
