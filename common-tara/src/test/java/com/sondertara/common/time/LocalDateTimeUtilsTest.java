package com.sondertara.common.time;

import com.sondertara.common.util.LocalDateTimeUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间工具单元测试<br>
 * 此单元测试依赖时区为中国+08:00
 *
 * <pre>
 * export TZ=Asia/Shanghai
 * </pre>
 *
 * @author huangxiaohu
 */
public class LocalDateTimeUtilsTest {

    @Test
    public void thisDayOfMonth() {
        LocalDateTime localDateTime = LocalDateTimeUtils.parseLocalDateTime(LocalDateTimeUtils.parseDate("2022-01-03"));
        localDateTime.toLocalDate().minusDays(1);

        WeekFields weekFields=WeekFields.of(Locale.getDefault());
        int i1 = localDateTime.get(weekFields.weekOfYear());
        int i = localDateTime.get(weekFields.weekOfWeekBasedYear());


        System.out.println(i1);
        System.out.println(i);
    }

    @Test
    public void beginAndEndTest() {
        final String dateStr = "2017-03-01 00:33:23";
        // 一天的开始
        LocalDateTime localDateTime = LocalDateTimeUtils.parseLocalDateTime(dateStr);
        LocalDate localDate = localDateTime.toLocalDate();
        Assertions.assertEquals("2017-03-01 00:00:00", LocalDateTimeUtils.getDayStart(localDate));
        // 一天的结束
        final String endOfDay = LocalDateTimeUtils.getDayEnd(localDate);
        Assertions.assertEquals("2017-03-01 23:59:59", endOfDay);
    }


    @Test
    public void parseTest6() {
        final String str = "Tue Jun 4 16:25:15 +0800 2019";
        final Date dateTime = LocalDateTimeUtils.parseDate(str);
        assert dateTime != null;
        Assertions.assertEquals("2019-06-04 16:25:15", LocalDateTimeUtils.format(dateTime));
    }

    @Test
    public void parseTest7() {
        String str = "2019-06-01T19:45:43.000 +0800";
        Date dateTime = LocalDateTimeUtils.parseDate(str);
        assert dateTime != null;
        Assertions.assertEquals("2019-06-01 19:45:43", LocalDateTimeUtils.format(dateTime));

        str = "2019-06-01T19:45:43 +08:00";
        dateTime = LocalDateTimeUtils.parseDate(str);
        assert dateTime != null;
        Assertions.assertEquals("2019-06-01 19:45:43", LocalDateTimeUtils.format(dateTime));
    }

    @Test
    public void parseTest8() {
        final String str = "2020-06-28T02:14:13.000Z";
        final Date dateTime = LocalDateTimeUtils.parseDate(str);
        assert dateTime != null;
        Assertions.assertEquals("2020-06-28 02:14:13", LocalDateTimeUtils.format(dateTime));
    }

    /**
     * 测试支持：yyyy-MM-dd HH:mm:ss.SSSSSS 格式
     */
    @Test
    public void parseNormFullTest() {
        String str = "2020-02-06 01:58:00.000020";
        Date dateTime = LocalDateTimeUtils.parseDate(str);
        Assertions.assertNotNull(dateTime);
        Assertions.assertEquals("2020-02-06 01:58:00.000", LocalDateTimeUtils.format(dateTime, DatePattern.NORM_DATETIME_MS_PATTERN));

        str = "2020-02-06 01:58:00.00002";
        dateTime = LocalDateTimeUtils.parseDate(str);
        Assertions.assertNotNull(dateTime);
        Assertions.assertEquals("2020-02-06 01:58:00.000", LocalDateTimeUtils.format(dateTime, DatePattern.NORM_DATETIME_MS_PATTERN));

        str = "2020-02-06 01:58:00.111000";
        dateTime = LocalDateTimeUtils.parseDate(str);
        Assertions.assertNotNull(dateTime);
        Assertions.assertEquals("2020-02-06 01:58:00.111", LocalDateTimeUtils.format(dateTime, DatePattern.NORM_DATETIME_MS_PATTERN));

        str = "2020-02-06 01:58:00.111";
        dateTime = LocalDateTimeUtils.parseDate(str);
        Assertions.assertNotNull(dateTime);
        Assertions.assertEquals("2020-02-06 01:58:00.111", LocalDateTimeUtils.format(dateTime, DatePattern.NORM_DATETIME_MS_PATTERN));
    }


    @Test
    public void parseUTCOffsetTest() {
        // issue#I437AP@Gitee
        String str = "2019-06-01T19:45:43+08:00";
        Date dateTime = LocalDateTimeUtils.parseDate(str);
        assert dateTime != null;
        Assertions.assertEquals("2019-06-01 19:45:43", LocalDateTimeUtils.format(dateTime));

        str = "2019-06-01T19:45:43 +08:00";
        dateTime = LocalDateTimeUtils.parseDate(str);
        assert dateTime != null;
        Assertions.assertEquals("2019-06-01 19:45:43", LocalDateTimeUtils.format(dateTime));
    }

