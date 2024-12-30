package org.nstep.engine.module.message.constant;

/**
 * 数据常量类，用于存储系统中的各种常量数据
 */
public class MessageDataConstants {

    // 字符串常量：用于分隔符
    public static final String SEPARATOR = ",";

    // JSON格式的左大括号
    public static final String JSON_LEFT_BRACE = "{";

    // JSON格式的右大括号
    public static final String JSON_RIGHT_BRACE = "}";

    /**
     * ！！！重要配置
     * 以下常量定义了不同消息渠道的标识
     */
    public static final Integer EMAIL = 10;  // 邮件
    public static final Integer SMS = 20;    // 短信
    public static final Integer DING_DING_ROBOT = 30;  // 钉钉机器人
    public static final Integer WECHAT_SERVICE_ACCOUNT = 40;  // 微信服务号
    public static final Integer PUSH = 50;   // 推送
    public static final Integer FEI_SHU_ROBOT = 60;  // 飞书机器人
    public static final Integer ENTERPRISE_WECHAT_ROBOT = 70;  // 企业微信机器人

    /**
     * handler后缀，用于标识处理消息的类名后缀
     */
    public static final String HANDLER_SUFFIX = "Handler";

    /**
     * 线程池后缀，用于标识线程池类名后缀
     */
    public static final String DtpExecutor_SUFFIX = "DtpExecutor";

    /**
     * ！！！重要配置
     * 配置支持的消息渠道
     * SUPPORT_CHANNEL_NAME 和 SUPPORT_CHANNEL 要一一对应 如 10 -> email
     */
    public static final String SUPPORT_CHANNEL = "10,20,30,40,50,60,70";  // 支持的消息渠道
    public static final String SUPPORT_CHANNEL_NAME = "email,sms,dingDingRobot,weChatServiceAccount,push,feiShuRobot,enterpriseWeChatRobot";  // 支持的消息渠道名称
    public static final String SUPPORT_CHANNEL_CN_NAME = "邮箱,短信,钉钉群机器人,微信公众号,APP通知栏,飞书机器人,企业微信机器人";  // 支持的消息渠道中文名称

    /**
     * 支持的第三方短信服务名称
     * 注意：要和对应的handler前缀对应，如：AlibabaCloudServiceSmsHandler
     */
    public static final String ALIBABA_CLOUD_SERVICE_SMS_NAME = "alibabaCloudServiceSms";
    public static final String TENCENT_CLOUD_SERVICE_SMS_NAME = "tencentCloudServiceSms";

    /**
     * 用于提取第三方短信服务名称的JSON对象key
     */
    public static final String SMS_SERVICE_KEY = "serviceName";

    /**
     * 占位符前缀，用于占位符替换
     */
    public static final String PLACE_HOLDER_PREFIX = "${";

    /**
     * 占位符后缀，用于占位符替换
     */
    public static final String PLACE_HOLDER_SUFFIX = "}";

    // 消息状态常量
    public static final Integer MSG_NEW = 0;  // 新消息
    public static final Integer MSG_STOP = 20;  // 停止消息
    public static final Integer MSG_START = 30;  // 启动消息
    public static final Integer MSG_SENDING = 40;  // 发送中
    public static final Integer MSG_SUCCESS = 50;  // 发送成功
    public static final Integer MSG_FAIL = 60;  // 发送失败

    // 消息操作常量
    public static final String SEND_CODE = "send";  // 发送
    public static final String RECALL_CODE = "recall";  // 撤回

    // 消息交换机和路由键
    public static final String EXCHANGE_NAME = "metax.point";
    public static final String TOPIC_KEY = "metax_KEY";
    public static final String ROTING_KEY = "metax_KEY";

    /**
     * Redis消息键前缀：engineX:message:userId:todayTime
     */
    public static final String APPLICATION_NAME = "engineX:";  // 应用名前缀
    public static final String MESSAGE_BUSINESS_NAME = APPLICATION_NAME + "message:";  // 消息业务前缀
    public static final String LOGIN_USER_ID = APPLICATION_NAME + "userId:";  // 用户ID前缀
    public static final String LOGIN_USER_NAME = APPLICATION_NAME + "userName:";  // 用户名前缀
    public static final String REDIS_DAY_KEY_FORMAT = "yyyyMMdd";  // 日期格式
    public static final String DAY_FORMAT_Y_M_D = "yyyy-MM-dd";  // 日期格式
    public static final String DAY_FORMAT_Y_M_D_H_M_S = "yyyy-MM-dd HH:mm:ss";  // 日期时间格式

    /**
     * Redis模板统计键前缀：engineX:template:userId
     * 用于统计当前用户所有发送过的消息模板次数
     */
    public static final String TEMPLATE_SEND_NUMBER_NAME = APPLICATION_NAME + "template:";

    /**
     * 用于统计当前用户当天发送过的消息模板次数：engineX:templateOfDay:userId:today
     */
    public static final String TEMPLATE_SEND_NUMBER_DAY = APPLICATION_NAME + "templateOfDay:";

