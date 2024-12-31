package org.nstep.engine.module.message.process.management;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateSendReqVO;
import org.nstep.engine.module.message.domain.content.ProcessContent;
import org.nstep.engine.module.message.domain.content.SendTaskParamContent;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 对变量数据进行统一分类
 * <p>
 * 该服务类用于处理变量数据的分类，将发送任务的接收者和占位符数据进行匹配和分类。
 * 它接收一个包含发送请求的上下文（`TemplateSendReqVO`），并将接收者与对应的占位符数据进行合并。
 */
@Component  // 将该类标注为 Spring 服务组件
@Slf4j  // 启用日志功能，方便记录日志信息
public class VariableClassificationProcess implements BusinessProcess {

    /**
     * 处理发送任务的逻辑
     * <p>
     * 该方法会根据接收到的上下文（`ProcessContent`）判断是否可以继续执行。如果上下文类型不匹配，则返回错误信息。
     * 如果占位符数据存在，则对接收者和占位符数据进行匹配，确保数据一致性；否则，仅处理接收者。
     *
     * @param context 发送任务的上下文，包含请求数据
     * @return 处理后的上下文，可能包含错误信息或处理结果
     */
    @Override
    public ProcessContent process(ProcessContent context) {
        // 确保上下文是 TemplateSendReqVO 类型，如果不是，则中断流程并返回错误信息
        if (!(context instanceof TemplateSendReqVO sendForm)) {
            log.warn("发送流程上下文断裂，PreCheckProcess.process");
            context.setIsNeedBreak(true);  // 标记需要中断后续流程
            context.setResponse(
                    CommonResult.error(
                            ErrorCodeConstants.PROCESS_CONTEXT_INTERRUPTION.getCode(),  // 错误码
                            ErrorCodeConstants.PROCESS_CONTEXT_INTERRUPTION.getMsg()   // 错误信息
                    )
            );
            return context;  // 返回中断后的上下文
        }

        // 初始化存储发送任务参数的容器
        Map<String, Set<String>> sendTaskParams = new HashMap<>();
        // 将接收者字符串拆分为 List，使用分隔符进行分割
        List<String> list = StrUtil.split(sendForm.getReceivers(), MessageDataConstants.SEPARATOR);
        // 接收者去重，避免重复发送相同的消息
        List<String> receivers = list.stream().distinct().collect(Collectors.toList());

        // 如果存在占位符数据，则进行处理
        if (sendForm.getIsExitVariables() != 0) {
            // 解析占位符数据（JSON 格式的字符串）
            List<String> jsonObjects = JSONUtil.toList(sendForm.getVariables(), String.class);

            // 校验接收者与占位符数据的数量是否一致
            if (receivers.size() != jsonObjects.size()) {
                // 如果数量不一致，则中断流程并返回错误信息
                context.setIsNeedBreak(true);
                context.setResponse(
                        CommonResult.error(
                                ErrorCodeConstants.RECEIVER_AND_PLACEHOLDER_DATA_COUNT_MISMATCH.getCode(),
                                ErrorCodeConstants.RECEIVER_AND_PLACEHOLDER_DATA_COUNT_MISMATCH.getMsg()
                        )
                );
                return context;  // 返回错误的上下文
            }

            // 合并接收者和占位符数据
            mergeData(sendTaskParams, receivers, jsonObjects);
        } else {
            // 如果没有占位符数据，直接将接收者放入默认的集合中
            sendTaskParams.put("", new HashSet<>(receivers));
        }

        // 构建并返回处理后的任务参数内容
        return SendTaskParamContent.builder()
                .sendTaskParams(sendTaskParams)  // 设置处理后的接收者与占位符数据
                .messageTemplateId(sendForm.getMessageTemplateId())  // 设置消息模板ID
                .sendChannel(sendForm.getSendChannel())  // 设置发送渠道
                .isExitVariables(sendForm.getIsExitVariables())  // 设置是否包含占位符数据
                .sender(sendForm.getSender())  // 设置发送方ID
                .build();  // 使用建造者模式构建 SendTaskParamContent 对象并返回
    }

    /**
     * 合并相同占位符数据
     * <p>
     * 该方法用于将接收者与占位符数据进行匹配，确保相同的占位符对应相同的接收者集合。
     * 如果某个占位符已经有接收者，则将新接收者加入该占位符的集合中；如果没有，则创建新的集合。
     *
     * @param sendTaskParams 存储占位符与接收者集合的 Map
     * @param receivers      接收者列表
     * @param jsonObjects    占位符数据列表
     */
    private void mergeData(Map<String, Set<String>> sendTaskParams, List<String> receivers, List<String> jsonObjects) {
        // 遍历所有接收者和占位符数据
        for (int i = 0; i < receivers.size(); i++) {
            // 如果该占位符数据还没有对应的接收者集合，创建新的集合并加入接收者
            if (sendTaskParams.get(jsonObjects.get(i)) == null) {
                HashSet<String> hashSet = new HashSet<>();
                hashSet.add(receivers.get(i));
                // 使用占位符数据作为 key，接收者集合作为 value
                sendTaskParams.put(jsonObjects.get(i), hashSet);
            }
            // 如果已经有对应的接收者集合，则直接将新的接收者加入集合中
            sendTaskParams.get(jsonObjects.get(i)).add(receivers.get(i));
        }
    }
}
