package org.nstep.engine.module.message.constant;


public class QueueConstants {

    /**
     * 阻塞队列的最大任务数
     * <p>
     * 该常量定义了队列中最大可存储的任务数。当队列已满时，新的任务将被阻塞，直到队列有空闲空间。
     * 在此示例中，最大任务数被设置为 1024。
     * </p>
     */
    public static final Integer BIG_QUEUE_SIZE = 1024;

    /**
     * 各消息渠道 MQ 延迟队列的过期时间，单位：毫秒
     * <p>
     * 这些常量定义了不同消息渠道的延迟队列过期时间。过期时间是指消息在队列中等待的最大时间，超过这个时间，消息将被丢弃。
     * </p>
     */

    // 邮件渠道的延迟队列过期时间，单位为毫秒（60秒）
    public static final String EMAIL_EXPIRATION_TIME = "60000";

    // 短信渠道的延迟队列过期时间，单位为毫秒（30秒）
    public static final String SMS_EXPIRATION_TIME = "30000";

    // 钉钉机器人渠道的延迟队列过期时间，单位为毫秒（30秒）
    public static final String DING_DING_ROBOT_EXPIRATION_TIME = "30000";

    // 微信服务号渠道的延迟队列过期时间，单位为毫秒（30秒）
    public static final String WECHAT_SERVICE_ACCOUNT_EXPIRATION_TIME = "30000";

    // 推送渠道的延迟队列过期时间，单位为毫秒（30秒）
    public static final String PUSH_EXPIRATION_TIME = "30000";

    // 飞书机器人渠道的延迟队列过期时间，单位为毫秒（30秒）
    public static final String FEI_SHU_ROBOT_EXPIRATION_TIME = "30000";

    // 企业微信机器人渠道的延迟队列过期时间，单位为毫秒（30秒）
    public static final String ENTERPRISE_WECHAT_ROBOT_TIME = "30000";
}
