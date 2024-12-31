package org.nstep.engine.module.message.process.management;


import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.dto.content.ProcessContent;
import org.nstep.engine.module.message.dto.content.SendContent;
import org.nstep.engine.module.message.dto.model.DingDingRobotContentModel;
import org.nstep.engine.module.message.util.DataUtil;
import org.springframework.stereotype.Component;


/**
 * 发送消息前进行类型映射转换的处理类
 * <p>
 * 该类实现了 `BusinessProcess` 接口，用于在消息推送流程中进行特定类型的映射和转换。
 * 例如，对于钉钉机器人消息，它会将消息内容中的发送类型进行转换。此过程主要用于处理推送消息的类型，
 * 如果不需要类型映射，可以在推送流程中去掉该处理过程。
 * </p>
 */
@Component
@Slf4j
public class TypeMappingProcess implements BusinessProcess {

    @Resource
    private DataUtil dataUtil; // 数据工具类，用于获取类型映射信息

    /**
     * 执行类型映射转换过程
     * <p>
     * 该方法接受一个 `ProcessContent` 对象作为输入，转换后返回相同类型的对象。
     * 具体来说，方法会根据 `SendContent` 中的发送任务信息进行类型处理：
     * - 如果消息的发送渠道为钉钉机器人（`DING_DING_ROBOT`），
     * 则会将消息内容的发送类型映射为相应的名称。
     * </p>
     *
     * @param context 处理上下文，包含了待处理的消息内容
     * @return 处理后的上下文，包含转换后的消息内容
     */
    @Override
    public ProcessContent process(ProcessContent context) {
        SendContent sendContext = (SendContent) context; // 将上下文转换为 SendContent 对象

        // 遍历所有发送任务进行类型映射转换
        for (TemplateSendTask sendTask : sendContext.getSendTasks()) {
            String msgContent = sendTask.getMessageTemplate().getMsgContent(); // 获取消息内容

            // 如果消息的发送渠道是钉钉机器人
            if (sendTask.getMessageTemplate().getSendChannel().equals(MessageDataConstants.DING_DING_ROBOT)) {
                // 将消息内容转换为钉钉机器人特定的内容模型
                DingDingRobotContentModel contentModel = JSON.parseObject(msgContent, DingDingRobotContentModel.class);

                // 获取并设置发送类型的映射（将原发送类型转换为对应的名称）
                contentModel.setSendType(dataUtil.sendTypeMapping.get(contentModel.getSendType()));

                // 将转换后的内容重新设置回消息模板中
                sendTask.getMessageTemplate().setMsgContent(JSON.toJSONString(contentModel));
            }
        }

        return context; // 返回处理后的上下文
    }
}
