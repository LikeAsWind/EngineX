package org.nstep.engine.module.message.process.management;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.module.message.dto.message.TemplateSend;
import org.nstep.engine.module.message.dto.content.ProcessContent;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 数据前置检查
 * <p>
 * 该类实现了 BusinessProcess 接口，用于在消息发送责任链中对数据进行前置校验。
 * 主要校验内容包括：消息模板 ID、接收者、占位符赋值等，确保数据完整性。
 * 校验失败时中断责任链并返回对应的错误信息。
 */
@Component
@Slf4j
public class PreCheckProcess implements BusinessProcess {

    /**
     * 核心处理方法
     * <p>
     * 对消息发送请求的上下文进行校验，确保数据完整性和正确性。
     * 校验内容包括接收者是否为空、模板 ID 是否存在、占位符赋值是否正确等。
     *
     * @param context 责任链上下文对象，包含消息发送的相关信息。
     * @return 更新后的上下文对象，校验失败时中断责任链。
     */
    @Override
    public ProcessContent process(ProcessContent context) {
        // 校验上下文对象类型是否正确
        if (!(context instanceof TemplateSend templateSendReqVO)) {
            log.warn("发送流程上下文断裂，PreCheckProcess.process");
            context.setIsNeedBreak(true);
            context.setResponse(
                    CommonResult.error(
                            ErrorCodeConstants.PROCESS_CONTEXT_INTERRUPTION.getCode(),
                            ErrorCodeConstants.PROCESS_CONTEXT_INTERRUPTION.getMsg()
                    ));
            return context;
        }

        // 校验接收者是否为空
        if (StrUtil.isBlank(templateSendReqVO.getReceivers())) {
            log.warn("消息模板ID:{} 接收者为空", templateSendReqVO.getMessageTemplateId());
            templateSendReqVO.setIsNeedBreak(true);
            templateSendReqVO.setResponse(
                    CommonResult.error0(
                            ErrorCodeConstants.MESSAGE_TEMPLATE_ID_RECEIVER_EMPTY.getCode(),
                            ErrorCodeConstants.MESSAGE_TEMPLATE_ID_RECEIVER_EMPTY.getMsg(),
                            templateSendReqVO.getMessageTemplateId()
                    ));
            return context;
        }

        // 校验模板 ID 是否为空
        if (templateSendReqVO.getMessageTemplateId() == null) {
            log.warn("模板ID为空");
            templateSendReqVO.setIsNeedBreak(true);
            templateSendReqVO.setResponse(
                    CommonResult.error(
                            ErrorCodeConstants.TEMPLATE_ID_IS_NULL.getCode(),
                            ErrorCodeConstants.TEMPLATE_ID_IS_NULL.getMsg()
                    ));
            return context;
        }

        // 校验占位符赋值是否完整
        if (templateSendReqVO.getIsExitVariables() != 0) {
            if (StrUtil.isBlank(templateSendReqVO.getVariables())) {
                log.warn("消息模板ID:{} 消息模板带有占位符但未赋值!", templateSendReqVO.getMessageTemplateId());
                templateSendReqVO.setIsNeedBreak(true);
                templateSendReqVO.setResponse(
                        CommonResult.error0(
                                ErrorCodeConstants.MESSAGE_TEMPLATE_ID_PLACEHOLDER_NEEDS_VALUE.getCode(),
                                ErrorCodeConstants.MESSAGE_TEMPLATE_ID_PLACEHOLDER_NEEDS_VALUE.getMsg(),
                                templateSendReqVO.getMessageTemplateId()
                        ));
                return context;
            }

            // 校验占位符数据是否存在空值
            List<JSONObject> jsonObjects = JSONUtil.toList(templateSendReqVO.getVariables(), JSONObject.class);
            for (JSONObject jsonObject : jsonObjects) {
                if (jsonObject.keySet().size() < templateSendReqVO.getIsExitVariables()) {
                    log.warn("消息模板ID:{} 存在占位符数据为空", templateSendReqVO.getMessageTemplateId());
                    context.setIsNeedBreak(true);
                    context.setResponse(
                            CommonResult.error0(
                                    ErrorCodeConstants.MESSAGE_TEMPLATE_ID_PLACEHOLDER_DATA_EMPTY.getCode(),
                                    ErrorCodeConstants.MESSAGE_TEMPLATE_ID_PLACEHOLDER_DATA_EMPTY.getMsg(),
                                    templateSendReqVO.getMessageTemplateId()
                            ));
                    return context;
                }
                for (String key : jsonObject.keySet()) {
                    if (ObjectUtil.isEmpty(jsonObject.get(key))) {
                        context.setIsNeedBreak(true);
                        context.setResponse(
                                CommonResult.error0(
                                        ErrorCodeConstants.MESSAGE_TEMPLATE_ID_PLACEHOLDER_DATA_EMPTY.getCode(),
                                        ErrorCodeConstants.MESSAGE_TEMPLATE_ID_PLACEHOLDER_DATA_EMPTY.getMsg(),
                                        templateSendReqVO.getMessageTemplateId()
                                ));
                        return context;
                    }
                }
            }
        }

        // 校验无占位符时是否错误地赋值了数据
        if (templateSendReqVO.getIsExitVariables() == 0 && !StrUtil.isBlank(templateSendReqVO.getVariables())) {
            log.warn("消息模板ID:{} 无需要赋值的占位符但赋值了数据!", templateSendReqVO.getMessageTemplateId());
            templateSendReqVO.setIsNeedBreak(true);
            templateSendReqVO.setResponse(
                    CommonResult.error(
                            ErrorCodeConstants.NO_NEED_TO_ASSIGN_PLACEHOLDER.getCode(),
                            ErrorCodeConstants.NO_NEED_TO_ASSIGN_PLACEHOLDER.getMsg()
                    ));
            return context;
        }

        // 设置默认发送者为当前登录用户
        if (Objects.isNull(templateSendReqVO.getSender())) {
            templateSendReqVO.setSender(SecurityFrameworkUtils.getLoginUserId());
        }

        return templateSendReqVO;
    }
}
