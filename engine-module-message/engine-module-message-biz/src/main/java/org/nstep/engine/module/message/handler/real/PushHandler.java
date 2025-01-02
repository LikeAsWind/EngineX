package org.nstep.engine.module.message.handler.real;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.message.constant.GeTuiConstants;
import org.nstep.engine.module.message.constant.SendChanelUrlConstant;
import org.nstep.engine.module.message.dto.message.TemplateSendTask;
import org.nstep.engine.module.message.dto.model.PushContentModel;
import org.nstep.engine.module.message.dto.push.getui.*;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.nstep.engine.module.message.util.AccountUtil;
import org.nstep.engine.module.message.util.DataUtil;
import org.nstep.engine.module.message.util.getui.AccessTokenUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;


/**
 * 个推Push通知栏处理器
 * 该类继承自 ChannelHandler，用于处理个推（GeTui）消息推送任务。它从模板发送任务中获取相关信息，获取token并进行单推或批量推送，最后记录发送结果。
 * 详情请参考个推API文档: <a href=https://docs.getui.com/getui/server/rest_v2/push/?id=doc-title-5></a>
 */
@Component
@Slf4j
public class PushHandler extends ChannelHandler {

    @Resource
    private AccountUtil accountUtil;  // 账户工具类，用于获取个推账户配置信息

    @Resource
    private StringRedisTemplate stringRedisTemplate;  // Redis工具类，用于存储和获取token

    @Resource
    private DataUtil dataUtil;  // 数据工具类，用于记录任务执行状态

    /**
     * 处理个推通知栏消息推送任务
     *
     * @param TemplateSendTask 消息模板发送任务对象，包含任务和消息的相关信息
     */
    @Override
    void doHandler(TemplateSendTask TemplateSendTask) {
        try {
            // 获取个推账户配置信息
            GeTuiConfig account = accountUtil.getAccount(TemplateSendTask.getMessageTemplate().getSendAccount(), GeTuiConfig.class);
            // 获取token
            String token = getToken(TemplateSendTask, account);
            if (Objects.isNull(token)) {
                // 如果获取不到token，记录失败信息并返回
                dataUtil.confirmSend(null, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(),
                        exception(ErrorCodeConstants.TOKEN_EXCEPTION));
                return;
            }
            String result;
            // 判断是否为单推任务或批量推送任务
            if (TemplateSendTask.getReceivers().size() == 1) {
                result = singlePush(TemplateSendTask, account, token);
                // 如果遇到token过期，重新获取token并发起请求
                if (JSON.parseObject(result, SendPushResult.class).getCode().equals(10001)) {
                    result = singlePush(TemplateSendTask, account, getToken(TemplateSendTask, account));
                }
            } else {
                result = batchPush(TemplateSendTask, account, token);
                // 如果遇到token过期，重新获取token并发起请求
                if (JSON.parseObject(result, SendPushResult.class).getCode().equals(10001)) {
                    result = batchPush(TemplateSendTask, account, getToken(TemplateSendTask, account));
                }
            }
            // 如果推送失败，记录失败信息并返回
            if (result == null || !JSON.parseObject(result, SendPushResult.class).getCode().equals(0)) {
                dataUtil.confirmSend(null, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(),
                        exception(ErrorCodeConstants.EMPTY_OBJECT, result));
                return;
            }

            // 记录推送成功的结果
            dataUtil.confirmSend(result, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId(), new Exception());
        } catch (Exception e) {
            // 记录异常信息
            dataUtil.confirmSend(null, TemplateSendTask.getMessageId(), TemplateSendTask.getSendMessageKey(), TemplateSendTask.getSendTaskId()
                    , exception(ErrorCodeConstants.EMPTY_OBJECT, e.getMessage()));
            log.error("APP通知栏消息推送异常:{}", e.getMessage());
        }
    }

    /**
     * 个推批量推送，同步发送
     *
     * @param TemplateSendTask 消息模板发送任务对象
     * @param account          个推账户配置信息
     * @param token            获取的个推token
     * @return 推送结果
     */
    private String batchPush(TemplateSendTask TemplateSendTask, GeTuiConfig account, String token) {
        String url = SendChanelUrlConstant.GE_TUI_BASE_URL + account.getAppId() + SendChanelUrlConstant.GE_TUI_BATCH_PUSH_PATH;

        String taskId = buildTaskId(TemplateSendTask, account, token);
        if (StrUtil.isBlank(taskId)) {
            throw exception(ErrorCodeConstants.BATCH_PUSH_TASK_ID_ERROR);
        }
        BatchSendPushParam batchSendPushParam = BatchSendPushParam.builder()
                .audience(BatchSendPushParam.AudienceVO.builder().cid(TemplateSendTask.getReceivers()).build())
                .taskId(taskId)
                .isAsync(false).build();

        return HttpRequest.post(url).header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .header("token", token)
                .body(JSON.toJSONString(batchSendPushParam))
                .timeout(2000)
                .execute().body();
    }

