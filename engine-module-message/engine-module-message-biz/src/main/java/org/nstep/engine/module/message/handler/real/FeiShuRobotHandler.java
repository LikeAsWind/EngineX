package org.nstep.engine.module.message.handler.real;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.exception.ServiceException;
import org.nstep.engine.module.message.dto.feishu.FeiShuRobotConfig;
import org.nstep.engine.module.message.dto.feishu.FeiShuRobotParam;
import org.nstep.engine.module.message.dto.feishu.FeiShuRobotResult;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.dto.model.FeiShuRobotContentModel;
import org.nstep.engine.module.message.util.AccountUtil;
import org.nstep.engine.module.message.util.DataUtil;
import org.springframework.stereotype.Component;

/**
 * 飞书机器人处理器
 * 该类继承自 ChannelHandler，用于处理飞书机器人消息发送的相关逻辑。
 * 它从模板发送任务中获取配置信息，构建请求参数并调用飞书机器人的接口进行消息发送，最后记录发送结果。
 */
@Component
@Slf4j
public class FeiShuRobotHandler extends ChannelHandler {

    public static final String TEXT = "10";  // 消息类型常量，表示文本消息

    @Resource
    private AccountUtil accountUtil;  // 账户工具类，用于获取飞书机器人账户配置信息

    @Resource
    private DataUtil dataUtil;  // 数据工具类，用于记录任务执行状态

    /**
     * 处理飞书机器人消息发送任务
     *
     * @param TemplateSendTask 消息模板发送任务对象，包含任务和消息的相关信息
     */
    @Override
    void doHandler(TemplateSendTask TemplateSendTask) {
        try {
            // 获取飞书机器人账户配置信息
            FeiShuRobotConfig account = accountUtil.getAccount(TemplateSendTask.getMessageTemplate().getSendAccount(), FeiShuRobotConfig.class);
            // 获取消息内容模型
            FeiShuRobotContentModel contentModel = JSON.parseObject(TemplateSendTask.getMessageTemplate().getMsgContent(), FeiShuRobotContentModel.class);
            // 构建飞书机器人发送请求参数
            FeiShuRobotParam feiShuRobotParam = buildFeiShuRobotParam(contentModel);
            // 发送请求并获取响应结果
            String result = HttpRequest.post(account.getWebhook())
                    .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                    .body(JSON.toJSONString(feiShuRobotParam))
                    .timeout(2000)
                    .execute().body();
            // 解析响应结果
            FeiShuRobotResult feiShuRobotResult = JSON.parseObject(result, FeiShuRobotResult.class);

            // 如果发送失败，记录发送失败的信息
            if (feiShuRobotResult.getCode() != 0) {
                dataUtil.confirmSend(null, TemplateSendTask.getMessageId(),
                        TemplateSendTask.getSendMessageKey(),
                        TemplateSendTask.getSendTaskId(),
                        new ServiceException());
                return;
            }
            // 记录发送成功的结果
            dataUtil.confirmSend(result, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.sendTaskId, new Exception());
        } catch (Exception e) {
            // 记录发送失败的异常信息
            dataUtil.confirmSend(null, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(), e);
            // 记录异常日志
            log.error("飞书自定义机器人发送异常:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 构建飞书机器人发送请求参数
     *
     * @param contentModel 消息内容模型，包含发送的消息类型和内容
     * @return 构建的飞书机器人请求参数对象
     */
    public FeiShuRobotParam buildFeiShuRobotParam(FeiShuRobotContentModel contentModel) {
        FeiShuRobotParam.ContentDTO content = null;
        String msgType = null;
        // 判断消息类型，如果是文本消息，则构建文本消息的内容
        if (TEXT.equals(contentModel.getMsgType())) {
            content = FeiShuRobotParam.ContentDTO.builder().text(contentModel.getText().getContent()).build();
            msgType = "text";
        }
        // 返回构建的飞书机器人请求参数
        return FeiShuRobotParam.builder().content(content).msgType(msgType).build();
    }
}
