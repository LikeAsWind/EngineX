package org.nstep.engine.module.message.constant;

public class GeTuiConstants {

    /**
     * 个推应用token的Redis键名
     * 该键名用于存储个推应用的token，格式为：engineX:ge_tui_token:accountId
     * 其中，accountId表示用户的唯一标识
     */
    public static final String GE_TUI_TOKEN_KEY = MessageDataConstants.APPLICATION_NAME + "ge_tui_token:";

    /**
     * 让用户的个推token在redis中23小时过期
     * 该值表示个推token的过期时间，单位为毫秒
     * 23小时 = 82800000毫秒
     */
    public static final long EXPIRE_TIME = 82800000;

    /**
     * 点击类型：URL
     * 当点击通知时，打开网页地址
     */
    public static final String CLICK_TYPE_URL = "url";

    /**
     * 点击类型：Intent
     * 当点击通知时，打开应用内特定页面
     */
    public static final String CLICK_TYPE_INTENT = "intent";

    /**
     * 点击类型：Payload
     * 当点击通知时，附加自定义消息内容启动应用
     */
    public static final String CLICK_TYPE_PAYLOAD = "payload";

    /**
     * 点击类型：Payload Custom
     * 当点击通知时，附加自定义消息内容不启动应用
     */
    public static final String CLICK_TYPE_PAYLOAD_CUSTOM = "payload_custom";

    /**
     * 点击类型：Start App
     * 当点击通知时，打开应用首页
     */
    public static final String CLICK_TYPE_STARTAPP = "startapp";

    /**
     * 点击类型：None
     * 当点击通知时，不执行任何操作，纯通知
     */
    public static final String CLICK_TYPE_NONE = "none";
}
