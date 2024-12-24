package org.nstep.engine.framework.common.util.date;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.*;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 */
public class DateUtils {

    /**
     * 时区 - 默认
     */
    public static final String TIME_ZONE_DEFAULT = "GMT+8";

    /**
     * 秒转换成毫秒
     */
    public static final long SECOND_MILLIS = 1000;

    public static final String FORMAT_YEAR_MONTH_DAY = "yyyy-MM-dd";

    public static final String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";

    /**
     * 将 LocalDateTime 转换成 Date
     *
     * @param date LocalDateTime
     * @return LocalDateTime
     */
    public static Date of(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        // 将此日期时间与时区相结合以创建 ZonedDateTime
        ZonedDateTime zonedDateTime = date.atZone(ZoneId.systemDefault());
        // 本地时间线 LocalDateTime 到即时时间线 Instant 时间戳
        Instant instant = zonedDateTime.toInstant();
        // UTC时间(世界协调时间,UTC + 00:00)转北京(北京,UTC + 8:00)时间
        return Date.from(instant);
    }

    /**
     * 将 Date 转换成 LocalDateTime
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime of(Date date) {
        // 如果传入的 date 为 null，则直接返回 null
        if (date == null) {
            return null;
        }
        // 将 Date 转换为 Instant
        Instant instant = date.toInstant();
        // 使用系统默认时区将 Instant 转换为 LocalDateTime
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }


    /**
     * 将给定的 Duration 添加到当前时间，并返回一个新的 Date 对象
     *
     * @param duration 要添加的时间间隔
     * @return 添加时间间隔后的新 Date 对象
     */
    public static Date addTime(Duration duration) {
        // 获取当前时间的毫秒数
        long currentTimeMillis = System.currentTimeMillis();
        // 将 Duration 转换为毫秒数
        long durationMillis = duration.toMillis();
        // 将当前时间的毫秒数与 Duration 的毫秒数相加
        long newTimeMillis = currentTimeMillis + durationMillis;
        // 创建一个新的 Date 对象，表示添加时间间隔后的时间
        return new Date(newTimeMillis);
    }

    /**
     * 判断给定的日期是否已经过期
     *
     * @param time 要检查的日期
     * @return 如果已经过期则返回 true，否则返回 false
     */
    public static boolean isExpired(LocalDateTime time) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 如果当前时间在给定时间之后，则返回 true
        return now.isAfter(time);
    }


    /**
     * 创建指定时间
     *
     * @param year  年
     * @param mouth 月
     * @param day   日
     * @return 指定时间
     */
    public static Date buildTime(int year, int mouth, int day) {
        // 调用另一个 buildTime 方法，传入小时、分钟和秒的默认值 0
        return buildTime(year, mouth, day, 0, 0, 0);
    }


    /**
     * 创建指定时间
     *
     * @param year   年
     * @param mouth  月
     * @param day    日
     * @param hour   小时
     * @param minute 分钟
     * @param second 秒
     * @return 指定时间
     */
    public static Date buildTime(int year, int mouth, int day,
                                 int hour, int minute, int second) {
        // 获取当前时间的日历对象
        Calendar calendar = Calendar.getInstance();
        // 设置年
        calendar.set(Calendar.YEAR, year);
        // 设置月，注意月份是从0开始的，所以要减1
        calendar.set(Calendar.MONTH, mouth - 1);
        // 设置日
        calendar.set(Calendar.DAY_OF_MONTH, day);
        // 设置小时
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        // 设置分钟
        calendar.set(Calendar.MINUTE, minute);
        // 设置秒
        calendar.set(Calendar.SECOND, second);
        // 一般情况下，毫秒都是0
        calendar.set(Calendar.MILLISECOND, 0);
        // 返回设置好的时间
        return calendar.getTime();
    }


    /**
     * 比较两个 Date 对象，并返回较大的一个
     *
     * @param a 第一个 Date 对象
     * @param b 第二个 Date 对象
     * @return 较大的 Date 对象，如果两个对象相等，则返回第一个对象
     */
    public static Date max(Date a, Date b) {
        // 如果第一个对象为空，则返回第二个对象
        if (a == null) {
            return b;
        }
        // 如果第二个对象为空，则返回第一个对象
        if (b == null) {
            return a;
        }
        // 如果第一个对象在时间上晚于第二个对象，则返回第一个对象
        return a.compareTo(b) > 0 ? a : b;
    }


    /**
     * 比较两个 LocalDateTime 对象，并返回较大的一个
     *
     * @param a 第一个 LocalDateTime 对象
     * @param b 第二个 LocalDateTime 对象
     * @return 较大的 LocalDateTime 对象，如果两个对象相等，则返回第一个对象
     */
    public static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        // 如果第一个对象为空，则返回第二个对象
        if (a == null) {
            return b;
        }
        // 如果第二个对象为空，则返回第一个对象
        if (b == null) {
            return a;
        }
        // 如果第一个对象在时间上晚于第二个对象，则返回第一个对象
        return a.isAfter(b) ? a : b;
    }


    /**
     * 判断给定的日期是否是今天
     *
     * @param date 要检查的日期
     * @return 如果是今天则返回 true，否则返回 false
     */
    public static boolean isToday(LocalDateTime date) {
        // 使用 LocalDateTimeUtil 的 isSameDay 方法比较两个日期是否相同
        return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now());
    }

    /**
     * 判断给定的日期是否是昨天
     *
     * @param date 要检查的日期
     * @return 如果是昨天则返回 true，否则返回 false
     */
    public static boolean isYesterday(LocalDateTime date) {
        // 获取当前日期并减去一天
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        // 使用 LocalDateTimeUtil 的 isSameDay 方法比较两个日期是否相同
        return LocalDateTimeUtil.isSameDay(date, yesterday);
    }


}
