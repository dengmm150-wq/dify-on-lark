package top.duhongming.utils;

import org.junit.jupiter.api.Test;

class TimeRangeSecondUtilsTest {
    @Test
    void test() {
        System.out.println("昨日开始：" + TimeRangeSecondUtils.getYesterdayStartTime());
        System.out.println("昨日结束：" + TimeRangeSecondUtils.getYesterdayEndTime());
        System.out.println("上周开始：" + TimeRangeSecondUtils.getLastWeekStartTime());
        System.out.println("上周结束：" + TimeRangeSecondUtils.getLastWeekEndTime());
        System.out.println("上月开始：" + TimeRangeSecondUtils.getLastMonthStartTime());
        System.out.println("上月结束：" + TimeRangeSecondUtils.getLastMonthEndTime());
        System.out.println("去年开始：" + TimeRangeSecondUtils.getLastYearStartTime());
        System.out.println("去年结束：" + TimeRangeSecondUtils.getLastYearEndTime());
    }

    @Test
    void getYesterdayStartTime() {
    }

    @Test
    void getYesterdayEndTime() {
    }

    @Test
    void getLastWeekStartTime() {
    }

    @Test
    void getLastWeekEndTime() {
    }

    @Test
    void getLastMonthStartTime() {
    }

    @Test
    void getLastMonthEndTime() {
    }

    @Test
    void getLastYearStartTime() {
    }

    @Test
    void getLastYearEndTime() {
    }
}