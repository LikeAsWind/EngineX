package org.nstep.engine.module.message.process.management;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dto.content.ProcessContent;
import org.nstep.engine.module.message.dto.content.SendTaskParamContent;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 接受者校验
 * <p>
 * 该服务类用于校验发送任务中的接收者是否符合特定渠道的格式规则。
 * 不同的发送渠道（如短信、邮件）有各自的格式校验规则，通过正则表达式进行匹配。
 * 如果发现非法接收者，会中断流程并返回错误信息。
 */
@Component
@Slf4j
public class ReceiverCheckProcess implements BusinessProcess {

    /**
     * 渠道类型枚举，包含渠道类型和对应的正则表达式
     */
    @Getter
    private enum ChannelType {
        EMAIL(MessageDataConstants.EMAIL, MessageDataConstants.EMAIL_REGEX_EXP),
        SMS(MessageDataConstants.SMS, MessageDataConstants.PHONE_REGEX_EXP);

        private final int channelId;  // 渠道 ID
        private final String regex;   // 渠道对应的正则表达式

        ChannelType(int channelId, String regex) {
            this.channelId = channelId;
            this.regex = regex;
        }

        /**
         * 根据渠道 ID 获取对应的正则表达式
         *
         * @param channelId 渠道 ID
         * @return 渠道对应的正则表达式，如果不存在则返回 null
         */
        public static String getRegexByChannelId(int channelId) {
            for (ChannelType type : values()) {
                if (type.getChannelId() == channelId) {
                    return type.getRegex();
                }
            }
            return null;  // 如果没有匹配的渠道 ID，则返回 null
        }
    }


    /**
     * 处理接受者校验逻辑
     * <p>
     * 根据上下文中的发送渠道和接收者信息，验证每个接收者是否符合渠道的格式规则。
     * 如果校验失败，则标记流程中断，并返回非法接收者的错误信息。
     *
     * @param context 发送任务的上下文，包含发送渠道和接收者信息
     * @return 处理后的上下文，如果校验失败，包含错误信息
     */
    @Override
    public ProcessContent process(ProcessContent context) {
        // 确保上下文是 SendTaskParamContent 类型，如果不是，则中断流程并返回错误信息
        if (!(context instanceof SendTaskParamContent sendTaskParamContext)) {
            context.setIsNeedBreak(true);  // 标记需要中断流程
            context.setResponse(
                    CommonResult.error(
                            ErrorCodeConstants.PROCESS_CONTEXT_INTERRUPTION.getCode(),  // 错误码
                            ErrorCodeConstants.PROCESS_CONTEXT_INTERRUPTION.getMsg()   // 错误信息
                    )
            );
            return context;  // 返回错误上下文
        }

        // 获取当前发送任务的发送渠道
        Integer sendChannel = sendTaskParamContext.getSendChannel();
        // 如果该渠道没有配置校验规则，直接返回上下文
        String regex = ChannelType.getRegexByChannelId(sendChannel);
        if (regex == null) {
            return context;
        }

        // 遍历接收者与占位符数据的映射
        sendTaskParamContext.getSendTaskParams().forEach((placeholder, receiver) -> {
            // 如果流程未被标记为中断，则进行校验（避免覆盖之前的校验结果）
            if (!context.getIsNeedBreak()) {
                check(receiver, regex, context);
            }
        });

        return context;  // 返回处理后的上下文
    }

    /**
     * 校验接收者格式
     * <p>
     * 该方法根据正则表达式校验接收者的格式是否合法。
     * 如果发现非法接收者，则记录日志，并标记流程中断，返回非法接收者的错误信息。
     *
     * @param receiver 接收者集合
     * @param regex    渠道对应的正则表达式
     * @param context  发送任务的上下文，用于存储校验结果
     */
    private void check(Set<String> receiver, String regex, ProcessContent context) {
        // 使用流操作筛选出非法接收者
        List<String> illegalReceiver = receiver.stream()
                .filter(r -> !ReUtil.isMatch(regex, r))  // 如果不匹配正则，则认为是非法接收者
                .collect(Collectors.toList());

        // 如果存在非法接收者
        if (CollectionUtil.isNotEmpty(illegalReceiver)) {
            log.warn("存在非法接受者:{}", illegalReceiver);  // 记录警告日志
            context.setIsNeedBreak(true);  // 标记流程中断
            context.setResponse(
                    CommonResult.error0(
                            ErrorCodeConstants.ILLEGAL_RECIPIENT.getCode(),  // 错误码
                            ErrorCodeConstants.ILLEGAL_RECIPIENT.getMsg(),   // 错误信息
                            illegalReceiver  // 返回非法接收者列表
                    )
            );
        }
    }
}
