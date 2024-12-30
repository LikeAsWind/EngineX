package org.nstep.engine.module.message.util;


import jakarta.annotation.Resource;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.nstep.engine.module.message.constant.MessageDataConstants.APPLICATION_NAME;

/**
 * RedisKeyUtil 工具类用于生成和管理与Redis相关的键值。
 * 主要用于生成消息ID、发送任务ID，以及根据用户和日期生成不同的Redis键。
 * 该类封装了Redis键的生成逻辑，提供了便捷的接口来处理Redis中存储的数据。
 */
@Component
public class RedisKeyUtil {

    // 用来生成当天消息的递增ID key: engineX:daily_msg_id:messageTemplateId
    private static final String DAILY_MSG_KEY = APPLICATION_NAME + "daily_msg_id:";

    // 用来生成当天消息任务的递增ID key: engineX:daily_task_id
    private static final String DAILY_TASK_KEY = APPLICATION_NAME + "daily_task_id";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成消息ID。
     * 根据消息模板ID生成一个唯一的消息ID，ID由模板ID和当天的递增次数组成。
     * 如果当天是第一次生成消息ID，会设置该ID的过期时间为当天0点。
     *
     * @param id 消息模板ID
     * @return 生成的消息ID
     */
    public long createMessageId(Long id) {
        String key = DAILY_MSG_KEY + id;
        Long increment = stringRedisTemplate.opsForValue().increment(key);
        if (increment != null && 1 == increment.intValue()) {
            // 设置 ID 为每天 0 点过期
            stringRedisTemplate.expireAt(key, LocalDate.now().atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        }
        // 以消息模板+当天发送次数作为ID
        String strId = String.valueOf(id) + increment;
        return Long.parseLong(strId);
    }

    /**
     * 生成发送任务ID。
     * 该ID是基于当天递增的ID生成的，并且会在每天0点过期。
     *
     * @return 生成的发送任务ID
     */
    public long createSendTaskId() {
        // 使用Long类型的对象进行操作，避免unboxing
        Long increment = stringRedisTemplate.opsForValue().increment(DAILY_TASK_KEY);
        // 检查increment是否为null，并且值是否等于1
        if (increment != null && increment == 1L) {
            // 设置 ID 为每天 0 点过期
            stringRedisTemplate.expireAt(DAILY_TASK_KEY, LocalDate.now().atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        }
        // 返回increment的值，如果increment为null，则返回0
        return increment != null ? increment : 0L;
    }

    /**
     * 获取Redis消息Key前缀组成 : engineX:message:userId:todayTime
     * 用于生成用户每天的消息存储Key。
     *
     * @param userId 用户ID
     * @return 生成的消息RedisKey
     */
    public static String createMessageRedisKey(Long userId) {
        return MessageDataConstants.MESSAGE_BUSINESS_NAME + userId.toString() + ":" + getCurrentDay();
    }

    /**
     * 获取当前日期的字符串格式（年月日）。例如：2023918。
     *
     * @return 当前日期的字符串格式
     */
    public static String getCurrentDay() {
        LocalDateTime currentDate = LocalDateTime.now();
        // 生成当天日期格式
        return currentDate.format(DateTimeFormatter.ofPattern(MessageDataConstants.REDIS_DAY_KEY_FORMAT));
    }

    /**
     * 获取指定用户和指定日期的消息RedisKey。
     * 用于生成用户在指定日期的消息存储Key。
     *
     * @param userId        用户ID
     * @param localDateTime 指定日期（格式：yyyyMMdd）
     * @return 生成的RedisKey
     */
    public static String getMessageRedisKey(Long userId, String localDateTime) {
        return MessageDataConstants.MESSAGE_BUSINESS_NAME + userId.toString() + ":" + localDateTime;
    }

    /**
     * 获取指定用户和指定日期发送成功的消息RedisKey。
     * 用于生成用户在指定日期发送成功的消息统计Key。
     *
     * @param userId        用户ID
     * @param localDateTime 指定日期（格式：yyyyMMdd）
     * @return 生成的RedisKey
     */
    public static String getSuccessRedisKey(Long userId, String localDateTime) {
        return MessageDataConstants.USER_SEND_TOTAL_SUCCESS + userId.toString() + ":" + localDateTime;
    }

    /**
     * 获取指定用户和指定日期发送失败的消息RedisKey。
     * 用于生成用户在指定日期发送失败的消息统计Key。
     *
     * @param userId        用户ID
     * @param localDateTime 指定日期（格式：yyyyMMdd）
     * @return 生成的RedisKey
     */
    public static String getFailRedisKey(Long userId, String localDateTime) {
        return MessageDataConstants.USER_SEND_TOTAL_FAIL + userId.toString() + ":" + localDateTime;
    }

    /**
     * 获取指定用户和指定日期发送中的消息RedisKey。
     * 用于生成用户在指定日期发送中的消息统计Key。
     *
     * @param userId        用户ID
     * @param localDateTime 指定日期（格式：yyyyMMdd）
     * @return 生成的RedisKey
     */
    public static String getSendingRedisKey(Long userId, String localDateTime) {
        return MessageDataConstants.USER_SEND_TOTAL_SENDING + userId.toString() + ":" + localDateTime;
    }

    /**
     * 获取指定用户和指定日期的发送渠道统计情况RedisKey。
     * 用于生成用户在指定日期的发送渠道统计Key。
     *
     * @param userId        用户ID
     * @param localDateTime 指定日期（格式：yyyyMMdd）
     * @return 生成的RedisKey
     */
    public static String getSendChannelCountRedisKey(Long userId, String localDateTime) {
        return MessageDataConstants.SEND_CHANNEL_COUNT + userId.toString() + ":" + localDateTime;
    }

    /**
     * 获取指定模板和指定日期的发送情况RedisKey。
     * 用于生成指定模板在指定日期的发送情况统计Key。
     *
     * @param id  模板ID
     * @param day 日期（格式：yyyyMMdd）
     * @return 生成的RedisKey
     */
    public static String getTemplateCountOfDayRedisKey(Long id, String day) {
        return MessageDataConstants.TEMPLATE_COUNT_DAY + id + ":" + day;
    }

    /**
     * 获取指定定时任务记录的RedisKey。
     * 用于生成指定用户和任务ID的定时任务状态Key。
     *
     * @param userId 用户ID
     * @param id     定时任务ID
     * @return 生成的RedisKey
     */
    public static String getCronTaskCordsRedisKey(Long userId, String id) {
        return MessageDataConstants.CRON_TASK_STATUS_KEY + userId + ":" + id;
    }

    /**
     * 获取指定用户和指定日期的下发总人数RedisKey。
     * 用于生成指定用户在指定日期的下发总人数统计Key。
     *
     * @param userId        用户ID
     * @param localDateTime 指定日期（格式：yyyyMMdd）
     * @return 生成的RedisKey
     */
    public static String getSendTotalOfDay(Long userId, String localDateTime) {
        return MessageDataConstants.SEND_TOTAL + userId + ":" + localDateTime;
    }
}
