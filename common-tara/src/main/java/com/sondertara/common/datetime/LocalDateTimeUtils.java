package com.sondertara.common.datetime;

import com.sondertara.common.base.Assert;
import com.sondertara.common.text.CharUtils;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.comparator.CompareUtils;
import com.sondertara.common.concurrent.ConcurrentReferenceHashMap;
import com.sondertara.common.convert.ConvertUtils;
import com.sondertara.common.datetime.format.DateParser;
import com.sondertara.common.datetime.format.FastDateFormat;
import com.sondertara.common.exception.ExceptionUtils;
import com.sondertara.common.exception.TaraException;
import com.sondertara.common.math.NumberUtils;
import com.sondertara.common.random.RandomUtils;
import com.sondertara.common.reflect.reference.ReferenceType;
import com.sondertara.common.regex.PatternPool;
import com.sondertara.common.regex.RegexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * https://www.iso.org/obp/ui#iso:std:iso:8601:-1:ed-1:v1:en
 * <p>
 * <p>
 * 标准时间：GMT时间，也叫格林威治平时，也叫 UTC时间。
 * Java 6 ：
 * <p>
 * <pre>
 * Date:
 * 表示一个瞬时值，单位是毫秒。它的结果是一个标准的GMT瞬时值。它和时区（timezone）地域（Locale）没有关系。
 * 在同一时刻，地球上两个不同时区国家的人使用Date API获取到的毫秒数完全一样的。
 *
 * Timezone:
 * 表示时区，其实是各个时区与GMT的毫秒数之差, 也即Offset。
 * 因为不同时区的时间看到的是不一样的，但是通过Date获取到的毫秒数是一样的，怎么做到的呢？
 * Date#toString 或者 SimpleDateFormat 都会根据Timezone 和 Locale 来进行处理，具体处理如下：
 *    1） 使用GMT 毫秒数 + Timezone.offset 计算出当地的实际毫秒数
 *    2） 格式化显示时会使用本土语言（Locale）进行处理
 *
 * Calender：
 *  日历，它综合了 GMT millis（Date）, timezone, locale，也就是说它是用来表示某个时区的某个国家的时间。
 *  并在此基础上提供了时间的加减运算。
 *  setTimeInMillis，setTimeInMillis 这两个方法的参数或者返回值是 UTC millis。
 *  提供的时间的加减运算都是针对 Locale Time的。
 *
 * SimpleDateFormat 又是基于Calender（即基于 GMT millis（Date）, timezone, locale）的一个日期格式化工具
 * Pattern 涉及的符号：https://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
 * </pre>
 */
public class LocalDateTimeUtils extends CalendarUtils {

    private static final Logger log = LoggerFactory.getLogger(LocalDateTimeUtils.class);

    public static final String DATE_TIME_FORMATTER = DatePattern.NORM_DATETIME_PATTERN;
    public static final String DATE_TIME_MILLS_FORMATTER = DatePattern.NORM_DATETIME_MS_PATTERN;
    public static final String DATE_FORMATTER = DatePattern.NORM_DATE_PATTERN;

    /**
     * java.util.Date EEE MMM zzz 缩写数组
     */
    private final static String[] wtb = {"sun", "mon", "tue", "wed", "thu", "fri", "sat", "jan", "feb", "mar", "apr",
            "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec", "gmt", "ut", "utc", "est", "edt", "cst", "cdt",
            "mst", "mdt", "pst", "pdt"};

    /**
     * 常用的UTC时间格式
     */
    private final static String[] FREQUENTLY_USED_UTC_WITH_Z_DATE_FORMATS = new String[]{"yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"};

    /**
     * 常用纯数字时间格式
     */
    private final static String[] FREQUENTLY_USED_NUMBER_DATE_FORMATS = new String[]{"yyyyMMddHHmmss",
            "yyyyMMddHHmmssSSS", "yyyyMMdd", "yyyyMMss", "HHmmss"};

    /**
     * CST格式
     */
    private final static String[] FREQUENTLY_USED_CST_DATE_FORMATS = new String[]{"EEE, dd MMM yyyy HH:mm:ss z",
            "EEE MMM dd HH:mm:ss zzz yyyy"};

    /**
     * 常用的时间格式
     */
    private final static String[] FREQUENTLY_USED_DATE_FORMATS = new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss", "yyyy年MM月dd日 HH时mm分ss秒", "yyyy/MM/dd",
            "yyyy.MM.dd", "HH时mm分ss秒", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss.SSS"};

    /**
     * 当前时间，转换为{@link Date}对象
     *
     * @return 当前时间
     */
    public static Date date() {
        return new Date();
    }


    /**
     * {@link Date}类型时间转为{@link Date}<br>
     * 如果date本身为DateTime对象，则返回强转后的对象，否则新建一个DateTime对象
     *
     * @param date Long类型Date（Unix时间戳）
     * @return 时间对象
     *      */
    public static Date date(Date date) {
        return dateNew(date);
    }

    /**
     * 根据已有{@link Date} 产生新的{@link Date}对象
     *
     * @param date Date对象
     * @return {@link Date}对象
     *      */
    public static Date dateNew(Date date) {
        return Date.from(date.toInstant());
    }

    /**
     * Long类型时间转为{@link Date}<br>
     * 只支持毫秒级别时间戳，如果需要秒级别时间戳，请自行×1000
     *
     * @param date Long类型Date（Unix时间戳）
     * @return 时间对象
     */
    public static Date date(long date) {
        return new Date(date);
    }

    /**
     * {@link Calendar}类型时间转为{@link Date}<br>
     * 始终根据已有{@link Calendar} 产生新的{@link Date}对象
     *
     * @param calendar {@link Calendar}
     * @return 时间对象
     */
    public static Date date(Calendar calendar) {
        return calendar.getTime();
    }

    /**
     * {@link TemporalAccessor}类型时间转为{@link Date}<br>
     * 始终根据已有{@link TemporalAccessor} 产生新的{@link Date}对象
     *
     * @param temporalAccessor {@link TemporalAccessor},常用子类： {@link LocalDateTime} {@link LocalDate}
     * @return 时间对象
     *      */
    public static Date date(TemporalAccessor temporalAccessor) {
        return date(LocalDateTime.from(temporalAccessor));
    }

    /**
     * 获得指定日期所属季度，从1开始计数
     *
     * @param date 日期
     * @return 第几个季度
     *      */
    public static int quarter(Date date) {
        return quarterEnum(date).getValue();
    }

    /**
     * 获得指定日期所属季度
     *
     * @param date 日期
     * @return 第几个季度枚举
     *      */
    public static Quarter quarterEnum(Date date) {
        return Quarter.of(calendar(date).get(Calendar.MONTH) + 1);
    }

    /**
     * 获得指定日期是所在年份的第几周<br>
     * 此方法返回值与一周的第一天有关，比如：<br>
     * 2016年1月3日为周日，如果一周的第一天为周日，那这天是第二周（返回2）<br>
     * 如果一周的第一天为周一，那这天是第一周（返回1）<br>
     * 跨年的那个星期得到的结果总是1
     *
     * @param date 日期
     * @return 周
     */
    public static int weekOfYear(Date date) {
        LocalDateTime localDateTime = parseLocalDateTime(date);
        TemporalField temporalField = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return localDateTime.get(temporalField);
    }

    /**
     * 获得指定日期是所在月份的第几周<br>
     *
     * @param date 日期
     * @return 周
     */
    public static int weekOfMonth(Date date) {
        LocalDateTime localDateTime = parseLocalDateTime(date);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        TemporalField temporalField = weekFields.weekOfMonth();
        return localDateTime.get(temporalField);
    }

    public static LocalDateTime parseLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static LocalDate parseLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 是否为周末（周六或周日）
     *
     * @param date 判定的日期{@link Date}
     * @return 是否为周末（周六或周日）
     *      */
    public static boolean isWeekend(Date date) {
        LocalDateTime localDateTime = parseLocalDateTime(date);
        return localDateTime.query(temporal -> temporal.get(ChronoField.DAY_OF_WEEK) > 5);
    }

    /**
     * @return 当前日期所在年份的第几周
     */
    public static int thisWeekOfYear() {
        return weekOfYear(date());
    }

    /**
     * @return 当前日期所在月份的第几周
     */
    public static int thisWeekOfMonth() {
        return weekOfMonth(date());
    }

    // -------------------------------------------------------------- Part of Date
    // end

    /**
     * 获得指定日期年份和季节<br>
     * 格式：[20131]表示2013年的第一季度
     *
     * @param date 日期
     * @return Quarter ，类似于 20132
     */
    public static String yearAndQuarter(Date date) {
        return yearAndQuarter(calendar(date));
    }