    /**
     * 获取批量推送任务ID
     *
     * @param TemplateSendTask 消息模板发送任务对象
     * @param account          个推账户配置信息
     * @param token            获取的个推token
     * @return 任务ID
     */
    private String buildTaskId(TemplateSendTask TemplateSendTask, GeTuiConfig account, String token) {
        String url = SendChanelUrlConstant.GE_TUI_BASE_URL + account.getAppId() + SendChanelUrlConstant.GE_TUI_BATCH_PUSH_CREATE_TASK_PATH;
        SingleSendPushParam sendPushParam = buildParam(TemplateSendTask);
        String body = HttpRequest.post(url).header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .header("token", token)
                .body(JSON.toJSONString(sendPushParam))
                .timeout(2000)
                .execute().body();
        SendPushResult sendPushResult = JSON.parseObject(body, SendPushResult.class);
        if (sendPushResult.getCode().equals(0)) {
            return sendPushResult.getData().getString("taskid");
        }
        return null;
    }

    /**
     * 个推单推
     *
     * @param TemplateSendTask 消息模板发送任务对象
     * @param account          个推账户配置信息
     * @param token            获取的个推token
     * @return 推送结果
     */
    private String singlePush(TemplateSendTask TemplateSendTask, GeTuiConfig account, String token) {
        String url = SendChanelUrlConstant.GE_TUI_BASE_URL + account.getAppId() + SendChanelUrlConstant.GE_TUI_SINGLE_PUSH_PATH;
        SingleSendPushParam singleSendPushParam = buildParam(TemplateSendTask);

        return HttpRequest.post(url).header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .header("token", token)
                .body(JSON.toJSONString(singleSendPushParam))
                .timeout(2000)
                .execute().body();
    }

    /**
     * 获取个推token
     *
     * @param TemplateSendTask 消息模板发送任务对象
     * @param account          个推账户配置信息
     * @return 获取的token
     */
    public String getToken(TemplateSendTask TemplateSendTask, GeTuiConfig account) {
        String token = stringRedisTemplate.opsForValue().get(GeTuiConstants.GE_TUI_TOKEN_KEY + TemplateSendTask.getMessageTemplate().getSendAccount());
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        GeTuiTokenResultDTO.DataDTO dataDTO = AccessTokenUtils.getGeTuiToken(account);
        if (Objects.isNull(dataDTO)) {
            return null;
        }
        token = dataDTO.getToken();
        // 将token存储到Redis，并设置过期时间
        stringRedisTemplate.opsForValue().set(GeTuiConstants.GE_TUI_TOKEN_KEY + TemplateSendTask.getMessageTemplate().getSendAccount(), token,
                GeTuiConstants.EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return token;
    }

    /**
     * 构建单推或批量推送消息参数
     *
     * @param TemplateSendTask 消息模板发送任务对象
     * @return 构建的推送参数
     */
    private SingleSendPushParam buildParam(TemplateSendTask TemplateSendTask) {
        PushContentModel contentModel = JSON.parseObject(TemplateSendTask.getMessageTemplate().getMsgContent(), PushContentModel.class);

        String clickType = contentModel.getClickType();
        SingleSendPushParam.PushMessageVO.NotificationVO notificationVO =
                SingleSendPushParam.PushMessageVO.NotificationVO.builder().build();
        if (GeTuiConstants.CLICK_TYPE_URL.equals(clickType)) {
            notificationVO.setUrl(contentModel.getUrl());
        }
        if (GeTuiConstants.CLICK_TYPE_INTENT.equals(clickType)) {
            notificationVO.setIntent(contentModel.getIntent());
        }
        if (GeTuiConstants.CLICK_TYPE_PAYLOAD.equals(clickType)) {
            notificationVO.setPayload(contentModel.getPayload());
        }
        if (GeTuiConstants.CLICK_TYPE_PAYLOAD_CUSTOM.equals(clickType)) {
            notificationVO.setPayload(contentModel.getPayload());
        }
        notificationVO.setTitle(contentModel.getTitle());
        notificationVO.setBody(contentModel.getBody());
        notificationVO.setClickType(contentModel.getClickType());
        notificationVO.setChannelLevel(contentModel.getChannelLevel());

        return SingleSendPushParam.builder()
                .requestId(String.valueOf(IdUtil.getSnowflake().nextId()))
                .audience(SingleSendPushParam.AudienceVO.builder().cid(TemplateSendTask.getReceivers()).build())
                .pushMessage(SingleSendPushParam.PushMessageVO.builder().notification(notificationVO).build())
                .build();
    }
}
