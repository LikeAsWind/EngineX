package org.nstep.engine.framework.common.util.date;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import org.nstep.engine.framework.common.enums.DateIntervalEnum;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * 时间工具类，用于 {@link java.time.LocalDateTime}
 */
public class LocalDateTimeUtils {

    /**
     * 空的 LocalDateTime 对象，主要用于 DB 唯一索引的默认值
     */
    public static LocalDateTime EMPTY = buildTime(1970, 1, 1);

    /**
     * 解析时间
     * 相比 {@link LocalDateTimeUtil#parse(CharSequence)} 方法来说，会尽量去解析，直到成功
     *
     * @param time 时间
     * @return 时间字符串
     */
    public static LocalDateTime parse(String time) {
        try {
            // 尝试使用默认的日期时间格式解析字符串
            return LocalDateTimeUtil.parse(time, DatePattern.NORM_DATE_PATTERN);
        } catch (DateTimeParseException e) {
            // 如果解析失败，捕获异常并尝试使用更宽松的解析方式
            return LocalDateTimeUtil.parse(time);
        }
    }


    /**
     * 将给定的 Duration 添加到当前时间，并返回一个新的 LocalDateTime 对象
     *
     * @param duration 要添加的时间间隔
     * @return 添加时间间隔后的新 LocalDateTime 对象
     */
    public static LocalDateTime addTime(Duration duration) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 将 Duration 添加到当前时间
        return now.plus(duration);
    }


    /**
     * 从当前时间减去给定的 Duration，并返回一个新的 LocalDateTime 对象
     *
     * @param duration 要减去的时间间隔
     * @return 减去时间间隔后的新 LocalDateTime 对象
     */
    public static LocalDateTime minusTime(Duration duration) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 从当前时间减去 Duration
        return now.minus(duration);
    }

    /**
     * 判断给定的日期是否在当前时间之前
     *
     * @param date 要检查的日期
     * @return 如果在当前时间之前则返回 true，否则返回 false
     */
    public static boolean beforeNow(LocalDateTime date) {
        // 将给定的日期与当前日期进行比较
        return date.isBefore(LocalDateTime.now());
    }

    /**
     * 判断给定的日期是否在当前时间之后
     *
     * @param date 要检查的日期
     * @return 如果在当前时间之后则返回 true，否则返回 false
     */
    public static boolean afterNow(LocalDateTime date) {
        // 将给定的日期与当前日期进行比较
        return date.isAfter(LocalDateTime.now());
    }

    /**
     * 创建指定时间
     *
     * @param year  年
     * @param mouth 月
     * @param day   日
     * @return 指定时间
     */
    public static LocalDateTime buildTime(int year, int mouth, int day) {
        // 使用 LocalDateTime 的 of 方法创建一个指定日期和时间的对象
        // 时间部分设置为 00:00:00
        return LocalDateTime.of(year, mouth, day, 0, 0, 0);
    }


    /**
     * 创建两个指定日期的 LocalDateTime 对象
     *
     * @param year1  第一个日期的年
     * @param mouth1 第一个日期的月
     * @param day1   第一个日期的日
     * @param year2  第二个日期的年
     * @param mouth2 第二个日期的月
     * @param day2   第二个日期的日
     * @return 包含两个 LocalDateTime 对象的数组
     */
    public static LocalDateTime[] buildBetweenTime(int year1, int mouth1, int day1, int year2, int mouth2, int day2) {
        // 创建第一个日期的 LocalDateTime 对象
        LocalDateTime time1 = buildTime(year1, mouth1, day1);
        // 创建第二个日期的 LocalDateTime 对象
        LocalDateTime time2 = buildTime(year2, mouth2, day2);
        // 将两个 LocalDateTime 对象放入数组中并返回
        return new LocalDateTime[]{time1, time2};
    }


    /**
     * 判断指定的时间是否在给定的开始时间和结束时间之间
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param time      要检查的时间
     * @return 如果在时间范围内则返回 true，否则返回 false
     */
    public static boolean isBetween(LocalDateTime startTime, LocalDateTime endTime, String time) {
        // 检查输入参数是否为 null
        if (startTime == null || endTime == null || time == null) {
            // 如果有任何一个参数为 null，则返回 false
            return false;
        }
        // 尝试将字符串解析为 LocalDateTime 对象
        LocalDateTime parsedTime = parse(time);
        // 如果解析成功，则检查该时间是否在指定的时间范围内
        return LocalDateTimeUtil.isIn(parsedTime, startTime, endTime);
    }


    /**
     * 判断当前时间是否在该时间范围内
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 是否
     */
    public static boolean isBetween(LocalDateTime startTime, LocalDateTime endTime) {
        // 检查输入参数是否为 null
        if (startTime == null || endTime == null) {
            // 如果有任何一个参数为 null，则返回 false
            return false;
        }
        // 使用 LocalDateTimeUtil 类的 isIn 方法判断当前时间是否在指定的时间范围内
        return LocalDateTimeUtil.isIn(LocalDateTime.now(), startTime, endTime);
    }


    /**
     * 判断当前时间是否在该时间范围内
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 是否
     */
    public static boolean isBetween(String startTime, String endTime) {
        // 检查输入参数是否为 null
        if (startTime == null || endTime == null) {
            // 如果有任何一个参数为 null，则返回 false
            return false;
        }
        // 获取当前日期
        LocalDate nowDate = LocalDate.now();
        // 使用 LocalDateTimeUtil 类的 isIn 方法判断当前时间是否在指定的时间范围内
        // 这里将字符串解析为 LocalTime 对象，然后与当前日期组合成 LocalDateTime 对象
        return LocalDateTimeUtil.isIn(LocalDateTime.now(), LocalDateTime.of(nowDate, LocalTime.parse(startTime)), LocalDateTime.of(nowDate, LocalTime.parse(endTime)));
    }


    /**
     * 判断时间段是否重叠
     *
     * @param startTime1 开始 time1
     * @param endTime1   结束 time1
     * @param startTime2 开始 time2
     * @param endTime2   结束 time2
     * @return 重叠：true 不重叠：false
     */
    public static boolean isOverlap(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        // 获取当前日期
        LocalDate nowDate = LocalDate.now();
        // 使用 LocalDateTimeUtil 类的 isOverlap 方法判断两个时间段是否重叠
        // 这里将 LocalTime 对象转换为 LocalDateTime 对象，以便进行比较
        return LocalDateTimeUtil.isOverlap(LocalDateTime.of(nowDate, startTime1), LocalDateTime.of(nowDate, endTime1), LocalDateTime.of(nowDate, startTime2), LocalDateTime.of(nowDate, endTime2));
    }


    /**
     * 获取指定日期所在的月份的开始时间
     * 例如：2023-09-30 00:00:00,000
     *
     * @param date 日期
     * @return 月份的开始时间
     */
    public static LocalDateTime beginOfMonth(LocalDateTime date) {
        // 使用 with 方法设置日期为该月的第一天
        date = date.with(TemporalAdjusters.firstDayOfMonth());
        // 使用 with 方法设置时间为当天的最小时间（00:00:00,000）
        return date.with(LocalTime.MIN);
    }


    /**
     * 获取指定日期所在的月份的最后时间
     * 例如：2023-09-30 23:59:59,999
     *
     * @param date 日期
     * @return 月份的结束时间
     */
    public static LocalDateTime endOfMonth(LocalDateTime date) {
        // 使用 with 方法设置日期为该月的最后一天
        date = date.with(TemporalAdjusters.lastDayOfMonth());
        // 使用 with 方法设置时间为当天的最大时间（23:59:59,999）
        return date.with(LocalTime.MAX);
    }


    /**
     * 获得指定日期所在季度
     *
     * @param date 日期
     * @return 所在季度
     */
    public static int getQuarterOfYear(LocalDateTime date) {
        // 获取日期的月份值
        int monthValue = date.getMonthValue();
        // 通过月份值计算季度，公式为 (monthValue - 1) / 3 + 1
        // 返回计算得到的季度
        return (monthValue - 1) / 3 + 1;
    }


    /**
     * 获取指定日期到现在过了几天，如果指定日期在当前日期之后，获取结果为负
     *
     * @param dateTime 日期
     * @return 相差天数
     */
    public static Long between(LocalDateTime dateTime) {
        // 调用 LocalDateTimeUtil 类的 between 方法，计算 dateTime 与当前时间的天数差
        return LocalDateTimeUtil.between(dateTime, LocalDateTime.now(), ChronoUnit.DAYS);
    }


    /**
     * 获取今天的开始时间
     *
     * @return LocalDateTime 表示今天的开始时间，即当天零点。
     * 例如，如果当前时间是 2024-12-24 15:30:00，
     * 返回值将是 2024-12-24 00:00:00。
     * 方法说明：
     * - 使用 LocalDateTime.now() 获取当前时间。
     * - 调用 LocalDateTimeUtil.beginOfDay() 方法，将当前时间调整为当天的零点。
     * - LocalDateTimeUtil 是一个工具类，假设它提供了对日期时间的便捷操作。
     */
    public static LocalDateTime getToday() {
        return LocalDateTimeUtil.beginOfDay(LocalDateTime.now());
    }


    /**
     * 获取昨天的开始时间
     *
     * @return LocalDateTime 表示昨天的开始时间，即昨天的零点。
     * 例如，如果当前时间是 2024-12-24 15:30:00，
     * 返回值将是 2024-12-23 00:00:00。
     * 方法说明：
     * - 使用 LocalDateTime.now() 获取当前时间。
     * - 调用 minusDays(1) 方法，将当前时间减去一天，得到昨天的时间。
     * - 使用 LocalDateTimeUtil.beginOfDay() 方法，将时间调整为昨天的零点。
     * - LocalDateTimeUtil 是一个工具类，假设它提供了对日期时间的便捷操作。
     */
    public static LocalDateTime getYesterday() {
        return LocalDateTimeUtil.beginOfDay(LocalDateTime.now().minusDays(1));
    }


    /**
     * 获取本月的开始时间
     *
     * @return LocalDateTime 表示本月的开始时间，即本月第一天的零点。
     * 例如，如果当前时间是 2024-12-24 15:30:00，
     * 返回值将是 2024-12-01 00:00:00。
     * 方法说明：
     * - 使用 LocalDateTime.now() 获取当前时间。
     * - 调用 beginOfMonth() 方法，将当前时间调整为本月的第一天零点。
     * - beginOfMonth() 方法假设是一个工具方法，用于计算指定时间所在月份的开始时间。
     */
    public static LocalDateTime getMonth() {
        return beginOfMonth(LocalDateTime.now());
    }


    /**
     * 获取本年的开始时间
     *
     * @return LocalDateTime 表示本年的开始时间，即本年第一天的零点。
     * 例如，如果当前时间是 2024-12-24 15:30:00，
     * 返回值将是 2024-01-01 00:00:00。
     * 方法说明：
     * - 使用 LocalDateTime.now() 获取当前时间。
     * - 调用 with(TemporalAdjusters.firstDayOfYear()) 方法，将当前时间调整为本年的第一天。
     * - 调用 with(LocalTime.MIN) 方法，将时间部分设置为当天的零点（即 00:00:00）。
     * - TemporalAdjusters 是一个工具类，提供了多种日期调整器，用于快速计算特定日期。
     */
    public static LocalDateTime getYear() {
        return LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfYear()) // 设置为本年第一天
                .with(LocalTime.MIN);                    // 设置时间为零点
    }


    /**
     * 根据起始时间和间隔类型，生成一组时间范围列表。
     *
     * @param startTime 起始时间，范围的开始时间。
     * @param endTime   结束时间，范围的结束时间。
     * @param interval  时间间隔类型，枚举值，表示按天、周、月、季度或年生成范围。
     *                  对应的枚举为 {@link DateIntervalEnum}。
     * @return List<LocalDateTime [ ]> 包含一组时间范围，每个范围用一个长度为 2 的数组表示，
     * 第一个元素为开始时间，第二个元素为结束时间。
     * <p>
     * 方法说明：
     * 1. 根据传入的 `interval` 参数，找到对应的时间间隔枚举。
     * 2. 将起始时间和结束时间对齐到当天的开始时间和结束时间。
     * 3. 根据间隔类型循环生成时间范围列表：
     * - 按天（DAY）：每个范围为一天。
     * - 按周（WEEK）：每个范围为一周，从起始时间开始，到周日结束。
     * - 按月（MONTH）：每个范围为一个月，从起始时间开始，到月末结束。
     * - 按季度（QUARTER）：每个范围为一个季度，从起始时间开始，到季度末结束。
     * - 按年（YEAR）：每个范围为一年，从起始时间开始，到年末结束。
     * 4. 确保最后一个时间范围的结束时间不会超过 `endTime`。
     * <p>
     * 注意：
     * - 如果 `interval` 无法匹配到有效的枚举，会抛出异常。
     * - 如果 `startTime` 在 `endTime` 之后，返回的列表将为空。
     */
    public static List<LocalDateTime[]> getDateRangeList(LocalDateTime startTime, LocalDateTime endTime, Integer interval) {
        // 1. 获取时间间隔枚举
        DateIntervalEnum intervalEnum = DateIntervalEnum.valueOf(interval);
        Assert.notNull(intervalEnum, "interval({}} 找不到对应的枚举", interval);

        // 2. 对齐时间到当天的开始和结束
        startTime = LocalDateTimeUtil.beginOfDay(startTime);
        endTime = LocalDateTimeUtil.endOfDay(endTime);

        // 3. 初始化时间范围列表
        List<LocalDateTime[]> timeRanges = new ArrayList<>();

        // 4. 根据间隔类型生成时间范围
        switch (intervalEnum) {
            case DAY:
                while (startTime.isBefore(endTime)) {
                    timeRanges.add(new LocalDateTime[]{startTime, startTime.plusDays(1).minusNanos(1)});
                    startTime = startTime.plusDays(1);
                }
                break;
            case WEEK:
                while (startTime.isBefore(endTime)) {
                    LocalDateTime endOfWeek = startTime.with(DayOfWeek.SUNDAY).plusDays(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, endOfWeek});
                    startTime = endOfWeek.plusNanos(1);
                }
                break;
            case MONTH:
                while (startTime.isBefore(endTime)) {
                    LocalDateTime endOfMonth = startTime.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, endOfMonth});
                    startTime = endOfMonth.plusNanos(1);
                }
                break;
            case QUARTER:
                while (startTime.isBefore(endTime)) {
                    int quarterOfYear = getQuarterOfYear(startTime);
                    LocalDateTime quarterEnd = quarterOfYear == 4
                            ? startTime.with(TemporalAdjusters.lastDayOfYear()).plusDays(1).minusNanos(1)
                            : startTime.withMonth(quarterOfYear * 3 + 1).withDayOfMonth(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, quarterEnd});
                    startTime = quarterEnd.plusNanos(1);
                }
                break;
            case YEAR:
                while (startTime.isBefore(endTime)) {
                    LocalDateTime endOfYear = startTime.with(TemporalAdjusters.lastDayOfYear()).plusDays(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, endOfYear});
                    startTime = endOfYear.plusNanos(1);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid interval: " + interval);
        }

        // 5. 确保最后一个时间范围的结束时间不超过 endTime
        LocalDateTime[] lastTimeRange = CollUtil.getLast(timeRanges);
        if (lastTimeRange != null) {
            lastTimeRange[1] = endTime;
        }

        return timeRanges;
    }


    /**
     * 格式化时间范围，根据起始时间和时间间隔类型返回特定格式的字符串。
     *
     * @param startTime 开始时间，用于确定格式化的时间点。
     * @param endTime   结束时间（未在当前逻辑中使用，但可为扩展预留）。
     * @param interval  时间间隔类型，枚举值，表示按天、周、月、季度或年格式化时间范围。
     *                  对应的枚举为 {@link DateIntervalEnum}。
     * @return String 格式化后的时间范围字符串。
     * - 按天（DAY）：返回格式为 "yyyy-MM-dd"。
     * - 按周（WEEK）：返回格式为 "yyyy-MM-dd(第 x 周)"。
     * - 按月（MONTH）：返回格式为 "yyyy-MM"。
     * - 按季度（QUARTER）：返回格式为 "yyyy-Qx"。
     * - 按年（YEAR）：返回格式为 "yyyy"。
     * <p>
     * 方法说明：
     * 1. 根据传入的 `interval` 参数，找到对应的时间间隔枚举。
     * 2. 使用 `switch` 表达式，根据时间间隔类型选择合适的格式化逻辑：
     * - DAY：格式化为标准日期格式。
     * - WEEK：格式化为标准日期格式，并附加周次信息。
     * - MONTH：格式化为标准月份格式。
     * - QUARTER：格式化为年份和季度信息。
     * - YEAR：格式化为年份。
     * 3. 如果 `interval` 不匹配有效的枚举值，会抛出异常。
     * <p>
     * 注意：
     * - 当前实现未使用 `endTime` 参数，但可以在未来扩展时用于更复杂的时间范围逻辑。
     * - 确保传入的 `interval` 有效，否则会触发断言异常。
     */
    public static String formatDateRange(LocalDateTime startTime, LocalDateTime endTime, Integer interval) {
        // 1. 获取时间间隔枚举
        DateIntervalEnum intervalEnum = DateIntervalEnum.valueOf(interval);
        Assert.notNull(intervalEnum, "interval({}} 找不到对应的枚举", interval);

        // 2. 根据间隔类型格式化时间范围
        return switch (intervalEnum) {
            case DAY -> LocalDateTimeUtil.format(startTime, DatePattern.NORM_DATE_PATTERN);
            case WEEK -> LocalDateTimeUtil.format(startTime, DatePattern.NORM_DATE_PATTERN)
                    + StrUtil.format("(第 {} 周)", LocalDateTimeUtil.weekOfYear(startTime));
            case MONTH -> LocalDateTimeUtil.format(startTime, DatePattern.NORM_MONTH_PATTERN);
            case QUARTER -> StrUtil.format("{}-Q{}", startTime.getYear(), getQuarterOfYear(startTime));
            case YEAR -> LocalDateTimeUtil.format(startTime, DatePattern.NORM_YEAR_PATTERN);
        };
    }


}
