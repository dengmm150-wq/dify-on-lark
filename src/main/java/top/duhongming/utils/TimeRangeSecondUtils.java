package top.duhongming.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

/**
 * 时间范围工具类（秒级时间戳）：获取相对于当前日期的各种时间范围的开始和结束时间戳
 */
public class TimeRangeSecondUtils {

    // 私有构造函数，防止实例化
    private TimeRangeSecondUtils() {
        throw new AssertionError("工具类不允许实例化");
    }

    /**
     * 获取昨天的开始时间戳（秒）
     * 例如：当前日期为2025-11-11，则返回2025-11-10 00:00:00的时间戳
     */
    public static long getYesterdayStartTime() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return localDateToStartSecondTimestamp(yesterday);
    }

    /**
     * 获取昨天的结束时间戳（秒）
     * 例如：当前日期为2025-11-11，则返回2025-11-10 23:59:59的时间戳
     */
    public static long getYesterdayEndTime() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return localDateToEndSecondTimestamp(yesterday);
    }

    /**
     * 获取上周的开始时间戳（秒）
     * 以周一为一周的第一天，例如：当前日期为2025-11-11（周二），则返回2025-11-03 00:00:00的时间戳
     */
    public static long getLastWeekStartTime() {
        LocalDate lastWeekMonday = LocalDate.now()
                .minusWeeks(1)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return localDateToStartSecondTimestamp(lastWeekMonday);
    }

    /**
     * 获取上周的结束时间戳（秒）
     * 以周日为一周的最后一天，例如：当前日期为2025-11-11（周二），则返回2025-11-09 23:59:59的时间戳
     */
    public static long getLastWeekEndTime() {
        LocalDate lastWeekSunday = LocalDate.now()
                .minusWeeks(1)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return localDateToEndSecondTimestamp(lastWeekSunday);
    }

    /**
     * 获取上月的开始时间戳（秒）
     * 例如：当前日期为2025-11-11，则返回2025-10-01 00:00:00的时间戳
     */
    public static long getLastMonthStartTime() {
        LocalDate lastMonthFirstDay = LocalDate.now()
                .minusMonths(1)
                .with(TemporalAdjusters.firstDayOfMonth());
        return localDateToStartSecondTimestamp(lastMonthFirstDay);
    }

    /**
     * 获取上月的结束时间戳（秒）
     * 例如：当前日期为2025-11-11，则返回2025-10-31 23:59:59的时间戳
     */
    public static long getLastMonthEndTime() {
        LocalDate lastMonthLastDay = LocalDate.now()
                .minusMonths(1)
                .with(TemporalAdjusters.lastDayOfMonth());
        return localDateToEndSecondTimestamp(lastMonthLastDay);
    }

    /**
     * 获取去年的开始时间戳（秒）
     * 例如：当前日期为2025-11-11，则返回2024-01-01 00:00:00的时间戳
     */
    public static long getLastYearStartTime() {
        LocalDate lastYearFirstDay = LocalDate.now()
                .minusYears(1)
                .with(TemporalAdjusters.firstDayOfYear());
        return localDateToStartSecondTimestamp(lastYearFirstDay);
    }

    /**
     * 获取去年的结束时间戳（秒）
     * 例如：当前日期为2025-11-11，则返回2024-12-31 23:59:59的时间戳
     */
    public static long getLastYearEndTime() {
        LocalDate lastYearLastDay = LocalDate.now()
                .minusYears(1)
                .with(TemporalAdjusters.lastDayOfYear());
        return localDateToEndSecondTimestamp(lastYearLastDay);
    }

    /**
     * 将LocalDate转换为当天开始时间戳（00:00:00，秒级）
     *
     * @param date 日期
     * @return 时间戳（秒）
     */
    private static long localDateToStartSecondTimestamp(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .getEpochSecond(); // 直接获取秒级时间戳
    }

    /**
     * 将LocalDate转换为当天结束时间戳（23:59:59，秒级）
     *
     * @param date 日期
     * @return 时间戳（秒）
     */
    private static long localDateToEndSecondTimestamp(LocalDate date) {
        return date.atTime(23, 59, 59) // 精确到秒的结束时间
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .getEpochSecond();
    }
}