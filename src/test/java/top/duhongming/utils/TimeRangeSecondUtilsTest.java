package top.duhongming.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeRangeSecondUtilsTest {

    @Test
    void testGetYesterdayStartTime() {
        long yesterdayStart = TimeRangeSecondUtils.getYesterdayStartTime();
        assertTrue(yesterdayStart > 0, "昨天开始时间戳应该大于0");
        
        // 验证时间戳是合理的（应该在最近的时间范围内）
        long currentTimestamp = System.currentTimeMillis() / 1000;
        assertTrue(yesterdayStart < currentTimestamp, "昨天开始时间应该小于当前时间");
        
        // 验证开始时间应该小于结束时间
        long yesterdayEnd = TimeRangeSecondUtils.getYesterdayEndTime();
        assertTrue(yesterdayStart < yesterdayEnd, "昨天开始时间应该小于结束时间");
        
        // 验证时间差应该在一天的范围内（86400秒）
        long diff = yesterdayEnd - yesterdayStart;
        assertTrue(diff >= 86399 && diff <= 86400, "昨天的时间范围应该在一天左右");
    }

    @Test
    void testGetYesterdayEndTime() {
        long yesterdayEnd = TimeRangeSecondUtils.getYesterdayEndTime();
        assertTrue(yesterdayEnd > 0, "昨天结束时间戳应该大于0");
        
        long currentTimestamp = System.currentTimeMillis() / 1000;
        assertTrue(yesterdayEnd < currentTimestamp, "昨天结束时间应该小于当前时间");
    }

    @Test
    void testGetLastWeekStartTime() {
        long lastWeekStart = TimeRangeSecondUtils.getLastWeekStartTime();
        assertTrue(lastWeekStart > 0, "上周开始时间戳应该大于0");
        
        long currentTimestamp = System.currentTimeMillis() / 1000;
        assertTrue(lastWeekStart < currentTimestamp, "上周开始时间应该小于当前时间");
        
        long lastWeekEnd = TimeRangeSecondUtils.getLastWeekEndTime();
        assertTrue(lastWeekStart < lastWeekEnd, "上周开始时间应该小于结束时间");
        
        // 验证时间差应该在一周左右（7天 = 604800秒）
        long diff = lastWeekEnd - lastWeekStart;
        assertTrue(diff >= 604799 && diff <= 604801, "上周的时间范围应该在一周左右");
    }

    @Test
    void testGetLastWeekEndTime() {
        long lastWeekEnd = TimeRangeSecondUtils.getLastWeekEndTime();
        assertTrue(lastWeekEnd > 0, "上周结束时间戳应该大于0");
        
        long currentTimestamp = System.currentTimeMillis() / 1000;
        assertTrue(lastWeekEnd < currentTimestamp, "上周结束时间应该小于当前时间");
    }

    @Test
    void testGetLastMonthStartTime() {
        long lastMonthStart = TimeRangeSecondUtils.getLastMonthStartTime();
        assertTrue(lastMonthStart > 0, "上月开始时间戳应该大于0");
        
        long currentTimestamp = System.currentTimeMillis() / 1000;
        assertTrue(lastMonthStart < currentTimestamp, "上月开始时间应该小于当前时间");
        
        long lastMonthEnd = TimeRangeSecondUtils.getLastMonthEndTime();
        assertTrue(lastMonthStart < lastMonthEnd, "上月开始时间应该小于结束时间");
        
        // 验证时间差应该在28-31天之间（取决于月份）
        long diff = lastMonthEnd - lastMonthStart;
        assertTrue(diff >= 28 * 86400 && diff <= 31 * 86400, "上月的时间范围应该在28-31天之间");
    }

    @Test
    void testGetLastMonthEndTime() {
        long lastMonthEnd = TimeRangeSecondUtils.getLastMonthEndTime();
        assertTrue(lastMonthEnd > 0, "上月结束时间戳应该大于0");
        
        long currentTimestamp = System.currentTimeMillis() / 1000;
        assertTrue(lastMonthEnd < currentTimestamp, "上月结束时间应该小于当前时间");
    }



    @Test
    void testGetLastYearEndTime() {
        long lastYearEnd = TimeRangeSecondUtils.getLastYearEndTime();
        assertTrue(lastYearEnd > 0, "去年结束时间戳应该大于0");
        
        long currentTimestamp = System.currentTimeMillis() / 1000;
        assertTrue(lastYearEnd < currentTimestamp, "去年结束时间应该小于当前时间");
    }
}