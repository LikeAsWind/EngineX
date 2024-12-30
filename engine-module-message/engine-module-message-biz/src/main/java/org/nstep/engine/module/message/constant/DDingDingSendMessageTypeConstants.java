package org.nstep.engine.module.message.constant;

/**
 * 钉钉消息发送类型常量类
 * 用于定义钉钉消息的不同类型和相关常量
 */
public class DDingDingSendMessageTypeConstants {

    /**
     * 消息类型常量：
     * 这些常量用于表示钉钉消息的不同类型，如文本、链接、markdown等
     */
    public static final String TEXT = "10";  // 文本类型
    public static final String LINK = "20";  // 链接类型
    public static final String MARKDOWN = "30";  // markdown类型
    public static final String ACTION_CARD = "40";  // 独立跳转actionCard类型
    public static final String FEED_CARD = "50";  // feedCard类型

    /**
     * 消息类型名称常量：
     * 用于描述不同消息类型的中文名称
     */
    public static final String TEXT_NAME = "文本类型";  // 文本类型名称
    public static final String LINK_NAME = "链接类型";  // 链接类型名称
    public static final String MARKDOWN_NAME = "markdown类型";  // markdown类型名称
    public static final String ACTION_CARD_NAME = "独立跳转actionCard类型";  // actionCard类型名称
    public static final String FEED_CARD_NAME = "feedCard类型";  // feedCard类型名称

    /**
     * 消息发送常量：
     *
     * @all 表示发送给所有人
     */
    public static final String SEND_ALL = "@all";  // 发送给所有人
}