    @Test
    public void parseAndOffsetTest() {
        // 检查UTC时间偏移是否准确
        final String str = "2019-09-17T13:26:17.948Z";
        final Date dateTime = LocalDateTimeUtils.parseDate(str);
        assert dateTime != null;
        Assertions.assertEquals("2019-09-17 13:26:17", LocalDateTimeUtils.format(dateTime));
    }

    @Test
    public void parseDateTest() {
        final String dateStr = "2018-4-10";
        final Date date = LocalDateTimeUtils.parseDate(dateStr);
        final String format = LocalDateTimeUtils.format(date, DatePattern.NORM_DATE_PATTERN);
        Assertions.assertEquals("2018-04-10", format);
    }

    @Test
    public void parseToDateTimeTest1() {
        final String dateStr1 = "2017-02-01";
        final String dateStr2 = "2017/02/01";
        final String dateStr3 = "2017.02.01";
        final String dateStr4 = "2017年02月01日";

        final Date dt1 = LocalDateTimeUtils.parseDate(dateStr1);
        final Date dt2 = LocalDateTimeUtils.parseDate(dateStr2);
        final Date dt3 = LocalDateTimeUtils.parseDate(dateStr3);
        final Date dt4 = LocalDateTimeUtils.parseDate(dateStr4);
        Assertions.assertEquals(dt1, dt2);
        Assertions.assertEquals(dt2, dt3);
        Assertions.assertEquals(dt3, dt4);
    }

    @Test
    public void parseToDateTimeTest2() {
        final String dateStr1 = "2017-02-01 12:23";
        final String dateStr2 = "2017/02/01 12:23";
        final String dateStr3 = "2017.02.01 12:23";
        final String dateStr4 = "2017年02月01日 12:23";

        final Date dt1 = LocalDateTimeUtils.parseDate(dateStr1);
        final Date dt2 = LocalDateTimeUtils.parseDate(dateStr2);
        final Date dt3 = LocalDateTimeUtils.parseDate(dateStr3);
        final Date dt4 = LocalDateTimeUtils.parseDate(dateStr4);
        Assertions.assertEquals(dt1, dt2);
        Assertions.assertEquals(dt2, dt3);
        Assertions.assertEquals(dt3, dt4);
    }

    @Test
    public void parseToDateTimeTest3() {
        final String dateStr1 = "2017-02-01 12:23:45";
        final String dateStr2 = "2017/02/01 12:23:45";
        final String dateStr3 = "2017.02.01 12:23:45";
        final String dateStr4 = "2017年02月01日 12时23分45秒";

        final Date dt1 = LocalDateTimeUtils.parseDate(dateStr1);
        final Date dt2 = LocalDateTimeUtils.parseDate(dateStr2);
        final Date dt3 = LocalDateTimeUtils.parseDate(dateStr3);
        final Date dt4 = LocalDateTimeUtils.parseDate(dateStr4);
        Assertions.assertEquals(dt1, dt2);
        Assertions.assertEquals(dt2, dt3);
        Assertions.assertEquals(dt3, dt4);
    }