    /**
     * 统计某个模板的当天发送情况：engineX:templateCount:templateId:today
     * hashMap结构：包括发送成功、发送失败、发送中
     */
    public static final String TEMPLATE_COUNT_DAY = APPLICATION_NAME + "templateCount:";

    /**
     * 用户总发送次数：engineX:userTotal:userId
     */
    public static final String USER_SEND_NUMBER = APPLICATION_NAME + "userTotal:";

    /**
     * 用户发送总失败数和成功数（一个接受者为一条且只有任务进入发送阶段才会被统计）
     * engineX:sendFail:userId:today
     * engineX:sendSuccess:userId:today
     */
    public static final String USER_SEND_TOTAL_FAIL = APPLICATION_NAME + "sendFail:";
    public static final String USER_SEND_TOTAL_SUCCESS = APPLICATION_NAME + "sendSuccess:";
    public static final String USER_SEND_TOTAL_SENDING = APPLICATION_NAME + "sending:";

    /**
     * 用户当天发送渠道统计情况：engineX:channelCount:userId:today
     */
    public static final String SEND_CHANNEL_COUNT = APPLICATION_NAME + "channelCount:";

    /**
     * 用户当天下发人数统计情况：engineX:userTotalOfDay:userId:today
     */
    public static final String SEND_TOTAL = APPLICATION_NAME + "userTotalOfDay:";

    /**
     * 消息发送类型常量
     */
    public static final Integer REAL_TIME = 10;  // 即时
    public static final Integer TIMING = 20;  // 定时

    /**
     * 用于记录当前任务第一次发送到Redis的key
     */
    public static final String SEND_MESSAGE_KEY = APPLICATION_NAME + "messageRedisKey";

    /**
     * 用于当前任务暂时存进Redis的sendTaskId的key
     */
    public static final String SEND_TASK_ID = APPLICATION_NAME + "sendTaskId";

    /**
     * 执行器地址注册类型
     */
    public static final Integer XXL_GROUP_TYPE_AUTO = 0;

    /**
     * 立即执行的任务延迟时间（秒数）
     */
    public static final Integer DELAY_TIME = 10;

    /**
     * 立即执行标识
     */
    public static final String PUSH_NOW = "0";

    /**
     * Cron时间格式
     */
    public final static String CRON_FORMAT = "ss mm HH dd MM ? yyyy";

    /**
     * 执行任务名称
     */
    public static final String JOB_HANDLER_NAME = "metaxJob";

    /**
     * 超时时间（秒）
     */
    public static final Integer TIME_OUT = 120;

    /**
     * 失败重试次数
     */
    public static final Integer RETRY_COUNT = 0;

    /**
     * 调度状态：0-停止，1-运行
     */
    public final static Integer TRIGGER_STATUS_TRUE = 1;
    public final static Integer TRIGGER_STATUS_FALSE = 0;

    /**
     * 默认邮件地址
     */
    public static final String NOTING_EMAIL = "123456@163.com";

    /**
     * 定时人群文件接受者表头名称
     */
    public static final String CRON_FILE_RECEIVER = "receiver";

    /**
     * 记录定时任务最近一次发送状态：任务开始、启动中、发送中、失败、发送成功、暂停
     */
    public static final String CRON_TASK_STARTING = "任务开始";
    public static final String CRON_TASK_SCHEDULING = "启动中";
    public static final String CRON_TASK_SENDING = "发送中";
    public static final String CRON_TASK_SUCCESS = "发送成功";
    public static final String CRON_TASK_STOP = "已暂停";
    public static final String CRON_TASK_FAIL = "发送失败";

    /**
     * 记录定时任务最近一次发送状态Redis键：engineX:cronTaskStatus:userId:messageTemplateId
     */
    public static final String CRON_TASK_STATUS_KEY = APPLICATION_NAME + "cronTaskStatus:";

    // 操作成功常量
    public static final String OK = "OK";

    /**
     * Redis最大模糊查询key个数
     */
    public static final Integer KEYS_SEARCH_MAX_VALUE = 10000;

    // 审核状态常量
    public static final Integer AUDIT_WAITING = 10;  // 审核中
    public static final Integer AUDIT_PASS = 20;  // 审核通过
    public static final Integer AUDIT_REJECTED = 30;  // 审核拒绝

    /**
     * 允许使用定时模板标识
     */
    public static final String PERMITTED_USE = "-1";

    /**
     * 用户近期短信回执查询记录：engineX:sms_records:userId
     */
    public static final String SMS_RECORDS_KEY = APPLICATION_NAME + "sms_records:";

    /**
     * 近期短信回执记录设置一个月后过期（单位：秒）
     */
    public static final long SMS_RECORD_EXPIRE_TIME = 2592000;  // 30天

    /**
     * 会话键前缀
     */
    public static final String TALK_KEY = APPLICATION_NAME + "talk:";

}