    /**
     * 获得指定日期区间内的年份和季节<br>
     *
     * @param startDate 起始日期（包含）
     * @param endDate   结束日期（包含）
     * @return 季度列表 ，元素类似于 20132
     */
    public static LinkedHashSet<String> yearAndQuarter(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return new LinkedHashSet<>(0);
        }
        return yearAndQuarter(startDate.getTime(), endDate.getTime());
    }
    // ------------------------------------ Format start
    // ----------------------------------------------


    /**
     * 根据特定格式格式化日期
     *
     * @param date   被格式化的日期
     * @param format {@link SimpleDateFormat}
     * @return 格式化后的字符串
     */
    public static String format(Date date, DateFormat format) {
        if (null == format || null == date) {
            return null;
        }
        return format.format(date);
    }

    /**
     * 根据特定格式格式化日期
     *
     * @param date   被格式化的日期
     * @param format {@link SimpleDateFormat}
     *               {@link DatePattern#NORM_DATETIME_FORMATTER}
     * @return 格式化后的字符串
     *      */
    public static String format(Date date, DateTimeFormatter format) {
        if (null == format || null == date) {
            return null;
        }
        // java.time.temporal.UnsupportedTemporalTypeException: Unsupported field:
        // YearOfEra
        // 出现以上报错时，表示Instant时间戳没有时区信息，赋予默认时区
        return TemporalAccessorUtils.format(date.toInstant(), format);
    }


    /**
     * 格式化为Http的标准日期格式<br>
     * 标准日期格式遵循RFC 1123规范，格式类似于：Fri, 31 Dec 1999 23:59:59 GMT
     *
     * @param date 被格式化的日期
     * @return HTTP标准形式日期字符串
     */
    public static String formatHttpDate(Date date) {
        if (null == date) {
            return null;
        }
        return DatePattern.HTTP_DATETIME_FORMAT.format(date);
    }

    // ------------------------------------ Format end
    // ----------------------------------------------

    // ------------------------------------ Parse start
    // ----------------------------------------------

    /**
     * 构建DateTime对象
     *
     * @param dateStr    Date字符串
     * @param dateFormat 格式化器 {@link SimpleDateFormat}
     * @return DateTime对象
     */
    public static Date parse(CharSequence dateStr, DateParser dateFormat) {
        try {
            return dateFormat.parse(dateStr.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 构建DateTime对象
     *
     * @param dateStr    Date字符串
     * @param dateFormat 格式化器 {@link SimpleDateFormat}
     * @return DateTime对象
     */
    public static Date parse(CharSequence dateStr, DateFormat dateFormat) {
        try {
            return dateFormat.parse(dateStr.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * 将特定格式的日期转换为Date对象
     *
     * @param dateStr 特定格式的日期
     * @param format  格式，例如yyyy-MM-dd
     * @param locale  区域信息
     * @return 日期对象
     *      */
    public static Date parse(CharSequence dateStr, String format, Locale locale) {
        try {
            return newSimpleFormat(format, locale, null).parse(dateStr.toString());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 通过给定的日期格式解析日期时间字符串。<br>
     *
     * @param str           日期时间字符串，非空
     * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
     * @return 解析后的Date
     * @throws IllegalArgumentException if the date string or pattern array is null
     *      */
    public static Date parse(String str, String... parsePatterns) {
        return CalendarUtils.parseByPatterns(str, parsePatterns).getTime();
    }

    /**
     * 解析时间，格式HH:mm 或 HH:mm:ss，日期默认为今天
     *
     * @param timeString 标准形式的日期字符串
     * @return 日期对象
     *      */
    public static Date parseTimeToday(String timeString) {
        timeString = StringUtils.format("{} {}", today(), timeString);
        if (1 == StringUtils.count(timeString, ':')) {
            // 时间格式为 HH:mm
            return parse(timeString, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
        } else {
            // 时间格式为 HH:mm:ss
            return parse(timeString, DatePattern.NORM_DATETIME_FORMAT);
        }
    }

    /**
     * 解析UTC时间，格式：<br>
     * <ol>
     * <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ssZ</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss+0800</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss+08:00</li>
     * </ol>
     *
     * @param utcString UTC时间
     * @return 日期对象
     *      */
    public static Date parseUTC(String utcString) {
        if (utcString == null) {
            return null;
        }
        int length = utcString.length();
        if (StringUtils.contains(utcString, 'Z')) {
            if (length == DatePattern.UTC_PATTERN.length() - 4) {
                // 格式类似：2018-09-13T05:34:31Z，-4表示减去4个单引号的长度
                return parse(utcString, DatePattern.UTC_FORMAT);
            }

            final int patternLength = DatePattern.UTC_MS_PATTERN.length();
            // 格式类似：2018-09-13T05:34:31.999Z，-4表示减去4个单引号的长度
            // -4 ~ -6的范围表示匹配毫秒1~3位的情况
            if (length <= patternLength - 4 && length >= patternLength - 6) {
                return parse(utcString, DatePattern.UTC_MS_FORMAT);
            }
        } else if (StringUtils.contains(utcString, '+')) {
            // 去除类似2019-06-01T19:45:43 +08:00加号前的空格
            utcString = utcString.replace(" +", "+");
            final String zoneOffset = StringUtils.subAfter(utcString, '+', true);
            if (StringUtils.isBlank(zoneOffset)) {
                throw ExceptionUtils.illegalArgumentException("Invalid format: [{}]", utcString);
            }
            if (!StringUtils.contains(zoneOffset, ':')) {
                // +0800转换为+08:00
                final String pre = StringUtils.subBefore(utcString, '+', true);
                utcString = pre + "+" + zoneOffset.substring(0, 2) + ":" + "00";
            }

            if (StringUtils.contains(utcString, CharUtils.DOT)) {
                // 带毫秒，格式类似：2018-09-13T05:34:31.999+08:00
                return parse(utcString, DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT);
            } else {
                // 格式类似：2018-09-13T05:34:31+08:00
                return parse(utcString, DatePattern.UTC_WITH_XXX_OFFSET_FORMAT);
            }
        } else {
            if (length == DatePattern.UTC_SIMPLE_PATTERN.length() - 2) {
                // 格式类似：2018-09-13T05:34:31
                return parse(utcString, DatePattern.UTC_SIMPLE_FORMAT);
            } else if (StringUtils.contains(utcString, CharUtils.DOT)) {
                // 可能为： 2021-03-17T06:31:33.99
                return parse(utcString, DatePattern.UTC_SIMPLE_MS_FORMAT);
            }
        }
        // 没有更多匹配的时间格式
        throw ExceptionUtils.illegalStateException("No format fit for date String [{}] !", utcString);
    }

    /**
     * 解析CST时间，格式：<br>
     * <ol>
     * <li>EEE MMM dd HH:mm:ss z yyyy（例如：Wed Aug 01 00:00:00 CST 2012）</li>
     * </ol>
     *
     * @param cstString UTC时间
     * @return 日期对象
     *      */
    public static Date parseCST(CharSequence cstString) {
        if (cstString == null) {
            return null;
        }

        return parse(cstString, DatePattern.JDK_DATETIME_FORMAT);
    }

    /**
     * 将日期字符串转换为{@link Date}对象，格式：<br>
     * <ol>
     * <li>yyyy-MM-dd HH:mm:ss</li>
     * <li>yyyy/MM/dd HH:mm:ss</li>
     * <li>yyyy.MM.dd HH:mm:ss</li>
     * <li>yyyy年MM月dd日 HH时mm分ss秒</li>
     * <li>yyyy-MM-dd</li>
     * <li>yyyy/MM/dd</li>
     * <li>yyyy.MM.dd</li>
     * <li>HH:mm:ss</li>
     * <li>HH时mm分ss秒</li>
     * <li>yyyy-MM-dd HH:mm</li>
     * <li>yyyy-MM-dd HH:mm:ss.SSS</li>
     * <li>yyyy-MM-dd HH:mm:ss.SSSSSS</li>
     * <li>yyyyMMddHHmmss</li>
     * <li>yyyyMMddHHmmssSSS</li>
     * <li>yyyyMMdd</li>
     * <li>EEE, dd MMM yyyy HH:mm:ss z</li>
     * <li>EEE MMM dd HH:mm:ss zzz yyyy</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ssZ</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ</li>
     * </ol>
     *
     * @param dateCharSequence 日期字符串
     * @return 日期
     */
    public static Date parse(CharSequence dateCharSequence) {
        if (StringUtils.isBlank(dateCharSequence)){
            return null;
        }
        String dateStr = dateCharSequence.toString();
        // 去掉两边空格并去掉中文日期中的“日”和“秒”，以规范长度
        dateStr = StringUtils.removeAll(dateStr.trim(), '日', '秒');
        int length = dateStr.length();

        if (NumberUtils.isCreatable(dateStr)) {
            // 纯数字形式
            if (length == DatePattern.PURE_DATETIME_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATETIME_FORMAT);
            } else if (length == DatePattern.PURE_DATETIME_MS_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATETIME_MS_FORMAT);
            } else if (length == DatePattern.PURE_DATE_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATE_FORMAT);
            } else if (length == DatePattern.PURE_TIME_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_TIME_FORMAT);
            }
        } else if (RegexUtils.isMatch(PatternPool.TIME, dateStr)) {
            // HH:mm:ss 或者 HH:mm 时间格式匹配单独解析
            return parseTimeToday(dateStr);
        } else if (StringUtils.containsAnyIgnoreCase(dateStr, wtb)) {
            // JDK的Date对象toString默认格式，类似于：
            // Tue Jun 4 16:25:15 +0800 2019
            // Thu May 16 17:57:18 GMT+08:00 2019
            // Wed Aug 01 00:00:00 CST 2012
            return parseCST(dateStr);
        } else if (StringUtils.contains(dateStr, 'T')) {
            // UTC时间
            return parseUTC(dateStr);
        }

        // 标准日期格式（包括单个数字的日期时间）
        dateStr = normalize(dateStr);
        if (RegexUtils.isMatch(DatePattern.REGEX_NORM, dateStr)) {
            final int colonCount = StringUtils.count(dateStr, CharUtils.COLON);
            switch (colonCount) {
                case 0:
                    // yyyy-MM-dd
                    return parse(dateStr, DatePattern.NORM_DATE_FORMAT);
                case 1:
                    // yyyy-MM-dd HH:mm
                    return parse(dateStr, DatePattern.NORM_DATETIME_MINUTE_FORMAT);
                case 2:
                    final int indexOfDot = StringUtils.indexOf(dateStr, CharUtils.DOT);
                    if (indexOfDot > 0) {
                        final int length1 = dateStr.length();
                        // yyyy-MM-dd HH:mm:ss.SSS 或者 yyyy-MM-dd HH:mm:ss.SSSSSS
                        if (length1 - indexOfDot > 4) {
                            // 类似yyyy-MM-dd HH:mm:ss.SSSSSS，采取截断操作
                            dateStr = StringUtils.subPre(dateStr, indexOfDot + 4);
                        }
                        return parse(dateStr, DatePattern.NORM_DATETIME_MS_FORMAT);
                    }
                    // yyyy-MM-dd HH:mm:ss
                    return parse(dateStr, DatePattern.NORM_DATETIME_FORMAT);
                default:
            }
        }

        // 没有更多匹配的时间格式
        throw ExceptionUtils.illegalStateException("No format fit for date String [{}] !", dateStr);
    }

    // ------------------------------------ Parse end
    // ----------------------------------------------

    // ------------------------------------ Offset start
    // ----------------------------------------------

    /**
     * 修改日期为某个时间字段起始时间
     *
     * @param date      {@link Date}
     * @param dateField 保留到的时间字段，如定义为 {@link DateField#SECOND}，表示这个字段不变，这个字段以下字段全部归0
     * @return {@link Date}
     *      */
    public static Date truncate(Date date, DateField dateField) {
        return truncate(calendar(date), dateField).getTime();
    }

    /**
     * 修改日期为某个时间字段四舍五入时间
     *
     * @param date      {@link Date}
     * @param dateField 时间字段
     * @return {@link Date}
     *      */
    public static Date round(Date date, DateField dateField) {
        return round(calendar(date), dateField).getTime();
    }

    /**
     * 修改日期为某个时间字段结束时间
     *
     * @param date      {@link Date}
     * @param dateField 保留到的时间字段，如定义为
     *                  {@link DateField#SECOND}，表示这个字段不变，这个字段以下字段全部取最大值
     * @return {@link Date}
     *      */
    public static Date ceiling(Date date, DateField dateField) {
        return ceiling(calendar(date), dateField).getTime();
    }

    /**
     * 修改日期为某个时间字段结束时间<br>
     * 可选是否归零毫秒。
     *
     * <p>
     * 有时候由于毫秒部分必须为0（如MySQL数据库中），因此在此加上选项。
     * </p>
     *
     * @param date                {@link Date}
     * @param dateField           时间字段
     * @param truncateMillisecond 是否毫秒归零
     * @return {@link Date}
     *      */
    public static Date ceiling(Date date, DateField dateField, boolean truncateMillisecond) {
        return ceiling(calendar(date), dateField, truncateMillisecond).getTime();
    }

    /**
     * 获取秒级别的开始时间，即毫秒部分设置为0
     *
     * @param date 日期
     * @return {@link Date}
     *      */
    public static Date beginOfSecond(Date date) {
        return beginOfSecond(calendar(date)).getTime();
    }

    /**
     * 获取秒级别的结束时间，即毫秒设置为999
     *
     * @param date 日期
     * @return {@link Date}
     *      */
    public static Date endOfSecond(Date date) {
        return endOfSecond(calendar(date)).getTime();
    }

    /**
     * 获取某小时的开始时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date beginOfHour(Date date) {
        return beginOfHour(calendar(date)).getTime();
    }

    /**
     * 获取某小时的结束时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date endOfHour(Date date) {
        return endOfHour(calendar(date)).getTime();
    }

    /**
     * 获取某分钟的开始时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date beginOfMinute(Date date) {
        return beginOfMinute(calendar(date)).getTime();
    }

    /**
     * 获取某分钟的结束时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date endOfMinute(Date date) {
        return endOfMinute(calendar(date)).getTime();
    }

    /**
     * 获取某天的开始时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date beginOfDay(Date date) {
        return beginOfDay(calendar(date)).getTime();
    }

    /**
     * 获取某一天零点
     *
     * @param dateStr 20120920
     * @return 20120920
     */
    public static String beginOfDay(String dateStr) {
        LocalDate localDate = null;
        try {
            localDate = parseLocalDate(dateStr);
        } catch (Exception e) {
            localDate = localDate(parseDate(dateStr));
        }

        return beginOfDay(localDate);
    }

    /**
     * 获取某一天零点
     *
     * @param localDate the local date
     * @return the
     */
    public static String beginOfDay(LocalDate localDate) {

        LocalDateTime dateTime = LocalDateTime.of(localDate, LocalTime.MIN);
        return format(dateTime,YMD_HMS);
    }

    /**
     * 获取某天的结束时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date endOfDay(Date date) {
        return endOfDay(calendar(date)).getTime();
    }

    /**
     * 获取某周的开始时间，周一定为一周的开始时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date beginOfWeek(Date date) {
        return beginOfWeek(calendar(date)).getTime();
    }

    /**
     * 获取某周的开始时间
     *
     * @param date               日期
     * @param isMondayAsFirstDay 是否周一做为一周的第一天（false表示周日做为第一天）
     * @return {@link Date}
     *      */
    public static Date beginOfWeek(Date date, boolean isMondayAsFirstDay) {
        return beginOfWeek(calendar(date), isMondayAsFirstDay).getTime();
    }

    /**
     * 获取某周的结束时间，周日定为一周的结束
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date endOfWeek(Date date) {
        return endOfWeek(calendar(date)).getTime();
    }

    /**
     * 获取某周的结束时间
     *
     * @param date              日期
     * @param isSundayAsLastDay 是否周日做为一周的最后一天（false表示周六做为最后一天）
     * @return {@link Date}
     *      */
    public static Date endOfWeek(Date date, boolean isSundayAsLastDay) {
        return endOfWeek(calendar(date), isSundayAsLastDay).getTime();
    }

    /**
     * 获取某月的开始时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date beginOfMonth(Date date) {
        return beginOfMonth(calendar(date)).getTime();
    }

    /**
     * 获取某月的结束时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date endOfMonth(Date date) {
        return endOfMonth(calendar(date)).getTime();
    }

    /**
     * 获取某季度的开始时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date beginOfQuarter(Date date) {
        return beginOfQuarter(calendar(date)).getTime();
    }

    /**
     * 获取某季度的结束时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date endOfQuarter(Date date) {
        return endOfQuarter(calendar(date)).getTime();
    }

    /**
     * 获取某年的开始时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date beginOfYear(Date date) {
        return beginOfYear(calendar(date)).getTime();
    }

    /**
     * 获取某年的结束时间
     *
     * @param date 日期
     * @return {@link Date}
     */
    public static Date endOfYear(Date date) {
        return endOfYear(calendar(date)).getTime();
    }
    // --------------------------------------------------- Offset for now

    /**
     * 昨天
     *
     * @return 昨天
     */
    public static Date yesterday() {
        return offsetDay(date(), -1);
    }

    /**
     * 明天
     *
     * @return 明天
     *      */
    public static Date tomorrow() {
        return offsetDay(date(), 1);
    }

    /**
     * 上周
     *
     * @return 上周
     */
    public static Date lastWeek() {
        return offsetWeek(date(), -1);
    }

    /**
     * 下周
     *
     * @return 下周
     *      */
    public static Date nextWeek() {
        return offsetWeek(date(), 1);
    }

    /**
     * 上个月
     *
     * @return 上个月
     */
    public static Date lastMonth() {
        return offsetMonth(date(), -1);
    }

    /**
     * 下个月
     *
     * @return 下个月
     *      */
    public static Date nextMonth() {
        return offsetMonth(date(), 1);
    }

    /**
     * 偏移毫秒数
     *
     * @param date   日期
     * @param offset 偏移毫秒数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsetMillisecond(Date date, int offset) {
        return offset(date, DateField.MILLISECOND, offset);
    }

    /**
     * 偏移秒数
     *
     * @param date   日期
     * @param offset 偏移秒数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsetSecond(Date date, int offset) {
        return offset(date, DateField.SECOND, offset);
    }

    /**
     * 偏移分钟
     *
     * @param date   日期
     * @param offset 偏移分钟数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsetMinute(Date date, int offset) {
        return offset(date, DateField.MINUTE, offset);
    }

    /**
     * 偏移小时
     *
     * @param date   日期
     * @param offset 偏移小时数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsetHour(Date date, int offset) {
        return offset(date, DateField.HOUR_OF_DAY, offset);
    }

    /**
     * w
     * 偏移天
     *
     * @param date   日期
     * @param offset 偏移天数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsetDay(Date date, int offset) {
        return offset(date, DateField.DAY_OF_YEAR, offset);
    }

    /**
     * 偏移周
     *
     * @param date   日期
     * @param offset 偏移周数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsetWeek(Date date, int offset) {
        return offset(date, DateField.WEEK_OF_YEAR, offset);
    }

    /**
     * 偏移月
     *
     * @param date   日期
     * @param offset 偏移月数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsetMonth(Date date, int offset) {
        return offset(date, DateField.MONTH, offset);
    }

    /**
     * 获取指定日期偏移指定时间后的时间，生成的偏移日期不影响原日期
     *
     * @param date      基准日期
     * @param dateField 偏移的粒度大小（小时、天、月等）{@link DateField}
     * @param offset    偏移量，正数为向后偏移，负数为向前偏移
     * @return 偏移后的日期
     */
    public static Date offset(Date date, DateField dateField, int offset) {
        return offset(date, dateField.getValue(), offset);
    }

    /**
     * 获取指定日期偏移指定时间后的时间，生成的偏移日期不影响原日期
     *
     * @param date      基准日期
     * @param dateField 偏移的粒度大小（小时、天、月等）{@link DateField}
     * @param offset    偏移量，正数为向后偏移，负数为向前偏移
     * @return 偏移后的日期
     */
    public static Date offset(Date date, int dateField, int offset) {
        Calendar calendar = calendar(date);
        calendar.add(dateField, offset);
        return date(calendar);

    }
    // ------------------------------------ Offset end
    // ----------------------------------------------

    /**
     * 判断两个日期相差的时长，只保留绝对值
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @return 日期差
     */
    public static long between(Date beginDate, Date endDate, TimeUnit unit) {
        return between(beginDate, endDate, unit, true);
    }

    /**
     * 判断两个日期相差的时长
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @param isAbs     日期间隔是否只保留绝对值正数
     * @return 日期差
     *      */
    public static long between(Date beginDate, Date endDate, TimeUnit unit, boolean isAbs) {
        return new DateBetween(beginDate, endDate, isAbs).between(unit);
    }

    /**
     * 判断两个日期相差的毫秒数
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @return 日期差
     *      */
    public static long betweenMs(Date beginDate, Date endDate) {
        return new DateBetween(beginDate, endDate).between(TimeUnit.MILLISECONDS);
    }

    /**
     * 判断两个日期相差的天数<br>
     *
     * <pre>
     * 有时候我们计算相差天数的时候需要忽略时分秒。
     * 比如：2016-02-01 23:59:59和2016-02-02 00:00:00相差一秒
     * 如果isReset为{@code
     * false
     * }相差天数为0。
     * 如果isReset为{@code
     * true
     * }相差天数将被计算为1
     * </pre>
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @param isReset   是否重置时间为起始时间
     * @return 日期差
     *      */
    public static long betweenDay(Date beginDate, Date endDate, boolean isReset) {
        if (isReset) {
            beginDate = beginOfDay(beginDate);
            endDate = beginOfDay(endDate);
        }
        return between(beginDate, endDate, TimeUnit.DAYS);
    }

    /**
     * 计算指定时间区间内的周数
     *
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @param isReset   是否重置时间为起始时间
     * @return 周数
     */
    public static long betweenWeek(Date beginDate, Date endDate, boolean isReset) {
        if (isReset) {
            beginDate = beginOfDay(beginDate);
            endDate = beginOfDay(endDate);
        }
        return between(beginDate, endDate, TimeUnit.DAYS) / 7;
    }

    /**
     * 计算两个日期相差月数<br>
     * 在非重置情况下，如果起始日期的天大于结束日期的天，月数要少算1（不足1个月）
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @param isReset   是否重置时间为起始时间（重置天时分秒）
     * @return 相差月数
     *      */
    public static long betweenMonth(Date beginDate, Date endDate, boolean isReset) {
        return new DateBetween(beginDate, endDate).betweenMonth(isReset);
    }

    /**
     * 计算两个日期相差年数<br>
     * 在非重置情况下，如果起始日期的月大于结束日期的月，年数要少算1（不足1年）
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @param isReset   是否重置时间为起始时间（重置月天时分秒）
     * @return 相差年数
     *      */
    public static long betweenYear(Date beginDate, Date endDate, boolean isReset) {
        return new DateBetween(beginDate, endDate).betweenYear(isReset);
    }

    /**
     * 当前日期是否在日期指定范围内<br>
     * 起始日期和结束日期可以互换
     *
     * @param date      被检查的日期
     * @param beginDate 起始日期（包含）
     * @param endDate   结束日期（包含）
     * @return 是否在范围内
     *      */
    public static boolean isIn(Date date, Date beginDate, Date endDate) {
        return date.getTime() >= beginDate.getTime() && date.getTime() <= endDate.getTime();
    }

    /**
     * 是否为相同时间<br>
     * 此方法比较两个日期的时间戳是否相同
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否为相同时间
     *      */
    public static boolean isSameTime(Date date1, Date date2) {
        return date1.compareTo(date2) == 0;
    }

    /**
     * 比较两个日期是否为同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否为同一天
     *      */
    public static boolean isSameDay(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return CalendarUtils.isSameDay(calendar(date1), calendar(date2));
    }

    /**
     * 比较两个日期是否为同一周
     *
     * @param date1 日期1
     * @param date2 日期2
     * @param isMon 是否为周一。国内第一天为星期一，国外第一天为星期日
     * @return 是否为同一周
     */
    public static boolean isSameWeek(final Date date1, final Date date2, boolean isMon) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return CalendarUtils.isSameWeek(calendar(date1), calendar(date2), isMon);
    }

    /**
     * 比较两个日期是否为同一月
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否为同一月
     *      */
    public static boolean isSameMonth(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return CalendarUtils.isSameMonth(calendar(date1), calendar(date2));
    }

    /**
     * 计时，常用于记录某段代码的执行时间，单位：纳秒
     *
     * @param preTime 之前记录的时间
     * @return 时间差，纳秒
     */
    public static long spendNt(long preTime) {
        return System.nanoTime() - preTime;
    }

    /**
     * 计时，常用于记录某段代码的执行时间，单位：毫秒
     *
     * @param preTime 之前记录的时间
     * @return 时间差，毫秒
     */
    public static long spendMs(long preTime) {
        return System.currentTimeMillis() - preTime;
    }

    /**
     * 生日转为年龄，计算法定年龄
     *
     * @param birthDay 生日，标准日期字符串
     * @return 年龄
     */
    public static int ageOfNow(String birthDay) {
        return ageOfNow(parse(birthDay));
    }

    /**
     * 生日转为年龄，计算法定年龄
     *
     * @param birthDay 生日
     * @return 年龄
     */
    public static int ageOfNow(Date birthDay) {
        return age(birthDay, date());
    }

    /**
     * 是否闰年
     *
     * @param year 年
     * @return 是否闰年
     */
    public static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }

    /**
     * 计算相对于dateToCompare的年龄，长用于计算指定生日在某年的年龄
     *
     * @param birthday      生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     */
    public static int age(Date birthday, Date dateToCompare) {
        Assert.notNull(birthday, "Birthday can not be null !");
        if (null == dateToCompare) {
            dateToCompare = date();
        }
        return age(birthday.getTime(), dateToCompare.getTime());
    }

    /**
     * HH:mm:ss 时间格式字符串转为秒数<br>
     * 参考：<a href="https://github.com/iceroot">https://github.com/iceroot</a>
     *
     * @param timeStr 字符串时分秒(HH:mm:ss)格式
     * @return 时分秒转换后的秒数
     *      */
    public static int timeToSecond(String timeStr) {
        if (StringUtils.isEmpty(timeStr)) {
            return 0;
        }

        final List<String> hms = StringUtils.splitTrim(timeStr,
                StringUtils.COLON, 3);
        int lastIndex = hms.size() - 1;

        int result = 0;
        for (int i = lastIndex; i >= 0; i--) {
            result += Integer.parseInt(hms.get(i)) * Math.pow(60, (lastIndex - i));
        }
        return result;
    }

    /**
     * 秒数转为时间格式(HH:mm:ss)<br>
     * 参考：<a href="https://github.com/iceroot">https://github.com/iceroot</a>
     *
     * @param seconds 需要转换的秒数
     * @return 转换后的字符串
     *      */
    public static String secondToTime(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Seconds must be a positive number!");
        }

        int hour = seconds / 3600;
        int other = seconds % 3600;
        int minute = other / 60;
        int second = other % 60;
        final StringBuilder sb = new StringBuilder();
        if (hour < 10) {
            sb.append("0");
        }
        sb.append(hour);
        sb.append(":");
        if (minute < 10) {
            sb.append("0");
        }
        sb.append(minute);
        sb.append(":");
        if (second < 10) {
            sb.append("0");
        }
        sb.append(second);
        return sb.toString();
    }

    /**
     * 通过生日计算星座
     *
     * @param month 月，从0开始计数
     * @param day   天
     * @return 星座名
     *      */
    public static String getZodiac(int month, int day) {
        return Zodiac.getZodiac(month, day);
    }

    /**
     * 计算生肖，只计算1900年后出生的人
     *
     * @param year 农历年
     * @return 生肖名
     *      */
    public static String getChineseZodiac(int year) {
        return Zodiac.getChineseZodiac(year);
    }

    /**
     * {@code null}安全的日期比较，{@code null}对象排在末尾
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 比较结果，如果date1 &lt; date2，返回数小于0，date1==date2返回0，date1 &gt; date2 大于0
     *      */
    public static int compare(Date date1, Date date2) {
        return CompareUtils.compare(date1, date2);
    }

    /**
     * {@code null}安全的日期比较，并只比较指定格式； {@code null}对象排在末尾, 并指定日期格式；
     *
     * @param date1  日期1
     * @param date2  日期2
     * @param format 日期格式，常用格式见： {@link DatePattern}; 允许为空； date1 date2; eg:
     *               yyyy-MM-dd
     * @return 比较结果，如果date1 &lt; date2，返回数小于0，date1==date2返回0，date1 &gt; date2 大于0
     */
    public static int compare(Date date1, Date date2, String format) {
        if (format != null) {
            if (date1 != null) {
                date1 = parse(format(date1, format), format);
            }
            if (date2 != null) {
                date2 = parse(format(date2, format), format);
            }
        }
        return CompareUtils.compare(date1, date2);
    }

    /**
     * 纳秒转毫秒
     *
     * @param duration 时长
     * @return 时长毫秒
     *      */
    public static long nanosToMillis(long duration) {
        return TimeUnit.NANOSECONDS.toMillis(duration);
    }

    /**
     * 纳秒转秒，保留小数
     *
     * @param duration 时长
     * @return 秒
     *      */
    public static double nanosToSeconds(long duration) {
        return duration / 1_000_000_000.0;
    }

    /**
     * Date对象转换为{@link Instant}对象
     *
     * @param date Date对象
     * @return {@link Instant}对象
     *      */
    public static Instant toInstant(Date date) {
        return null == date ? null : date.toInstant();
    }

    /**
     * Date对象转换为{@link Instant}对象
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     *      */
    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        return TemporalAccessorUtils.toInstant(temporalAccessor);
    }

    /**
     * {@link Instant} 转换为 {@link LocalDateTime}，使用系统默认时区
     *
     * @param instant {@link Instant}
     * @return {@link LocalDateTime}
     *      */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTimeUtils.of(instant);
    }

    /**
     * {@link Date} 转换为 {@link LocalDateTime}，使用系统默认时区
     *
     * @param date {@link Date}
     * @return {@link LocalDateTime}
     *      */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTimeUtils.of(date);
    }

    /**
     * {@link Date} 转换时区
     *
     * @param date   {@link Date}
     * @param zoneId {@link ZoneId}
     * @return {@link Date}
     *      */
    public static Date convertTimeZone(Date date, ZoneId zoneId) {
        return date(date.toInstant().atZone(zoneId));
    }

    /**
     * {@link Date} 转换时区
     *
     * @param date     {@link Date}
     * @param timeZone {@link TimeZone}
     * @return {@link Date}
     *      */
    public static Date convertTimeZone(Date date, TimeZone timeZone) {
        return date(date.toInstant().atZone(timeZone.toZoneId()));
    }

    /**
     * 获得指定年份的总天数
     *
     * @param year 年份
     * @return 天
     *      */
    public static int lengthOfYear(int year) {
        return Year.of(year).length();
    }

    /**
     * 获得指定月份的总天数
     *
     * @param month      月份
     * @param isLeapYear 是否闰年
     * @return 天
     *      */
    public static int lengthOfMonth(int month, boolean isLeapYear) {
        return java.time.Month.of(month).length(isLeapYear);
    }

    /**
     * 创建{@link SimpleDateFormat}，注意此对象非线程安全！<br>
     * 此对象默认为严格格式模式，即parse时如果格式不正确会报错。
     *
     * @param pattern 表达式
     * @return {@link SimpleDateFormat}
     *      */
    public static SimpleDateFormat newSimpleFormat(String pattern) {
        return newSimpleFormat(pattern, null, null);
    }

    /**
     * 创建{@link SimpleDateFormat}，注意此对象非线程安全！<br>
     * 此对象默认为严格格式模式，即parse时如果格式不正确会报错。
     *
     * @param pattern  表达式
     * @param locale   {@link Locale}，{@code null}表示默认
     * @param timeZone {@link TimeZone}，{@code null}表示默认
     * @return {@link SimpleDateFormat}
     *      */
    public static SimpleDateFormat newSimpleFormat(String pattern, Locale locale, TimeZone timeZone) {
        if (null == locale) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        final SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
        if (null != timeZone) {
            format.setTimeZone(timeZone);
        }
        format.setLenient(false);
        return format;
    }

    /**
     * 获取时长单位简写
     *
     * @param unit 单位
     * @return 单位简写名称
     *      */
    public static String getShotName(TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "μs";
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "min";
            case HOURS:
                return "h";
            default:
                return unit.name().toLowerCase();
        }
    }

    /**
     * 检查两个时间段是否有时间重叠<br>
     * 重叠指两个时间段是否有交集
     *
     * @param realStartTime 第一个时间段的开始时间
     * @param realEndTime   第一个时间段的结束时间
     * @param startTime     第二个时间段的开始时间
     * @param endTime       第二个时间段的结束时间
     * @return true 表示时间有重合
     *      */
    public static boolean isOverlap(Date realStartTime, Date realEndTime, Date startTime, Date endTime) {

        // x>b||a>y 无交集
        // 则有交集的逻辑为 !(x>b||a>y)
        // 根据德摩根公式，可化简为 x<=b && a<=y
        return startTime.before(realEndTime) && endTime.after(realStartTime);
    }

    // ------------------------------------------------------------------------
    // Private method start

    /**
     * 标准化日期，默认处理以空格区分的日期时间格式，空格前为日期，空格后为时间：<br>
     * 将以下字符替换为"-"
     *
     * <pre>
     * "."
     * "/"
     * "年"
     * "月"
     * </pre>
     * <p>
     * 将以下字符去除
     *
     * <pre>
     * "日"
     * </pre>
     * <p>
     * 将以下字符替换为":"
     *
     * <pre>
     * "时"
     * "分"
     * "秒"
     * </pre>
     * <p>
     * 当末位是":"时去除之（不存在毫秒时）
     *
     * @param dateStr 日期时间字符串
     * @return 格式化后的日期字符串
     */
    private static String normalize(CharSequence dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return StringUtils.str(dateStr);
        }

        // 日期时间分开处理
        final List<String> dateAndTime = StringUtils.splitTrim(dateStr, ' ');
        final int size = dateAndTime.size();
        if (size < 1 || size > 2) {
            // 非可被标准处理的格式
            return StringUtils.str(dateStr);
        }

        final StringBuilder builder = StringUtils.builder();

        // 日期部分（"\"、"/"、"."、"年"、"月"都替换为"-"）
        String datePart = dateAndTime.get(0).replaceAll("[/.年月]", "-");
        datePart = StringUtils.removeSuffix(datePart, "日");
        builder.append(datePart);

        // 时间部分
        if (size == 2) {
            builder.append(' ');
            String timePart = dateAndTime.get(1).replaceAll("[时分秒]", ":");
            timePart = StringUtils.removeSuffix(timePart, ":");
            // 将ISO8601中的逗号替换为.
            timePart = timePart.replace(',', '.');
            builder.append(timePart);
        }

        return builder.toString();
    }
    // ------------------------------------------------------------------------
    // Private method end

    /**
     * 随机生成日期（startDateInclusive至endDateExclusive）
     *
     * @param startDateInclusive start
     * @param endDateExclusive   end
     * @return the date
     */
    public static Date random(Date startDateInclusive, Date endDateExclusive) {
        int betweenSeconds = (int) ((endDateExclusive.getTime() - startDateInclusive.getTime()) / 1000);

        Calendar instance = Calendar.getInstance();
        instance.setTime(startDateInclusive);
        instance.add(Calendar.SECOND, RandomUtils.randomInt(0, betweenSeconds));
        return instance.getTime();
    }

    /**
     * 随机生成日期
     *
     * @return date
     */
    public static Date random() {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(0L);
        instance.add(Calendar.MINUTE, RandomUtils.randomInt(0, Integer.MAX_VALUE));
        return instance.getTime();
    }


    /**
     * 获取某天的昨天
     *
     * @param date current date
     * @return yesterday date
     */
    public static Date getYesterday(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusDays(1);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取昨天
     *
     * @return yesterday
     */
    public static Date getYesterday() {
        return getYesterday(Calendar.getInstance().getTime());
    }

    /**
     * 获取某天的明天
     *
     * @param date
     * @return tomorrow
     */
    public static Date getTomorrow(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.plusDays(1);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getTomorrow() {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    /**
     * 判断指定日期是否在某个时间区间内
     *
     * @param date               current
     * @param startDateInclusive start date
     * @param endDateInclusive   end date
     * @return is in
     */
    public static boolean isDayBetween(Date date, Date startDateInclusive, Date endDateInclusive) {
        if (date == null) {
            return false;
        }
        return ((date.after(startDateInclusive) && date.before(endDateInclusive)) || date.equals(startDateInclusive)
                || date.equals(endDateInclusive));
    }

    /**
     * 自动解析日期
     *
     * @param dateTimeStr date time string
     * @return localDateTime
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeStr) {
        if (StringUtils.isEmpty(dateTimeStr)) {
            throw new IllegalArgumentException("Date string must not be null");
        }

        String[] dateFormats;

        if (NumberUtils.isCreatable(dateTimeStr)) {
            // 纯数字
            dateFormats = FREQUENTLY_USED_NUMBER_DATE_FORMATS;
        } else if (StringUtils.contains(dateTimeStr, 'T')) {
            // UTC
            String utcString = dateTimeStr;
            int length = utcString.length();
            if (StringUtils.contains(utcString, 'Z')) {
                dateFormats = FREQUENTLY_USED_UTC_WITH_Z_DATE_FORMATS;
            } else if (StringUtils.contains(utcString, '+')) {
                // 去除类似2019-06-01T19:45:43 +08:00加号前的空格
                utcString = utcString.replace(" +", "+");
                final String zoneOffset = StringUtils.subAfter(utcString, '+', true);
                if (StringUtils.isBlank(zoneOffset)) {
                    throw new TaraException("Invalid UTC format: [{}]", dateTimeStr);
                }

                dateFormats = new String[]{DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT.getPattern(),
                        DatePattern.UTC_WITH_XXX_OFFSET_FORMAT.getPattern()};
            } else {
                if (length == DatePattern.UTC_SIMPLE_PATTERN.length() - 2) {
                    // 格式类似：2018-09-13T05:34:31
                    dateFormats = new String[]{DatePattern.UTC_SIMPLE_FORMAT.getPattern()};
                } else if (StringUtils.contains(utcString, CharUtils.DOT)) {
                    // 可能为： 2021-03-17T06:31:33.99
                    dateFormats = new String[]{DatePattern.UTC_SIMPLE_MS_FORMAT.getPattern()};
                } else {
                    throw new TaraException("Invalid UTC format: [{}]", dateTimeStr);
                }
            }
        } else if (StringUtils.containsAny(dateTimeStr, wtb)) {
            // CST格式
            dateFormats = FREQUENTLY_USED_CST_DATE_FORMATS;
        } else {
            // 其它
            dateTimeStr = normalize(dateTimeStr);
            dateFormats = FREQUENTLY_USED_DATE_FORMATS;
        }

        for (String dateFormat : dateFormats) {
            try {
                return parseLocalDateTime(dateTimeStr, DateTimeFormatter.ofPattern(dateFormat));
            } catch (Exception e) {
                log.debug("Error parsing date format: " + dateFormat, e);
            }

        }
        throw new IllegalStateException("Invalid date format: " + dateTimeStr);
    }

    /**
     * 抹除时间，只保留日期
     *
     * @param date the date
     * @return date erase time
     */
    public static Date eraseTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        eraseTime(calendar);
        return calendar.getTime();
    }

    /**
     * 抹除时间，只保留日期
     *
     * @param calendar calendar
     * @return date erase time
     */
    public static Date eraseTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取格式化时长
     *
     * @param timeLength time length
     * @param timeUnit   unit
     * @return string
     */
    public static String getGapTime(long timeLength, TimeUnit timeUnit) {
        String separator = ":";
        StringBuilder gapTime = new StringBuilder();
        long days = timeUnit.toDays(timeLength);
        if (days > 0) {
            gapTime.append(StringUtils.leftPad(String.valueOf(days % 365), 2, "0"));
            gapTime.append(separator);
        }

        long hours = timeUnit.toHours(timeLength);
        if (hours > 0) {
            gapTime.append(StringUtils.leftPad(String.valueOf(hours % 24), 2, "0"));
            gapTime.append(separator);
        }

        long minutes = timeUnit.toMinutes(timeLength);
        if (minutes > 0) {
            gapTime.append(StringUtils.leftPad(String.valueOf(minutes % 60), 2, "0"));
            gapTime.append(separator);
        }

        long seconds = timeUnit.toSeconds(timeLength);
        if (seconds > 0) {
            gapTime.append(StringUtils.leftPad(String.valueOf(seconds % 60), 2, "0"));
        }

        return gapTime.toString();
    }

    public static String today() {
        return now(DatePattern.NORM_DATE_PATTERN);
    }


    public static LocalDateTime parseLocalDateTime(String dateTimeStr, String formatPattern) {
        try {
            if (StringUtils.isBlank(dateTimeStr)) {
                return null;
            }

            DateTimeFormatter formatter = null;
            if (StringUtils.isNotBlank(formatPattern)) {
                // 修复yyyyMMddHHmmssSSS格式不能解析的问题
                // fix issue#1082
                // see
                // https://stackoverflow.com/questions/22588051/is-java-time-failing-to-parse-fraction-of-second
                // jdk8 bug at: https://bugs.openjdk.java.net/browse/JDK-8031085
                if (StringUtils.startWith(formatPattern,
                        DatePattern.PURE_DATETIME_PATTERN)) {
                    final String fraction = StringUtils.removePrefix(formatPattern,
                            DatePattern.PURE_DATETIME_PATTERN);
                    if (RegexUtils.isMatch("[S]{1,2}", fraction)) {
                        // 将yyyyMMddHHmmssS、yyyyMMddHHmmssSS的日期统一替换为yyyyMMddHHmmssSSS格式，用0补
                        formatPattern += StringUtils.repeat('0', 3 - fraction.length());
                    }
                    formatter = new DateTimeFormatterBuilder().appendPattern(DatePattern.PURE_DATETIME_PATTERN)
                            .appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
                } else {
                    formatter = DateTimeFormatter.ofPattern(formatPattern);
                }
            }
            return parseLocalDateTime(dateTimeStr, formatter);
        } catch (Exception e) {
            log.error("parser date error,params=[{}],pattern=[{}]", dateTimeStr, formatPattern, e);
            return null;
        }
    }

    private static LocalDate parseLocalDate(String date) {
        for (String formatter : DatePattern.GENERIC_DATE_FORMATTERS) {
            LocalDate localDate = parseLocalDate(date, formatter);
            if (null != localDate) {
                return localDate;
            }
        }
        return localDate(parseDate(date));
    }

    /**
     * @param date jdk8之前的date
     */
    public static String format(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        Instant instant = null;
        try {
            instant = date.toInstant();
        } catch (Exception e) {
            //UnsupportedOperationException
            instant = Instant.ofEpochMilli(date.getTime());
        }
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }

    public static String format(Date date) {
        if (null == date) {
            return null;
        }

        for (String format : FREQUENTLY_USED_DATE_FORMATS) {
            try {
                return format(date, format);
            } catch (Exception ignored) {

            }
        }
        //if all formats are failed, return the default format string by instant of the date parameter.
        return TemporalAccessorUtils.format(date.toInstant(), DatePattern.NORM_DATETIME_PATTERN);
    }


    /**
     * @param dateTime 时间
     * @return
     * @deprecated {@link LocalDateTimeUtils#parse(CharSequence)}
     */

    @Deprecated
    public static Date parseDate(String dateTime) {
        return parse(dateTime);
    }

    public static Date parse(String dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        Date date = null;
        try {
            LocalDateTime localDateTime = parseLocalDateTime(dateTime, pattern);
            assert localDateTime != null;
            date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception ignored) {
        }
        if (date == null) {
            try {
                final LocalDate parse = parseLocalDate(dateTime);
                date = Date.from(parse.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            } catch (Exception ignored) {
            }

        }
        if (null != date) {
            return date;
        }
        return parse(dateTime, FastDateFormat.getInstance(pattern));
    }

    /**
     * 获取两个日期间隔天数，只要不是同一天，间隔就为1
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return the two date delay
     */
    public static int getDayDelay(String startDate, String endDate) {

        final LocalDate start = parseLocalDate(startDate);
        final LocalDate end = parseLocalDate(endDate);
        if (start != null && end != null) {
            final long l = end.toEpochDay() - start.toEpochDay();
            return (int) l;
        }
        return -1;
    }


    /**
     * 获取时间戳
     *
     * @param timeStr   时间字符串
     * @param formatter 格式化
     * @return 时间
     */
    public static Long getTimeStamp(String timeStr, String formatter) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern(formatter));
            return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        } catch (Exception e) {
            log.error("", e);
        }
        try {
            LocalDate localDate = LocalDate.parse(timeStr, DateTimeFormatter.ofPattern(formatter));
            return localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * 尝试获取时间戳
     *
     * @param timeStr 时间str
     * @return 时间戳
     */
    public static Long getTimeStamp(String timeStr) {

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(timeStr,
                    DateTimeFormatter.ofPattern(DATE_TIME_MILLS_FORMATTER));
            return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        } catch (Exception e) {

        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(timeStr,
                    DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER));
            return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        } catch (Exception e) {
        }

        try {
            LocalDate localDate = LocalDate.parse(timeStr, DateTimeFormatter.ofPattern(DATE_FORMATTER));
            return localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
        } catch (Exception e) {
        }

        return null;
    }

    /**
     * 时间戳获取时间
     *
     * @param timestamp timestamp
     * @return localDateTime
     */
    public static LocalDateTime getLocalDateTime(long timestamp) {
        try {
            return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.of("+8")).toLocalDateTime();
        } catch (Exception e) {

        }
        try {
            return Instant.ofEpochSecond(timestamp).atZone(ZoneOffset.of("+8")).toLocalDateTime();
        } catch (Exception e) {

        }

        throw new RuntimeException("timestamp is invalid:" + timestamp);

    }

    /**
     * 时间戳获取时间
     *
     * @param timestamp timestamp
     * @return localDate
     */
    public static LocalDate getLocalDate(long timestamp) {
        try {
            return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.of("+8")).toLocalDate();
        } catch (Exception e) {

        }
        try {
            return Instant.ofEpochSecond(timestamp).atZone(ZoneOffset.of("+8")).toLocalDate();
        } catch (Exception e) {

        }

        throw new RuntimeException("timestamp is invalid:" + timestamp);

    }

    /**
     * 时间戳获取秒日期
     *
     * @param timestamp timestamp
     * @return date time
     */
    public static String getDateTimeSecondStr(long timestamp) {
        LocalDateTime localDateTime = getLocalDateTime(timestamp);
        if (null != localDateTime) {
            return localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER));
        }
        throw new RuntimeException("timestamp is invalid:" + timestamp);
    }

    /**
     * 时间戳获取毫秒时间
     *
     * @param timestamp timestamp
     * @return data time
     */
    public static String getDateTimeMillsStr(long timestamp) {
        LocalDateTime localDateTime = getLocalDateTime(timestamp);
        if (null != localDateTime) {
            localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_MILLS_FORMATTER));
        }
        throw new RuntimeException("timestamp is invalid:" + timestamp);
    }

    /**
     * 时间戳获取日期
     *
     * @param timestamp timestamp
     * @return data str
     */
    public static String getDateStr(long timestamp) {
        LocalDate localDate = getLocalDate(timestamp);
        if (null != localDate) {
            localDate.format(DateTimeFormatter.ofPattern(DATE_FORMATTER));
        }
        throw new RuntimeException("timestamp is invalid:" + timestamp);
    }

    /**
     * {@link Instant}转{@link LocalDateTime}，使用默认时区
     *
     * @param instant {@link Instant}
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime of(Instant instant) {
        return of(instant, ZoneId.systemDefault());
    }

    /**
     * {@link Instant}转{@link LocalDateTime}，使用UTC时区
     *
     * @param instant {@link Instant}
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime ofUTC(Instant instant) {
        return of(instant, ZoneId.of("UTC"));
    }

    /**
     * {@link ZonedDateTime}转{@link LocalDateTime}
     *
     * @param zonedDateTime {@link ZonedDateTime}
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime of(ZonedDateTime zonedDateTime) {
        if (null == zonedDateTime) {
            return null;
        }
        return zonedDateTime.toLocalDateTime();
    }

    /**
     * {@link Instant}转{@link LocalDateTime}
     *
     * @param instant {@link Instant}
     * @param zoneId  时区
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime of(Instant instant, ZoneId zoneId) {
        if (null == instant) {
            return null;
        }

        return LocalDateTime.ofInstant(instant, ObjectUtils.defaultIfNull(zoneId, ZoneId::systemDefault));
    }

    /**
     * {@link Instant}转{@link LocalDateTime}
     *
     * @param instant  {@link Instant}
     * @param timeZone 时区
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime of(Instant instant, TimeZone timeZone) {
        if (null == instant) {
            return null;
        }

        return of(instant, ObjectUtils.defaultIfNull(timeZone, TimeZone::getDefault).toZoneId());
    }

    /**
     * 毫秒转{@link LocalDateTime}，使用默认时区
     *
     * <p>
     * 注意：此方法使用默认时区，如果非UTC，会产生时间偏移
     * </p>
     *
     * @param epochMilli 从1970-01-01T00:00:00Z开始计数的毫秒数
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime of(long epochMilli) {
        return of(Instant.ofEpochMilli(epochMilli));
    }

    /**
     * 毫秒转{@link LocalDateTime}，使用UTC时区
     *
     * @param epochMilli 从1970-01-01T00:00:00Z开始计数的毫秒数
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime ofUTC(long epochMilli) {
        return ofUTC(Instant.ofEpochMilli(epochMilli));
    }

    /**
     * 毫秒转{@link LocalDateTime}，根据时区不同，结果会产生时间偏移
     *
     * @param epochMilli 从1970-01-01T00:00:00Z开始计数的毫秒数
     * @param zoneId     时区
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime of(long epochMilli, ZoneId zoneId) {
        return of(Instant.ofEpochMilli(epochMilli), zoneId);
    }

    /**
     * 毫秒转{@link LocalDateTime}，结果会产生时间偏移
     *
     * @param epochMilli 从1970-01-01T00:00:00Z开始计数的毫秒数
     * @param timeZone   时区
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime of(long epochMilli, TimeZone timeZone) {
        return of(Instant.ofEpochMilli(epochMilli), timeZone);
    }

    /**
     * {@link Date}转{@link LocalDateTime}，使用默认时区
     *
     * @param date Date对象
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime of(Date date) {
        if (null == date) {
            return null;
        }
        return of(date.toInstant());
    }

    /**
     * {@link TemporalAccessor}转{@link LocalDateTime}，使用默认时区
     *
     * @param temporalAccessor {@link TemporalAccessor}
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime of(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        if (temporalAccessor instanceof LocalDate) {
            return ((LocalDate) temporalAccessor).atStartOfDay();
        } else if (temporalAccessor instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) temporalAccessor, ZoneId.systemDefault());
        } else if (temporalAccessor instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporalAccessor).toLocalDateTime();
        }

        return LocalDateTime.of(TemporalAccessorUtils.get(temporalAccessor, ChronoField.YEAR),
                TemporalAccessorUtils.get(temporalAccessor, ChronoField.MONTH_OF_YEAR),
                TemporalAccessorUtils.get(temporalAccessor, ChronoField.DAY_OF_MONTH),
                TemporalAccessorUtils.get(temporalAccessor, ChronoField.HOUR_OF_DAY),
                TemporalAccessorUtils.get(temporalAccessor, ChronoField.MINUTE_OF_HOUR),
                TemporalAccessorUtils.get(temporalAccessor, ChronoField.SECOND_OF_MINUTE),
                TemporalAccessorUtils.get(temporalAccessor, ChronoField.NANO_OF_SECOND));
    }

    /**
     * {@link TemporalAccessor}转{@link LocalDate}，使用默认时区
     *
     * @param temporalAccessor {@link TemporalAccessor}
     * @return {@link LocalDate}
     *      */
    public static LocalDate ofDate(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        if (temporalAccessor instanceof LocalDateTime) {
            return ((LocalDateTime) temporalAccessor).toLocalDate();
        } else if (temporalAccessor instanceof Instant) {
            return of(temporalAccessor).toLocalDate();
        }

        return LocalDate.of(TemporalAccessorUtils.get(temporalAccessor, ChronoField.YEAR),
                TemporalAccessorUtils.get(temporalAccessor, ChronoField.MONTH_OF_YEAR),
                TemporalAccessorUtils.get(temporalAccessor, ChronoField.DAY_OF_MONTH));
    }

    /**
     * 解析日期时间字符串为{@link LocalDateTime}，格式支持日期时间、日期、时间<br>
     * 如果formatter为{code null}，则使用{@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}
     *
     * @param text      日期时间字符串
     * @param formatter 日期格式化器，预定义的格式见：{@link DateTimeFormatter}
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime parseLocalDateTime(CharSequence text, DateTimeFormatter formatter) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        if (null == formatter) {
            return LocalDateTime.parse(text);
        }

        return of(formatter.parse(text));
    }

    /**
     * 解析日期时间字符串为{@link LocalDate}，仅支持yyyy-MM-dd'T'HH:mm:ss格式，例如：2007-12-03T10:15:30
     *
     * @param text 日期时间字符串
     * @return {@link LocalDate}
     *      */
    public static LocalDate parseLocalDate(CharSequence text) {
        return parseLocalDate(text, (DateTimeFormatter) null);
    }

    /**
     * 解析日期时间字符串为{@link LocalDate}，格式支持日期
     *
     * @param text      日期时间字符串
     * @param formatter 日期格式化器，预定义的格式见：{@link DateTimeFormatter}
     * @return {@link LocalDate}
     *      */
    public static LocalDate parseLocalDate(CharSequence text, DateTimeFormatter formatter) {
        if (null == text) {
            return null;
        }
        if (null == formatter) {
            return LocalDate.parse(text);
        }

        return ofDate(formatter.parse(text));
    }

    /**
     * 解析日期字符串为{@link LocalDate}
     *
     * @param text   日期字符串
     * @param format 日期格式，类似于yyyy-MM-dd
     * @return {@link LocalDateTime}
     */
    public static LocalDate parseLocalDate(CharSequence text, String format) {
        if (null == text) {
            return null;
        }
        return parseLocalDate(text, DateTimeFormatter.ofPattern(format));
    }


    /**
     * 格式化日期时间为指定格式
     *
     * @param time      {@link LocalDateTime}
     * @param formatter 日期格式化器，预定义的格式见：{@link DateTimeFormatter}
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime time, DateTimeFormatter formatter) {
        return TemporalAccessorUtils.format(time, formatter);
    }

    /**
     * 格式化日期时间为指定格式
     *
     * @param time   {@link LocalDateTime}
     * @param format 日期格式，类似于yyyy-MM-dd HH:mm:ss,SSS
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime time, String format) {
        return TemporalAccessorUtils.format(time, format);
    }


    /**
     * 格式化日期时间为指定格式
     *
     * @param date      {@link LocalDate}
     * @param formatter 日期格式化器，预定义的格式见：{@link DateTimeFormatter}; 常量如：
     *                  {@link DatePattern#NORM_DATE_FORMATTER},
     *                  {@link DatePattern#NORM_DATETIME_FORMATTER}
     * @return 格式化后的字符串
     *      */
    public static String format(LocalDate date, DateTimeFormatter formatter) {
        return TemporalAccessorUtils.format(date, formatter);
    }

    /**
     * 格式化日期时间为指定格式
     *
     * @param date   {@link LocalDate}
     * @param format 日期格式，类似于yyyy-MM-dd, 常量如 {@link DatePattern#NORM_DATE_PATTERN},
     *               {@link DatePattern#NORM_DATETIME_PATTERN}
     * @return 格式化后的字符串
     *      */
    public static String format(LocalDate date, String format) {
        if (null == date) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String dateStr = date.format(formatter);
        if (StringUtils.isBlank(dateStr)) {
            dateStr = format(date, formatter);
        }
        return dateStr;
    }

    /**
     * 日期偏移,根据field不同加不同值（偏移会修改传入的对象）
     *
     * @param time   {@link LocalDateTime}
     * @param number 偏移量，正数为向后偏移，负数为向前偏移
     * @param field  偏移单位，见{@link ChronoUnit}，不能为null
     * @return 偏移后的日期时间
     */
    public static LocalDateTime offset(LocalDateTime time, long number, TemporalUnit field) {
        return TemporalUtils.offset(time, number, field);
    }

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。
     * <p>
     * 返回结果为{@link Duration}对象，通过调用toXXX方法返回相差单位
     *
     * @param startTimeInclude 开始时间（包含）
     * @param endTimeExclude   结束时间（不包含）
     * @return 时间差 {@link Duration}对象
     * @see TemporalUtils#between(Temporal, Temporal)
     */
    public static Duration between(LocalDateTime startTimeInclude, LocalDateTime endTimeExclude) {
        return TemporalUtils.between(startTimeInclude, endTimeExclude);
    }

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。
     * <p>
     * 返回结果为时间差的long值
     *
     * @param startTimeInclude 开始时间（包括）
     * @param endTimeExclude   结束时间（不包括）
     * @param unit             时间差单位
     * @return 时间差
     *      */
    public static long between(LocalDateTime startTimeInclude, LocalDateTime endTimeExclude, ChronoUnit unit) {
        return TemporalUtils.between(startTimeInclude, endTimeExclude, unit);
    }

    /**
     * 获取两个日期的表象时间差，如果结束时间早于开始时间，获取结果为负。
     * <p>
     * 比如2011年2月1日，和2021年8月11日，日相差了10天，月相差6月
     *
     * @param startTimeInclude 开始时间（包括）
     * @param endTimeExclude   结束时间（不包括）
     * @return 时间差
     *      */
    public static Period betweenPeriod(LocalDate startTimeInclude, LocalDate endTimeExclude) {
        return Period.between(startTimeInclude, endTimeExclude);
    }

    /**
     * 修改为一天的开始时间，例如：2020-02-02 00:00:00,000
     *
     * @param time 日期时间
     * @return 一天的开始时间
     */
    public static LocalDateTime beginOfDay(LocalDateTime time) {
        return time.with(LocalTime.MIN);
    }

    /**
     * 获取某一天结束
     *
     * @param dateStr 20120920
     * @return the end str 2012-09-20 23:59:59
     */
    public static String endOfDay(String dateStr) {
        LocalDate localDate = parseLocalDate(dateStr);
        return endOfDay(localDate);
    }

    /**
     * get end of a day
     *
     * @return the end str 2012-09-20 23:59:59
     */
    public static String endOfDay(LocalDate localDate) {

        LocalDateTime dateTime = LocalDateTime.of(localDate, LocalTime.MAX);
        return format(dateTime,YMD_HMS);
    }

    /**
     * 修改为一天的结束时间，例如：2020-02-02 23:59:59,999
     *
     * @param time 日期时间
     * @return 一天的结束时间
     */
    public static LocalDateTime endOfDay(LocalDateTime time) {
        return endOfDay(time, false);
    }

    /**
     * 修改为一天的结束时间，例如：
     * <ul>
     * <li>毫秒不归零：2020-02-02 23:59:59,999</li>
     * <li>毫秒归零：2020-02-02 23:59:59,000</li>
     * </ul>
     *
     * @param time                日期时间
     * @param truncateMillisecond 是否毫秒归零
     * @return 一天的结束时间
     *      */
    public static LocalDateTime endOfDay(LocalDateTime time, boolean truncateMillisecond) {
        if (truncateMillisecond) {
            return time.with(LocalTime.of(23, 59, 59));
        }
        return time.with(LocalTime.MAX);
    }

    /**
     * {@link TemporalAccessor}转换为 时间戳（从1970-01-01T00:00:00Z开始的毫秒数）
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     * @see TemporalAccessorUtils#toEpochMilli(TemporalAccessor)
     *      */
    public static long toEpochMilli(TemporalAccessor temporalAccessor) {
        return TemporalAccessorUtils.toEpochMilli(temporalAccessor);
    }

    /**
     * 是否为周末（周六或周日）
     *
     * @param localDateTime 判定的日期{@link LocalDateTime}
     * @return 是否为周末（周六或周日）
     *      */
    public static boolean isWeekend(LocalDateTime localDateTime) {
        return isWeekend(localDateTime.toLocalDate());
    }

    /**
     * 是否为周末（周六或周日）
     *
     * @param localDate 判定的日期{@link LocalDate}
     * @return 是否为周末（周六或周日）
     *      */
    public static boolean isWeekend(LocalDate localDate) {
        final DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return DayOfWeek.SATURDAY == dayOfWeek || DayOfWeek.SUNDAY == dayOfWeek;
    }

    /**
     * 获取{@link LocalDate}对应的星期值
     *
     * @param localDate 日期{@link LocalDate}
     * @return {@link Week}
     *      */
    public static Week dayOfWeek(LocalDate localDate) {
        return Week.of(localDate.getDayOfWeek());
    }

    /**
     * 检查两个时间段是否有时间重叠<br>
     * 重叠指两个时间段是否有交集
     *
     * @param realStartTime 第一个时间段的开始时间
     * @param realEndTime   第一个时间段的结束时间
     * @param startTime     第二个时间段的开始时间
     * @param endTime       第二个时间段的结束时间
     * @return true 表示时间有重合
     *      */
    public static boolean isOverlap(ChronoLocalDateTime<?> realStartTime, ChronoLocalDateTime<?> realEndTime,
                                    ChronoLocalDateTime<?> startTime, ChronoLocalDateTime<?> endTime) {

        // x>b||a>y 无交集
        // 则有交集的逻辑为 !(x>b||a>y)
        // 根据德摩根公式，可化简为 x<=b && a<=y
        return startTime.isBefore(realEndTime) && endTime.isAfter(realStartTime);
    }

    /**
     * 获得指定日期是所在年份的第几周，如：
     * <ul>
     * <li>如果一年的第一天是星期一，则第一周从第一天开始，没有零周</li>
     * <li>如果一年的第二天是星期一，则第一周从第二天开始，而第一天在零周</li>
     * <li>如果一年中的第4天是星期一，则第1周从第4周开始，第1至第3周在零周开始</li>
     * <li>如果一年中的第5天是星期一，则第二周从第5周开始，第1至第4周在第1周</li>
     * </ul>
     *
     * @param date 日期（{@link LocalDate} 或者 {@link LocalDateTime}等）
     * @return 所在年的第几周
     *      */
    public static int weekOfYear(TemporalAccessor date) {
        return TemporalAccessorUtils.get(date, WeekFields.ISO.weekOfYear());
    }

    /**
     * 比较两个日期是否为同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否为同一天
     *      */
    public static boolean isSameDay(final LocalDateTime date1, final LocalDateTime date2) {
        return date1 != null && date2 != null && isSameDay(date1.toLocalDate(), date2.toLocalDate());
    }

    /**
     * 比较两个日期是否为同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否为同一天
     *      */
    public static boolean isSameDay(final LocalDate date1, final LocalDate date2) {
        return date1 != null && date2 != null && date1.isEqual(date2);
    }

    /**
     * 当前日期是否在日期指定范围内<br>
     * 起始日期和结束日期可以互换
     *
     * @param date      被检查的日期
     * @param beginDate 起始日期（包含）
     * @param endDate   结束日期（包含）
     * @return 是否在范围内
     */
    public static boolean isIn(ChronoLocalDateTime<?> date, ChronoLocalDateTime<?> beginDate,
                               ChronoLocalDateTime<?> endDate) {
        return TemporalAccessorUtils.isIn(date, beginDate, endDate);
    }

    public static Date date(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Integer) {
            return date((int) o * SECOND_STAMP);
        } else if (o instanceof Long) {
            long l = (long) o;
            if (isMilli(l)) {
                return date(l);
            } else {
                return date(l * SECOND_STAMP);
            }
        } else if (o instanceof byte[] || o instanceof Byte[] || o instanceof short[] || o instanceof Short[]
                || o instanceof int[] || o instanceof Integer[] || o instanceof long[] || o instanceof Long[]
                || o instanceof float[] || o instanceof Float[] || o instanceof double[] || o instanceof Double[]
                || o instanceof char[] || o instanceof Character[] || o instanceof String[]) {
            try {
                int[] analysis = ConvertUtils.convert(int[].class,o);
                if (analysis.length == 3) {
                    return build(analysis[0], analysis[1], analysis[2]);
                } else if (analysis.length == 6) {
                    return build(analysis[0], analysis[1], analysis[2], analysis[3], analysis[4], analysis[5]);
                } else if (analysis.length == 7) {
                    return build(analysis[0], analysis[1], analysis[2], analysis[3], analysis[4], analysis[5], analysis[6]);
                }
            } catch (Exception e) {
                return null;
            }
        } else if (o instanceof Date) {
            return (Date) o;
        } else if (o instanceof Calendar) {
            return ((Calendar) o).getTime();
        } else if (o instanceof String) {
            return parse((String) o);
        } else if (o instanceof LocalDate) {
            return date((LocalDate) o);
        } else if (o instanceof LocalDateTime) {
            return date((LocalDateTime) o);
        } else if (o instanceof Instant) {
            return Date.from((Instant) o);
        }
        return null;
    }


    public static Calendar calendar() {
        return Calendar.getInstance();
    }

    public static Calendar calendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    public static Calendar calendar(long milliSecond) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliSecond);
        return c;
    }

    /**
     * 获取日历
     *
     * @param o o
     * @return 日历
     */
    public static Calendar calendar(Object o) {
        Date date = date(o);
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    /**
     * 解构时间
     *
     * @param d 时间
     * @return [y, M, d, H, m, s, S]
     */
    public static int[] analysis(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return new int[]{
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE), c.get(Calendar.SECOND), c.get(Calendar.MILLISECOND)
        };
    }

    public static Date build(int year, int month, int day) {
        return build(year, month, day, 0, 0, 0, 0);
    }

    public static Date build(int year, int month, int day, int h, int m, int s) {
        return build(year, month, day, h, m, s, 0);
    }

    /**
     * 构建时间
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @param h     时
     * @param m     分
     * @param s     秒
     * @param ms    毫秒
     * @return 时间
     */
    public static Date build(int year, int month, int day, int h, int m, int s, int ms) {
        Calendar c = calendar();
        c.set(year, month - 1, day, h, m, s);
        if (ms != 0) {
            c.set(Calendar.MILLISECOND, ms);
        }
        return c.getTime();
    }

    public static String now() {
        return FastDateFormat.getInstance(YMD_HMS).format(date());
    }

    /**
     * 获取当前时间
     *
     * @param pattern 格式
     * @return 当前时间
     */
    public static String now(String pattern) {
        return FastDateFormat.getInstance(pattern).format(date());
    }

    public static Date clearHms() {
        return clearHms(calendar());
    }

    public static Date clearHms(Date d) {
        return clearHms(calendar(d));
    }

    /**
     * 日期的 00:00:00
     * 清除时分秒
     *
     * @param c 时间
     * @return 清除后的时间
     */
    public static Date clearHms(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date dayEnd() {
        return dayEnd(calendar());
    }

    public static Date dayEnd(Date d) {
        return dayEnd(calendar(d));
    }

    /**
     * 日期的 23:59:59
     *
     * @param c 时间
     * @return 23:59:59
     */
    public static Date dayEnd(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }


    public static Date[] getIncrementDayDates(int incr, int times) {
        return getIncrementDates(null, Calendar.DAY_OF_MONTH, incr, times);
    }

    /**
     * 获取自增时间
     *
     * @param d     开始时间
     * @param incr  自增时间(+ -)
     * @param times 自增次数
     * @return 时间
     */
    public static Date[] getIncrementDayDates(Date d, int incr, int times) {
        return getIncrementDates(d, Calendar.DAY_OF_MONTH, incr, times);
    }

    public static Date[] getIncrementHourDates(int incr, int times) {
        return getIncrementDates(null, Calendar.HOUR_OF_DAY, incr, times);
    }

    /**
     * 获取自增时间
     *
     * @param d     开始时间
     * @param incr  自增时间(+ -)
     * @param times 自增次数
     * @return 时间
     */
    public static Date[] getIncrementHourDates(Date d, int incr, int times) {
        return getIncrementDates(d, Calendar.HOUR_OF_DAY, incr, times);
    }

    public static Date[] getIncrementDates(int field, int incr, int times) {
        return getIncrementDates(null, field, incr, times);
    }

    /**
     * 获取自增时间
     *
     * @param d     开始时间
     * @param field 时间字段
     * @param incr  自增时间(+ -)
     * @param times 自增次数
     * @return 时间
     */
    public static Date[] getIncrementDates(Date d, int field, int incr, int times) {
        Date[] dates = new Date[times];
        Calendar c = calendar();
        if (d != null) {
            c.setTime(d);
        }
        dates[0] = c.getTime();
        for (int i = 0; i < times - 1; i++) {
            c.add(field, incr);
            dates[i + 1] = c.getTime();
        }
        return dates;
    }


    public static String format(Date d, Locale locale) {
        return FastDateFormat.getInstance(YMD_HMS, locale).format(d);
    }

    public static String format(Date d, String pattern, Locale locale) {
        return d == null ? null : FastDateFormat.getInstance(pattern, locale).format(d);
    }

    public static String format(Date d, TimeZone timeZone) {
        return FastDateFormat.getInstance(YMD_HMS, timeZone).format(d);
    }

    public static String format(Date d, String pattern, TimeZone timeZone) {
        return d == null ? null : FastDateFormat.getInstance(pattern, timeZone).format(d);
    }

    public static String format(Date d, TimeZone timeZone, Locale locale) {
        return FastDateFormat.getInstance(YMD_HMS, timeZone, locale).format(d);
    }

    /**
     * 格式化
     *
     * @param d        时间
     * @param pattern  格式
     * @param timeZone 时区
     * @param locale   地区
     * @return ignore
     */
    public static String format(Date d, String pattern, TimeZone timeZone, Locale locale) {
        return d == null ? null : FastDateFormat.getInstance(pattern, timeZone, locale).format(d);
    }


    public static Date parse(String d, String pattern, Locale locale) {
        try {
            return FastDateFormat.getInstance(pattern, locale).parse(d);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parse(String d, Locale locale, String... patterns) {
        for (String pattern : patterns) {
            Date parse = parse(d, pattern, locale);
            if (parse != null) {
                return parse;
            }
        }
        return null;
    }

    public static Date parse(String d, String pattern, TimeZone timeZone) {
        try {
            return FastDateFormat.getInstance(pattern, timeZone).parse(d);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parse(String d, TimeZone timeZone, String... patterns) {
        for (String pattern : patterns) {
            Date parse = parse(d, pattern, timeZone);
            if (parse != null) {
                return parse;
            }
        }
        return null;
    }

    public static Date parse(String d, String pattern, TimeZone timeZone, Locale locale) {
        try {
            return FastDateFormat.getInstance(pattern, timeZone, locale).parse(d);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 日期转化
     *
     * @param d        日期
     * @param timeZone 时区
     * @param locale   地区
     * @param patterns 格式
     * @return 日期
     */
    public static Date parse(String d, TimeZone timeZone, Locale locale, String... patterns) {
        for (String pattern : patterns) {
            Date parse = parse(d, pattern, timeZone, locale);
            if (parse != null) {
                return parse;
            }
        }
        return null;
    }

    /**
     * 将GMT时间转为date
     *
     * @param s GMT时间
     * @return date
     */
    public static Date world(String s) {
        return parse(s, WORLD, Locale.US);
    }

    public static String hourType() {
        return hourType(calendar());
    }

    /**
     * 时间段
     *
     * @param d d
     * @return 时间段
     */
    public static String hourType(Date d) {
        return hourType(calendar(d));
    }

    /**
     * 时间段
     *
     * @param c c
     * @return 时间段
     */
    public static String hourType(Calendar c) {
        return hourType(c.get(Calendar.HOUR_OF_DAY));
    }


    public static String interval(Date date1, Date date2) {
        return interval(intervalMs(date1, date2), false, null, null, null, null);
    }

    public static String interval(Date date1, Date date2, boolean full) {
        return interval(intervalMs(date1, date2), full, null, null, null, null);
    }

    public static String interval(Date date1, Date date2, String day, String hour, String minute, String second) {
        return interval(intervalMs(date1, date2), false, day, hour, minute, second);
    }

    /**
     * 时差
     *
     * @param date1  时间1
     * @param date2  时间2
     * @param full   为0是否显示 0天0时0分1秒 : 1秒
     * @param day    天显示的文字
     * @param hour   时显示的文字
     * @param minute 分显示的文字
     * @param second 秒显示的文字
     * @return 时差
     */
    public static String interval(Date date1, Date date2, boolean full, String day, String hour, String minute, String second) {
        return interval(intervalMs(date1, date2), full, day, hour, minute, second);
    }

    /**
     * 时差毫秒
     *
     * @param date1 时间1
     * @param date2 时间2
     * @return 时差
     */
    public static long intervalMs(Date date1, Date date2) {
        return Math.abs(date1.getTime() - date2.getTime());
    }

    /**
     * 时差解构
     *
     * @param date1 时间1
     * @param date2 时间2
     * @return 时差 [天,时,分,秒]
     */
    public static long[] intervalAnalysis(Date date1, Date date2) {
        return intervalAnalysis(intervalMs(date1, date2));
    }

    /**
     * 转换格式
     * <p>
     * 使用格式推断
     *
     * @param d 时间
     * @param n 新格式
     * @return 日期
     * @see #parse(CharSequence)
     * @see #PARSE_PATTERN_GROUP1
     * @see #PARSE_PATTERN_GROUP2
     * @see #PARSE_PATTERN_GROUP3
     * @see #WORLD
     */
    public static String convert(String d, String n) {
        Date parse = parse(d);
        if (parse == null) {
            return StringUtils.EMPTY;
        }
        return format(parse, n);
    }

    /**
     * 转换格式
     *
     * @param d      时间
     * @param before 原格式
     * @param after  新格式
     * @return 日期
     */
    public static String convert(String d, String before, String after) {
        return format(parse(d, before), after);
    }

    public static boolean isLeapYear() {
        return isLeapYear(calendar());
    }

    public static boolean isLeapYear(Date d) {
        return isLeapYear(calendar(d));
    }

    /**
     * 是否为闰年
     *
     * @param c calendar
     * @return true 闰年
     */
    public static boolean isLeapYear(Calendar c) {
        return isLeapYear(c.get(Calendar.YEAR));
    }

    public static int getMonthLastDay() {
        return getMonthLastDay(calendar());
    }

    public static int getMonthLastDay(Date d) {
        return getMonthLastDay(calendar(d));
    }

    /**
     * 获得月份的最后一天
     *
     * @param c calendar
     * @return 最后一天
     */
    public static int getMonthLastDay(Calendar c) {
        return getMonthLastDay(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
    }

    public static int getQuarter() {
        return getQuarter(calendar());
    }

    public static int getQuarter(Date d) {
        return getQuarter(calendar(d));
    }

    /**
     * 获取季度
     *
     * @param c calendar
     * @return 季度
     */
    public static int getQuarter(Calendar c) {
        return getQuarter(c.get(Calendar.MONTH) + 1);
    }

    public static boolean isAm() {
        return isAm(calendar());
    }

    public static boolean isAm(Date d) {
        return isAm(calendar(d));
    }

    /**
     * 是否为上午
     *
     * @param c c
     * @return ignore
     */
    public static boolean isAm(Calendar c) {
        return Calendar.AM == c.get(Calendar.AM_PM);
    }

    public static boolean isPm() {
        return isPm(calendar());
    }

    public static boolean isPm(Date d) {
        return isPm(calendar(d));
    }

    /**
     * 是否为下午
     *
     * @param c c
     * @return ignore
     */
    public static boolean isPm(Calendar c) {
        return Calendar.PM == c.get(Calendar.AM_PM);
    }

    /**
     * 判断时间是否在未来
     *
     * @param date date
     * @return true 在未来
     */
    public static boolean inFuture(Date date) {
        return inFuture(date.getTime());
    }

    // -------------------- stream --------------------

    public static DateStream stream() {
        return new DateStream(date());
    }

    public static DateStream stream(TimeZone timeZone) {
        Valid.notNull(timeZone);
        return new DateStream(date(), timeZone);
    }

    public static DateStream stream(Locale locale) {
        Valid.notNull(locale);
        return new DateStream(date(), locale);
    }

    public static DateStream stream(TimeZone timeZone, Locale locale) {
        Valid.notNull(timeZone);
        Valid.notNull(locale);
        return new DateStream(date(), timeZone, locale);
    }

    public static DateStream stream(Date date) {
        Valid.notNull(date);
        return new DateStream(date);
    }

    public static DateStream stream(Date date, TimeZone timeZone) {
        Valid.notNull(date);
        Valid.notNull(timeZone);
        return new DateStream(date, timeZone);
    }

    public static DateStream stream(Date date, Locale locale) {
        Valid.notNull(date);
        Valid.notNull(locale);
        return new DateStream(date, locale);
    }

    /**
     * 日期流
     *
     * @param date     时间
     * @param timeZone 时区
     * @param locale   地区
     * @return 流
     */
    public static DateStream stream(Date date, TimeZone timeZone, Locale locale) {
        Valid.notNull(date);
        Valid.notNull(timeZone);
        Valid.notNull(locale);
        return new DateStream(date, timeZone, locale);
    }

    /**
     * 日期流
     *
     * @param c 日历
     * @return 流
     */
    public static DateStream stream(Calendar c) {
        Valid.notNull(c);
        return new DateStream(c);
    }

    public static final String WORLD = "EEE MMM dd HH:mm:ss z yyyy";
    public static final String YM = "yyyy-MM";
    public static final String YMD = "yyyy-MM-dd";
    public static final String YMD_HM = "yyyy-MM-dd HH:mm";
    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YMD_HMSS = "yyyy-MM-dd HH:mm:ss SSS";
    public static final String HMS = "HH:mm:ss";
    public static final String HMSS = "HH:mm:ss SSS";
    public static final String YM1 = "yyyy/MM";
    public static final String YMD1 = "yyyy/MM/dd";
    public static final String YMD_HM1 = "yyyy/MM/dd HH/mm";
    public static final String YMD_HMS1 = "yyyy/MM/dd HH/mm/ss";
    public static final String YMD_HMSS1 = "yyyy/MM/dd HH/mm/ss SSS";
    public static final String YM2 = "yyyyMM";
    public static final String YMD2 = "yyyyMMdd";
    public static final String YMD_HM2 = "yyyyMMddHHmm";
    public static final String YMD_HMS2 = "yyyyMMddHHmmss";
    public static final String YMD_HMSS2 = "yyyyMMddHHmmssSSS";
    public static final String[] PARSE_PATTERN_GROUP1 = {YMD_HMS, YMD, YM, YMD_HM, YMD_HMSS};
    public static final String[] PARSE_PATTERN_GROUP2 = {YMD_HMS1, YMD1, YM1, YMD_HM1, YMD_HMSS1};
    public static final String[] PARSE_PATTERN_GROUP3 = {YMD_HMS2, YMD2, YM2, YMD_HM2, YMD_HMSS2};

    public static final long WEEK_STAMP = 604800000L;
    public static final long YEAR_STAMP = 31536000000L;
    public static final long MONTH_STAMP = 2592000000L;
    public static final long DAY_STAMP = 86400000L;
    public static final long HOUR_STAMP = 3600000L;
    public static final long MINUTE_STAMP = 60000L;
    public static final long SECOND_STAMP = 1000L;

    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.of("+8");
    public static final ZoneOffset DEFAULT_ZERO_ZONE_OFFSET = ZoneOffset.ofHours(0);

    private static final String PAD_HMS = "%04d-%02d-%02d";
    private static final String PAD_YMD_HMS = "%04d-%02d-%02d %02d:%02d:%02d";
    private static final String PAD_YMD_HMSS = "%04d-%02d-%02d %02d:%02d:%02d %03d";

    /**
     * 判断对象是否为时间格式 不包含String
     *
     * @param c c
     * @return ignore
     */
    public static boolean isDateClass(Class<?> c) {
        return c == Long.TYPE || c == Long.class
                || c == Integer.TYPE || c == Integer.class
                || c == Date.class || c == Calendar.class
                || c == LocalDate.class || c == LocalDateTime.class || c == Instant.class;
    }

    /**
     * 是否是时间戳毫秒
     *
     * @param l ignore
     * @return true 是
     */
    public static boolean isMilli(long l) {
        return l > 1000000000000L;
    }

    /**
     * 时间戳毫秒转时间戳秒
     *
     * @param timestamp 时间戳毫秒
     * @return 时间戳秒
     */
    public static long getSecondTime(long timestamp) {
        return timestamp / SECOND_STAMP;
    }

    /**
     * 时间段
     *
     * @param hour 小时
     * @return 时间段
     */
    public static String hourType(int hour) {
        if (hour >= 0 && hour < 6) {
            return "凌晨";
        } else if (hour >= 6 && hour < 11) {
            return "上午";
        } else if (hour >= 11 && hour < 13) {
            return "中午";
        } else if (hour >= 13 && hour < 18) {
            return "下午";
        } else if (hour >= 18 && hour < 20) {
            return "傍晚";
        } else if (hour >= 20 && hour < 22) {
            return "晚上";
        } else if (hour >= 22 && hour <= 23) {
            return "深夜";
        }
        return "未知";
    }

    /**
     * 判断时间是否在未来
     *
     * @param date date
     * @return true 在未来
     */
    public static boolean inFuture(long date) {
        return date > System.currentTimeMillis();
    }

    public static String interval(long ms) {
        return interval(ms, false, null, null, null, null);
    }

    public static String interval(long ms, boolean full) {
        return interval(ms, full, null, null, null, null);
    }

    /**
     * 时差
     *
     * @param ms     时间戳毫秒
     * @param full   为0是否显示 0天0时0分1秒 : 1秒
     * @param day    天显示的文字
     * @param hour   时显示的文字
     * @param minute 分显示的文字
     * @param second 秒显示的文字
     * @return 时差
     */
    public static String interval(long ms, boolean full, String day, String hour, String minute, String second) {
        long[] analysis = intervalAnalysis(ms);
        if (full) {
            return analysis[0] + (day == null ? "天" : day)
                    + analysis[1] + (hour == null ? "时" : hour)
                    + analysis[2] + (minute == null ? "分" : minute)
                    + analysis[3] + (second == null ? "秒" : second);
        }
        boolean ay = true, ah = true, am = true;
        if (analysis[0] == 0) {
            ay = false;
        }
        if (!ay && analysis[1] == 0) {
            ah = false;
        }
        if (!ah && analysis[2] == 0) {
            am = false;
        }
        StringBuilder sb = new StringBuilder();
        if (ay) {
            sb.append(analysis[0]).append(day == null ? "天" : day);
        }
        if (ah) {
            sb.append(analysis[1]).append(hour == null ? "时" : hour);
        }
        if (am) {
            sb.append(analysis[2]).append(minute == null ? "分" : minute);
        }
        sb.append(analysis[3]).append(second == null ? "秒" : second);
        return sb.toString();
    }

    /**
     * 时差解构
     *
     * @param ms 时间戳毫秒
     * @return 时差 [天,时,分,秒]
     */
    public static long[] intervalAnalysis(long ms) {
        long distanceDay = ms / DAY_STAMP;
        long distanceHour = ms % DAY_STAMP / HOUR_STAMP;
        long distanceMinute = ms % DAY_STAMP % HOUR_STAMP / MINUTE_STAMP;
        long distanceSecond = ms % DAY_STAMP % HOUR_STAMP % MINUTE_STAMP / SECOND_STAMP;
        if (distanceSecond == 0 && ms != 0) {
            distanceSecond = 1;
        }
        return new long[]{distanceDay, distanceHour, distanceMinute, distanceSecond};
    }


    /**
     * 获得月份的最后一天
     *
     * @param year  年份
     * @param month 月份
     * @return 最后一天
     */
    public static int getMonthLastDay(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                return isLeapYear(year) ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                throw new IllegalArgumentException("month must >= 1 and <= 12");
        }
    }

    /**
     * 获取季度
     *
     * @param month month
     * @return 季度
     */
    public static int getQuarter(int month) {
        switch (month) {
            case 1:
            case 2:
            case 3:
                return 1;
            case 4:
            case 5:
            case 6:
                return 2;
            case 7:
            case 8:
            case 9:
                return 3;
            case 10:
            case 11:
            case 12:
                return 4;
            default:
                throw new IllegalArgumentException("month must >= 1 and <= 12");
        }
    }

    public static String pad(int year, int month, int day) {
        return String.format(PAD_HMS, year, month, day);
    }

    public static String pad(int year, int month, int day, int hour, int minute, int second) {
        return String.format(PAD_YMD_HMS, year, month, day, hour, minute, second);
    }

    /**
     * 填充时间
     *
     * @param year   year
     * @param month  month
     * @param day    day
     * @param hour   hour
     * @param minute minute
     * @param second second
     * @param milli  milli
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String pad(int year, int month, int day, int hour, int minute, int second, int milli) {
        return String.format(PAD_YMD_HMSS, year, month, day, hour, minute, second, milli);
    }


    private static final Map<String, DateTimeFormatter> DATE_TIME_FORMAT_CONTAINER = new ConcurrentReferenceHashMap<>(16, ReferenceType.WEAK,ReferenceType.WEAK);


    static {
        DATE_TIME_FORMAT_CONTAINER.put(YMD, DateTimeFormatter.ofPattern(YMD));
        DATE_TIME_FORMAT_CONTAINER.put(YMD_HMS, DateTimeFormatter.ofPattern(YMD_HMS));
    }

    /**
     * 将对象转化为 LocalDateTime
     *
     * @param o 对象
     * @return LocalDateTime
     */
    public static LocalDateTime localDateTime(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof LocalDateTime) {
            return (LocalDateTime) o;
        }
        Date date = date(o);
        if (date == null) {
            return null;
        }
        return localDateTime(date);
    }

    /**
     * 将对象转化为 LocalDate
     *
     * @param o 对象
     * @return LocalDate
     */
    public static LocalDate localDate(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof LocalDate) {
            return (LocalDate) o;
        }
        Date date = date(o);
        if (date == null) {
            return null;
        }
        return localDate(date);
    }

    public static Instant instant() {
        return Instant.now();
    }

    public static Instant instant(Date date) {
        return Instant.ofEpochMilli(date.getTime());
    }

    /**
     * 获取 Instant
     *
     * @param ms 时间戳毫秒
     * @return Instant
     */
    public static Instant instant(long ms) {
        // ms是时间戳毫秒
        return Instant.ofEpochMilli(ms);
    }

    public static Instant instant(LocalDate d) {
        return d.atStartOfDay().toInstant(DEFAULT_ZERO_ZONE_OFFSET);
    }

    public static Instant instant(LocalDate d, ZoneOffset offset) {
        return d.atStartOfDay().toInstant(offset);
    }

    public static Instant instant(LocalDateTime dt) {
        return dt.toInstant(DEFAULT_ZONE_OFFSET);
    }

    public static Instant instant(LocalDateTime dt, ZoneOffset offset) {
        return dt.toInstant(offset);
    }

    public static long timestamp() {
        return System.currentTimeMillis() / SECOND_STAMP;
    }

    public static long timestamp(Date date) {
        return date.getTime() / SECOND_STAMP;
    }

    public static long timestamp(Instant instant) {
        return instant.getEpochSecond();
    }

    public static long timestamp(LocalDate d) {
        return d.atStartOfDay().toInstant(DEFAULT_ZERO_ZONE_OFFSET).getEpochSecond();
    }

    public static long timestamp(LocalDate d, ZoneOffset offset) {
        return d.atStartOfDay().toInstant(offset).toEpochMilli() / SECOND_STAMP;
    }

    public static long timestamp(LocalDateTime d) {
        return d.toEpochSecond(DEFAULT_ZONE_OFFSET);
    }

    public static long timestamp(LocalDateTime d, ZoneOffset offset) {
        return d.toEpochSecond(offset);
    }

    public static Date date(Instant instant) {
        return Date.from(instant);
    }

    public static Date date(LocalDate d) {
        return Date.from(d.atStartOfDay().toInstant(DEFAULT_ZONE_OFFSET));
    }

    public static Date date(LocalDate d, ZoneOffset offset) {
        return Date.from(d.atStartOfDay().toInstant(offset));
    }

    public static Date date(LocalDateTime d) {
        return Date.from(d.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date date(LocalDateTime d, ZoneOffset offset) {
        return Date.from(d.atZone(offset).toInstant());
    }

    public static LocalDate localDate() {
        return LocalDate.now();
    }

    public static LocalDate localDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    public static LocalDate localDate(long ms) {
        return LocalDateTime.ofEpochSecond(ms / SECOND_STAMP, 0, DEFAULT_ZONE_OFFSET).toLocalDate();
    }

    public static LocalDate localDate(long ms, ZoneOffset offset) {
        return LocalDateTime.ofEpochSecond(ms / SECOND_STAMP, 0, offset).toLocalDate();
    }

    public static LocalDate localDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID).toLocalDate();
    }

    public static LocalDate localDate(Date date, ZoneId zoneId) {
        return LocalDateTime.ofInstant(date.toInstant(), zoneId).toLocalDate();
    }

    public static LocalDate localDate(Instant instant) {
        return LocalDateTime.ofInstant(instant, DEFAULT_ZONE_ID).toLocalDate();
    }

    public static LocalDate localDate(Instant instant, ZoneId zoneId) {
        return LocalDateTime.ofInstant(instant, zoneId).toLocalDate();
    }

    public static LocalDate localDate(LocalDateTime d) {
        return d.toLocalDate();
    }

    public static LocalTime localTime() {
        return LocalTime.now();
    }

    public static LocalTime localTime(int h, int m, int s) {
        return LocalTime.of(h, m, s);
    }

    public static LocalTime localTime(int h, int m, int s, int nano) {
        return LocalTime.of(h, m, s, nano);
    }

    public static LocalTime localTime(long ms, int nano) {
        return LocalDateTime.ofEpochSecond(ms / SECOND_STAMP, nano, DEFAULT_ZONE_OFFSET).toLocalTime();
    }

    public static LocalTime localTime(long ms, int nano, ZoneOffset offset) {
        return LocalDateTime.ofEpochSecond(ms / SECOND_STAMP, nano, offset).toLocalTime();
    }

    public static LocalTime localTime(long ms) {
        return LocalDateTime.ofEpochSecond(ms / SECOND_STAMP, 0, DEFAULT_ZONE_OFFSET).toLocalTime();
    }

    public static LocalTime localTime(long ms, ZoneOffset offset) {
        return LocalDateTime.ofEpochSecond(ms / SECOND_STAMP, 0, offset).toLocalTime();
    }

    public static LocalTime localTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID).toLocalTime();
    }

    public static LocalTime localTime(Date date, ZoneId zoneId) {
        return LocalDateTime.ofInstant(date.toInstant(), zoneId).toLocalTime();
    }

    public static LocalTime localTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, DEFAULT_ZONE_ID).toLocalTime();
    }

    public static LocalTime localTime(Instant instant, ZoneId zoneId) {
        return LocalDateTime.ofInstant(instant, zoneId).toLocalTime();
    }

    public static LocalTime localTime(LocalDateTime d) {
        return d.toLocalTime();
    }

    public static LocalDateTime localDateTime() {
        return LocalDateTime.now();
    }

    public static LocalDateTime localDateTime(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0, 0);
    }

    public static LocalDateTime localDateTime(int year, int month, int day, int h, int m, int s) {
        return LocalDateTime.of(year, month, day, h, m, s);
    }

    public static LocalDateTime localDateTime(int year, int month, int day, int h, int m, int s, int ms) {
        return LocalDateTime.of(year, month, day, h, m, s, ms);
    }

    public static LocalDateTime localDateTime(long ms) {
        return LocalDateTime.ofEpochSecond(ms / SECOND_STAMP, 0, DEFAULT_ZONE_OFFSET);
    }

    public static LocalDateTime localDateTime(long ms, ZoneOffset offset) {
        return LocalDateTime.ofEpochSecond(ms / SECOND_STAMP, 0, offset);
    }

    public static LocalDateTime localDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
    }

    public static LocalDateTime localDateTime(Date date, ZoneId zoneId) {
        return LocalDateTime.ofInstant(date.toInstant(), zoneId);
    }

    public static LocalDateTime localDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, DEFAULT_ZONE_ID);
    }

    public static LocalDateTime localDateTime(Instant instant, ZoneId zoneId) {
        return LocalDateTime.ofInstant(instant, zoneId);
    }

    public static LocalDateTime localDateTime(LocalDate d) {
        return d.atStartOfDay();
    }

    public static LocalDateTime localDateTime(LocalDate d, LocalTime t) {
        return LocalDateTime.of(d, t);
    }

    public static LocalDateTime clearHms(LocalDateTime d) {
        return LocalDateTime.of(d.toLocalDate(), LocalTime.of(0, 0, 0, 0));
    }

    public static LocalDateTime clearDay(LocalDateTime d) {
        return d.withDayOfMonth(1);
    }

    public static LocalDateTime clearDayHms(LocalDateTime d) {
        return LocalDateTime.of(d.toLocalDate().withDayOfMonth(1), LocalTime.of(0, 0, 0, 0));
    }

    public static String format(TemporalAccessor d) {
        String date = null;
        try {
            if (d instanceof LocalDate) {
                date = format(d, YMD);
            } else if (d instanceof LocalDateTime) {
                date = format(d, YMD_HMS);
            } else {
                date = format(d, YMD_HMS);
            }
        } catch (Exception ignored) {
        }
        if (null == date) {
            date = format(date(d));
        }
        return date;
    }

    /**
     * 格式化时间
     *
     * @param d       时间
     * @param pattern pattern
     * @return 格式化
     */
    public static String format(TemporalAccessor d, String pattern) {
        try {
            DateTimeFormatter formatter = DATE_TIME_FORMAT_CONTAINER.get(pattern);
            if (formatter == null) {
                formatter = DateTimeFormatter.ofPattern(pattern);
                DATE_TIME_FORMAT_CONTAINER.put(pattern, formatter);
            }
            return formatter.format(d);
        } catch (Exception e) {
            return null;
        }
    }


}