    @Test
    public void parseUTCTest() {
        String dateStr1 = "2018-09-13T05:34:31Z";
        Date dt = LocalDateTimeUtils.parseDate(dateStr1);

        // parse方法支持UTC格式测试
        final Date dt2 = LocalDateTimeUtils.parseDate(dateStr1);
        Assertions.assertEquals(dt, dt2);

        // 默认使用Pattern对应的时区，即UTC时区
        String dateStr = dt.toString();
        Assertions.assertEquals("2018-09-13 05:34:31", dateStr);
        //
        //// 使用当前（上海）时区
        //dateStr = dt.toString(TimeZone.getTimeZone("GMT+8:00"));
        //Assertions.assertEquals("2018-09-13 13:34:31", dateStr);
        //
        //dateStr1 = "2018-09-13T13:34:32+0800";
        //dt = DateUtil.parseUTC(dateStr1);
        //dateStr = dt.toString(TimeZone.getTimeZone("GMT+8:00"));
        //Assertions.assertEquals("2018-09-13 13:34:32", dateStr);
        //
        //dateStr1 = "2018-09-13T13:34:33+08:00";
        //dt = DateUtil.parseUTC(dateStr1);
        //dateStr = dt.toString(TimeZone.getTimeZone("GMT+8:00"));
        //Assertions.assertEquals("2018-09-13 13:34:33", dateStr);
        //
        //dateStr1 = "2018-09-13T13:34:34+0800";
        //dt = DateUtil.parse(dateStr1);
        //assert dt != null;
        //dateStr = dt.toString(TimeZone.getTimeZone("GMT+8:00"));
        //Assertions.assertEquals("2018-09-13 13:34:34", dateStr);
        //
        //dateStr1 = "2018-09-13T13:34:35+08:00";
        //dt = DateUtil.parse(dateStr1);
        //assert dt != null;
        //dateStr = dt.toString(TimeZone.getTimeZone("GMT+8:00"));
        //Assertions.assertEquals("2018-09-13 13:34:35", dateStr);
        //
        //dateStr1 = "2018-09-13T13:34:36.999+0800";
        //dt = DateUtil.parseUTC(dateStr1);
        //final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.NORM_DATETIME_MS_PATTERN);
        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        //dateStr = dt.toString(simpleDateFormat);
        //Assertions.assertEquals("2018-09-13 13:34:36.999", dateStr);
        //
        //dateStr1 = "2018-09-13T13:34:37.999+08:00";
        //dt = DateUtil.parseUTC(dateStr1);
        //dateStr = dt.toString(simpleDateFormat);
        //Assertions.assertEquals("2018-09-13 13:34:37.999", dateStr);
        //
        //dateStr1 = "2018-09-13T13:34:38.999+0800";
        //dt = DateUtil.parse(dateStr1);
        //assert dt != null;
        //dateStr = dt.toString(simpleDateFormat);
        //Assertions.assertEquals("2018-09-13 13:34:38.999", dateStr);
        //
        //dateStr1 = "2018-09-13T13:34:39.999+08:00";
        //dt = DateUtil.parse(dateStr1);
        //assert dt != null;
        //dateStr = dt.toString(simpleDateFormat);
        //Assertions.assertEquals("2018-09-13 13:34:39.999", dateStr);
        //
        //// 使用UTC时区
        //dateStr1 = "2018-09-13T13:34:39.99";
        //dt = DateUtil.parse(dateStr1);
        //assert dt != null;
        //dateStr = dt.toString();
        //Assertions.assertEquals("2018-09-13 13:34:39", dateStr);
    }

    @Test
    public void parseUTCTest2() {
        // issue1503@Github
        // 检查不同毫秒长度都可以正常匹配
        String utcTime = "2021-03-30T12:56:51.3Z";
        Date parse = LocalDateTimeUtils.parseDate(utcTime);
        Assertions.assertEquals("2021-03-30 12:56:51", LocalDateTimeUtils.format(parse));

        utcTime = "2021-03-30T12:56:51.34Z";
        parse = LocalDateTimeUtils.parseDate(utcTime);
        Assertions.assertEquals("2021-03-30 12:56:51", LocalDateTimeUtils.format(parse));

        utcTime = "2021-03-30T12:56:51.345Z";
        parse = LocalDateTimeUtils.parseDate(utcTime);
        Assertions.assertEquals("2021-03-30 12:56:51", LocalDateTimeUtils.format(parse));

    }

    //
    //@Test
    //public void parseCSTTest() {
    //	final String dateStr = "Wed Sep 16 11:26:23 CST 2009";
    //
    //	final SimpleDateFormat sdf = new SimpleDateFormat(DatePattern.JDK_DATETIME_PATTERN, Locale.US);
    //	// Asia/Shanghai是以地区命名的地区标准时，在中国叫CST，因此如果解析CST时不使用"Asia/Shanghai"而使用"GMT+08:00"，会导致相差一个小时
    //	sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    //	final DateTime parse = DateUtil.parse(dateStr, sdf);
    //
    //	DateTime dateTime = DateUtil.parseCST(dateStr);
    //	Assertions.assertEquals(parse, dateTime);
    //
    //	dateTime = DateUtil.parse(dateStr);
    //	Assertions.assertEquals(parse, dateTime);
    //}
    //
    @Test
    public void parseCSTTest2() {
        final String dateStr = "Wed Sep 16 11:26:23 CST 2009";

        final SimpleDateFormat sdf = new SimpleDateFormat(DatePattern.JDK_DATETIME_PATTERN, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
        final DateTime parse = LocalDateTimeUtils.parse(dateStr, sdf);

        final FastDateFormat fdf = FastDateFormat.getInstance(DatePattern.JDK_DATETIME_PATTERN, TimeZone.getTimeZone("America/Chicago"), Locale.US);
        final DateTime parse2 = LocalDateTimeUtils.parse(dateStr, fdf);

        Assertions.assertEquals(parse, parse2);
    }
    //
    //@Test
    //public void parseJDkTest() {
    //	final String dateStr = "Thu May 16 17:57:18 GMT+08:00 2019";
    //	final DateTime time = DateUtil.parse(dateStr);
    //	Assertions.assertEquals("2019-05-16 17:57:18", Objects.requireNonNull(time).toString());
    //}
    //
    //@Test
    //public void parseISOTest() {
    //	final String dateStr = "2020-04-23T02:31:00.000Z";
    //	final DateTime time = DateUtil.parse(dateStr);
    //	Assertions.assertEquals("2020-04-23 02:31:00", Objects.requireNonNull(time).toString());
    //}


}
