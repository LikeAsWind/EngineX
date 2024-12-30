package org.nstep.engine.module.message.constant;

/**
 * 微信消息发送类型常量类
 * 该类包含了与微信消息发送相关的常量值，用于标识不同的消息类型、资源名称等
 */
public class WeChatConstants {

    /**
     * 微信服务号的 AccessToken 前缀
     * 用于标识微信服务号的 AccessToken，结合应用名称使用
     */
    public static final String WECHAT_SERVICE_ACCOUNT_ACCESS_TOKEN_PREFIX = MessageDataConstants.APPLICATION_NAME + "weChat_service_account";

    /**
     * 微信服务号 URL 名称
     * 用于标识微信服务号相关的 URL
     */
    public static final String WECHAT_SERVICE_ACCOUNT_URL_NAME = "weChat_url";

    // 消息类型常量
    /**
     * 文本消息类型
     * 用于标识微信消息类型为文本
     */
    public static final String TEXT = "10";

    /**
     * Markdown 消息类型
     * 用于标识微信消息类型为 Markdown 格式
     */
    public static final String MARKDOWN = "20";

    /**
     * 图片消息类型
     * 用于标识微信消息类型为图片
     */
    public static final String IMAGE = "30";

    /**
     * 图文消息类型
     * 用于标识微信消息类型为图文消息
     */
    public static final String NEWS = "40";

    /**
     * 文件消息类型
     * 用于标识微信消息类型为文件
     */
    public static final String FILE = "50";

    /**
     * 语音消息类型
     * 用于标识微信消息类型为语音
     */
    public static final String VOICE = "60";

    // 消息类型名称常量
    /**
     * 文本消息类型名称
     * 用于标识微信消息类型为文本
     */
    public static final String TEXT_NAME = "text";

    /**
     * Markdown 消息类型名称
     * 用于标识微信消息类型为 Markdown 格式
     */
    public static final String MARKDOWN_NAME = "markdown";

    /**
     * 图片消息类型名称
     * 用于标识微信消息类型为图片
     */
    public static final String IMAGE_NAME = "image";

    /**
     * 图文消息类型名称
     * 用于标识微信消息类型为图文消息
     */
    public static final String NEWS_NAME = "news";

    /**
     * 文件消息类型名称
     * 用于标识微信消息类型为文件
     */
    public static final String FILE_NAME = "file";

    /**
     * 语音消息类型名称
     * 用于标识微信消息类型为语音
     */
    public static final String VOICE_NAME = "voice";

    /**
     * 发送给所有人的标识
     * 用于标识消息发送对象为所有人
     */
    public static final String SEND_ALL = "@all";
}
