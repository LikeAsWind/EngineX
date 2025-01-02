package org.nstep.engine.module.message.constant;

/**
 * 存储各类推送渠道的URL常量
 * 该类包含了多个推送平台（如个推、钉钉、企业微信等）的API接口URL。
 */
public class SendChanelUrlConstant {

    /**
     * 个推相关的URL常量
     * 这些常量用于与个推平台进行通信，发送消息、获取token等。
     */
    public static final String GE_TUI_BASE_URL = "https://restapi.getui.com/v2/"; // 个推基础URL
    public static final String GE_TUI_SINGLE_PUSH_PATH = "/push/single/cid"; // 个推单推接口路径
    public static final String GE_TUI_BATCH_PUSH_CREATE_TASK_PATH = "/push/list/message"; // 个推批量推送创建任务路径
    public static final String GE_TUI_BATCH_PUSH_PATH = "/push/list/cid"; // 个推批量推送路径
    public static final String GE_TUI_AUTH = "/auth"; // 个推认证接口路径

    /**
     * 钉钉工作消息相关的URL常量
     * 这些常量用于与钉钉平台进行工作消息推送、撤回等操作。
     */
    public static final String DING_DING_SEND_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2"; // 钉钉发送消息接口
    public static final String DING_DING_RECALL_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/recall"; // 钉钉撤回消息接口
    public static final String DING_DING_PULL_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/getsendresult"; // 钉钉获取消息发送结果接口
    public static final String DING_DING_UPLOAD_URL = "https://oapi.dingtalk.com/media/upload"; // 钉钉上传媒体文件接口
    public static final String DING_DING_TOKEN_URL = "https://oapi.dingtalk.com/gettoken"; // 钉钉获取token接口

    /**
     * 企业微信机器人相关的URL常量
     * 该常量用于与企业微信机器人接口进行消息推送。
     */
    public static final String ENTERPRISE_WE_CHAT_ROBOT_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/upload_media?key=<KEY>&type=<TYPE>"; // 企业微信机器人上传媒体文件接口

    /**
     * 支付宝小程序相关的URL常量
     * 该常量用于与支付宝小程序API进行交互。
     */
    public static final String ALI_MINI_PROGRAM_GATEWAY_URL = "https://openapi.alipaydev.com/gateway.do"; // 支付宝小程序网关接口

    /**
     * 微信小程序相关的URL常量
     * 该常量用于与微信小程序API进行交互，获取openid等。
     */
    public static final String WE_CHAT_MINI_PROGRAM_OPENID_SYNC = "https://api.weixin.qq.com/sns/jscode2session?appid=<APPID>&secret=<SECRET>&js_code=<CODE>&grant_type=authorization_code"; // 微信小程序获取openid接口
}
