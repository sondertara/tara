package com.sondertara.common.util;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.lang.Assert;
import com.sondertara.common.regex.PatternPool;
import com.sondertara.common.time.CalendarUtils;
import com.sondertara.common.time.DateBetween;
import com.sondertara.common.time.DateField;
import com.sondertara.common.time.DatePattern;
import com.sondertara.common.time.DateTime;
import com.sondertara.common.time.Quarter;
import com.sondertara.common.time.TemporalAccessorUtils;
import com.sondertara.common.time.TemporalUtils;
import com.sondertara.common.time.Week;
import com.sondertara.common.time.Zodiac;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.DatePrinter;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
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
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * date util
 *
 * @author huangxiaohu
 * @since 2019-03-19 16:25
 */
public class LocalDateTimeUtils extends CalendarUtils {

    private static final Logger log = LoggerFactory.getLogger(LocalDateTimeUtils.class);

    public static final String DATE_TIME_FORMATTER = DatePattern.NORM_DATETIME_PATTERN;
    public static final String DATE_TIME_MILLS_FORMATTER = DatePattern.NORM_DATETIME_MS_PATTERN;
    public static final String DATE_FORMATTER = DatePattern.NORM_DATE_PATTERN;

    /**
     * java.util.Date EEE MMM zzz 缩写数组
     */
    private final static String[] wtb = { "sun", "mon", "tue", "wed", "thu", "fri", "sat", "jan", "feb", "mar", "apr",
            "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec", "gmt", "ut", "utc", "est", "edt", "cst", "cdt",
            "mst", "mdt", "pst", "pdt" };

    /**
     * 常用的UTC时间格式
     */
    private final static String[] FREQUENTLEY_USED_UTC_WITH_Z_DATE_FORMATS = new String[] { "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" };

    /**
     * 常用纯数字时间格式
     */
    private final static String[] FREQUENTLY_USED_NUMBER_DATE_FORMATS = new String[] { "yyyyMMddHHmmss",
            "yyyyMMddHHmmssSSS", "yyyyMMdd", "yyyyMMss", "HHmmss" };

    /**
     * CST格式
     */
    private final static String[] FREQUENTLY_USED_CST_DATE_FORMATS = new String[] { "EEE, dd MMM yyyy HH:mm:ss z",
            "EEE MMM dd HH:mm:ss zzz yyyy" };

    /**
     * 常用的时间格式
     */
    private final static String[] FREQUENTLY_USED_DATE_FORMATS = new String[] { "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss", "yyyy年MM月dd日 HH时mm分ss秒", "yyyy-MM-dd", "yyyy/MM/dd",
            "yyyy.MM.dd", "HH:mm:ss", "HH时mm分ss秒", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss.SSS" };

    /**
     * 当前时间，转换为{@link DateTime}对象
     *
     * @return 当前时间
     */
    public static DateTime date() {
        return new DateTime();
    }

    /**
     * 当前时间，转换为{@link DateTime}对象，忽略毫秒部分
     *
     * @return 当前时间
     * @since 4.6.2
     */
    public static DateTime dateSecond() {
        return beginOfSecond(date());
    }

    /**
     * {@link Date}类型时间转为{@link DateTime}<br>
     * 如果date本身为DateTime对象，则返回强转后的对象，否则新建一个DateTime对象
     *
     * @param date Long类型Date（Unix时间戳）
     * @return 时间对象
     * @since 3.0.7
     */
    public static DateTime date(Date date) {
        if (date instanceof DateTime) {
            return (DateTime) date;
        }
        return dateNew(date);
    }

    /**
     * 根据已有{@link Date} 产生新的{@link DateTime}对象
     *
     * @param date Date对象
     * @return {@link DateTime}对象
     * @since 4.3.1
     */
    public static DateTime dateNew(Date date) {
        return new DateTime(date);
    }

    /**
     * Long类型时间转为{@link DateTime}<br>
     * 只支持毫秒级别时间戳，如果需要秒级别时间戳，请自行×1000
     *
     * @param date Long类型Date（Unix时间戳）
     * @return 时间对象
     */
    public static DateTime date(long date) {
        return new DateTime(date);
    }

    /**
     * {@link Calendar}类型时间转为{@link DateTime}<br>
     * 始终根据已有{@link Calendar} 产生新的{@link DateTime}对象
     *
     * @param calendar {@link Calendar}
     * @return 时间对象
     */
    public static DateTime date(Calendar calendar) {
        return new DateTime(calendar);
    }

    /**
     * {@link TemporalAccessor}类型时间转为{@link DateTime}<br>
     * 始终根据已有{@link TemporalAccessor} 产生新的{@link DateTime}对象
     *
     * @param temporalAccessor {@link TemporalAccessor},常用子类： {@link LocalDateTime}、
     *                         LocalDate
     * @return 时间对象
     * @since 5.0.0
     */
    public static DateTime date(TemporalAccessor temporalAccessor) {
        return new DateTime(temporalAccessor);
    }

    /**
     * 获得指定日期所属季度，从1开始计数
     *
     * @param date 日期
     * @return 第几个季度
     * @since 4.1.0
     */
    public static int quarter(Date date) {
        return DateTime.of(date).quarter();
    }

    /**
     * 获得指定日期所属季度
     *
     * @param date 日期
     * @return 第几个季度枚举
     * @since 4.1.0
     */
    public static Quarter quarterEnum(Date date) {
        return DateTime.of(date).quarterEnum();
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
     * @see DateTime#setFirstDayOfWeek(Week)
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
     * @since 5.7.6
     */
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
     * 格式：[20131]表示2013年第一季度
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
     * @param format {@link DatePrinter} 或 {@link FastDateFormat}
     *               {@link DatePattern#NORM_DATETIME_FORMAT}
     * @return 格式化后的字符串
     */
    public static String format(Date date, DatePrinter format) {
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
     * @since 5.0.0
     */
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
     * 格式化日期时间<br>
     * 格式 yyyy-MM-dd HH:mm:ss
     *
     * @param date 被格式化的日期
     * @return 格式化后的日期
     */
    public static String formatDateTime(Date date) {
        if (null == date) {
            return null;
        }
        return DatePattern.NORM_DATETIME_FORMAT.format(date);
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
    public static DateTime parse(CharSequence dateStr, DateFormat dateFormat) {
        return new DateTime(dateStr, dateFormat);
    }

    /**
     * 构建DateTime对象
     *
     * @param dateStr Date字符串
     * @param parser  格式化器,{@link FastDateFormat}
     * @return DateTime对象
     */
    public static DateTime parse(CharSequence dateStr, DateParser parser) {
        return new DateTime(dateStr, parser);
    }

    /**
     * 构建DateTime对象
     *
     * @param dateStr Date字符串
     * @param parser  格式化器,{@link FastDateFormat}
     * @param lenient 是否宽容模式
     * @return DateTime对象
     * @since 5.7.14
     */
    public static DateTime parse(CharSequence dateStr, DateParser parser, boolean lenient) {
        return new DateTime(dateStr, parser, lenient);
    }

    /**
     * 将特定格式的日期转换为Date对象
     *
     * @param dateStr 特定格式的日期
     * @param format  格式，例如yyyy-MM-dd
     * @param locale  区域信息
     * @return 日期对象
     * @since 4.5.18
     */
    public static DateTime parse(CharSequence dateStr, String format, Locale locale) {
        return new DateTime(dateStr, newSimpleFormat(format, locale, null));
    }

    /**
     * 通过给定的日期格式解析日期时间字符串。<br>
     *
     * @param str           日期时间字符串，非空
     * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
     * @return 解析后的Date
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @since 5.3.11
     */
    public static DateTime parse(String str, String... parsePatterns) {
        return new DateTime(CalendarUtils.parseByPatterns(str, parsePatterns));
    }

    /**
     * 解析时间，格式HH:mm 或 HH:mm:ss，日期默认为今天
     *
     * @param timeString 标准形式的日期字符串
     * @return 日期对象
     * @since 3.1.1
     */
    public static DateTime parseTimeToday(String timeString) {
        timeString = com.sondertara.common.util.StringUtils.format("{} {}", today(), timeString);
        if (1 == com.sondertara.common.util.StringUtils.count(timeString, ':')) {
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
     * @since 4.1.14
     */
    public static DateTime parseUTC(String utcString) {
        if (utcString == null) {
            return null;
        }
        int length = utcString.length();
        if (com.sondertara.common.util.StringUtils.contains(utcString, 'Z')) {
            if (length == DatePattern.UTC_PATTERN.length() - 4) {
                // 格式类似：2018-09-13T05:34:31Z，-4表示减去4个单引号的长度
                return parse(utcString, DatePattern.UTC_FORMAT);
            }

            final int patternLength = DatePattern.UTC_MS_PATTERN.length();
            // 格式类似：2018-09-13T05:34:31.999Z，-4表示减去4个单引号的长度
            // -4 ~ -6范围表示匹配毫秒1~3位的情况
            if (length <= patternLength - 4 && length >= patternLength - 6) {
                return parse(utcString, DatePattern.UTC_MS_FORMAT);
            }
        } else if (com.sondertara.common.util.StringUtils.contains(utcString, '+')) {
            // 去除类似2019-06-01T19:45:43 +08:00加号前的空格
            utcString = utcString.replace(" +", "+");
            final String zoneOffset = com.sondertara.common.util.StringUtils.subAfter(utcString, '+', true);
            if (com.sondertara.common.util.StringUtils.isBlank(zoneOffset)) {
                throw ErrorUtils.illegalArgumentException("Invalid format: [{}]", utcString);
            }
            if (!com.sondertara.common.util.StringUtils.contains(zoneOffset, ':')) {
                // +0800转换为+08:00
                final String pre = com.sondertara.common.util.StringUtils.subBefore(utcString, '+', true);
                utcString = pre + "+" + zoneOffset.substring(0, 2) + ":" + "00";
            }

            if (com.sondertara.common.util.StringUtils.contains(utcString, CharUtils.DOT)) {
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
            } else if (com.sondertara.common.util.StringUtils.contains(utcString, CharUtils.DOT)) {
                // 可能为： 2021-03-17T06:31:33.99
                return parse(utcString, DatePattern.UTC_SIMPLE_MS_FORMAT);
            }
        }
        // 没有更多匹配的时间格式
        throw ErrorUtils.illegalStateException("No format fit for date String [{}] !", utcString);
    }

    /**
     * 解析CST时间，格式：<br>
     * <ol>
     * <li>EEE MMM dd HH:mm:ss z yyyy（例如：Wed Aug 01 00:00:00 CST 2012）</li>
     * </ol>
     *
     * @param cstString UTC时间
     * @return 日期对象
     * @since 4.6.9
     */
    public static DateTime parseCST(CharSequence cstString) {
        if (cstString == null) {
            return null;
        }

        return parse(cstString, DatePattern.JDK_DATETIME_FORMAT);
    }

    /**
     * 将日期字符串转换为{@link DateTime}对象，格式：<br>
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
    public static DateTime parseDate(CharSequence dateCharSequence) {
        if (com.sondertara.common.util.StringUtils.isBlank(dateCharSequence)) {
            return null;
        }
        String dateStr = dateCharSequence.toString();
        // 去掉两边空格并去掉中文日期中的“日”和“秒”，以规范长度
        dateStr = com.sondertara.common.util.StringUtils.removeAll(dateStr.trim(), '日', '秒');
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
        } else if (com.sondertara.common.util.StringUtils.containsAnyIgnoreCase(dateStr, wtb)) {
            // JDK的Date对象toString默认格式，类似于：
            // Tue Jun 4 16:25:15 +0800 2019
            // Thu May 16 17:57:18 GMT+08:00 2019
            // Wed Aug 01 00:00:00 CST 2012
            return parseCST(dateStr);
        } else if (com.sondertara.common.util.StringUtils.contains(dateStr, 'T')) {
            // UTC时间
            return parseUTC(dateStr);
        }

        // 标准日期格式（包括单个数字的日期时间）
        dateStr = normalize(dateStr);
        if (RegexUtils.isMatch(DatePattern.REGEX_NORM, dateStr)) {
            final int colonCount = com.sondertara.common.util.StringUtils.count(dateStr, CharUtils.COLON);
            switch (colonCount) {
                case 0:
                    // yyyy-MM-dd
                    return parse(dateStr, DatePattern.NORM_DATE_FORMAT);
                case 1:
                    // yyyy-MM-dd HH:mm
                    return parse(dateStr, DatePattern.NORM_DATETIME_MINUTE_FORMAT);
                case 2:
                    final int indexOfDot = com.sondertara.common.util.StringUtils.indexOf(dateStr, CharUtils.DOT);
                    if (indexOfDot > 0) {
                        final int length1 = dateStr.length();
                        // yyyy-MM-dd HH:mm:ss.SSS 或者 yyyy-MM-dd HH:mm:ss.SSSSSS
                        if (length1 - indexOfDot > 4) {
                            // 类似yyyy-MM-dd HH:mm:ss.SSSSSS，采取截断操作
                            dateStr = com.sondertara.common.util.StringUtils.subPre(dateStr, indexOfDot + 4);
                        }
                        return parse(dateStr, DatePattern.NORM_DATETIME_MS_FORMAT);
                    }
                    // yyyy-MM-dd HH:mm:ss
                    return parse(dateStr, DatePattern.NORM_DATETIME_FORMAT);
                default:
            }
        }

        // 没有更多匹配的时间格式
        throw ErrorUtils.illegalStateException("No format fit for date String [{}] !", dateStr);
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
     * @return {@link DateTime}
     * @since 4.5.7
     */
    public static DateTime truncate(Date date, DateField dateField) {
        return new DateTime(truncate(calendar(date), dateField));
    }

    /**
     * 修改日期为某个时间字段四舍五入时间
     *
     * @param date      {@link Date}
     * @param dateField 时间字段
     * @return {@link DateTime}
     * @since 4.5.7
     */
    public static DateTime round(Date date, DateField dateField) {
        return new DateTime(round(calendar(date), dateField));
    }

    /**
     * 修改日期为某个时间字段结束时间
     *
     * @param date      {@link Date}
     * @param dateField 保留到的时间字段，如定义为
     *                  {@link DateField#SECOND}，表示这个字段不变，这个字段以下字段全部取最大值
     * @return {@link DateTime}
     * @since 4.5.7
     */
    public static DateTime ceiling(Date date, DateField dateField) {
        return new DateTime(ceiling(calendar(date), dateField));
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
     * @return {@link DateTime}
     * @since 4.5.7
     */
    public static DateTime ceiling(Date date, DateField dateField, boolean truncateMillisecond) {
        return new DateTime(ceiling(calendar(date), dateField, truncateMillisecond));
    }

    /**
     * 获取秒级别的开始时间，即毫秒部分设置为0
     *
     * @param date 日期
     * @return {@link DateTime}
     * @since 4.6.2
     */
    public static DateTime beginOfSecond(Date date) {
        return new DateTime(beginOfSecond(calendar(date)));
    }

    /**
     * 获取秒级别的结束时间，即毫秒设置为999
     *
     * @param date 日期
     * @return {@link DateTime}
     * @since 4.6.2
     */
    public static DateTime endOfSecond(Date date) {
        return new DateTime(endOfSecond(calendar(date)));
    }

    /**
     * 获取某小时的开始时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime beginOfHour(Date date) {
        return new DateTime(beginOfHour(calendar(date)));
    }

    /**
     * 获取某小时的结束时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime endOfHour(Date date) {
        return new DateTime(endOfHour(calendar(date)));
    }

    /**
     * 获取某分钟的开始时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime beginOfMinute(Date date) {
        return new DateTime(beginOfMinute(calendar(date)));
    }

    /**
     * 获取某分钟的结束时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime endOfMinute(Date date) {
        return new DateTime(endOfMinute(calendar(date)));
    }

    /**
     * 获取某天的开始时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime beginOfDay(Date date) {
        return new DateTime(beginOfDay(calendar(date)));
    }

    /**
     * 获取某天的结束时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime endOfDay(Date date) {
        return new DateTime(endOfDay(calendar(date)));
    }

    /**
     * 获取某周的开始时间，周一定为一周的开始时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime beginOfWeek(Date date) {
        return new DateTime(beginOfWeek(calendar(date)));
    }

    /**
     * 获取某周的开始时间
     *
     * @param date               日期
     * @param isMondayAsFirstDay 是否周一做为一周的第一天（false表示周日做为第一天）
     * @return {@link DateTime}
     * @since 5.4.0
     */
    public static DateTime beginOfWeek(Date date, boolean isMondayAsFirstDay) {
        return new DateTime(beginOfWeek(calendar(date), isMondayAsFirstDay));
    }

    /**
     * 获取某周的结束时间，周日定为一周的结束
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime endOfWeek(Date date) {
        return new DateTime(endOfWeek(calendar(date)));
    }

    /**
     * 获取某周的结束时间
     *
     * @param date              日期
     * @param isSundayAsLastDay 是否周日做为一周的最后一天（false表示周六做为最后一天）
     * @return {@link DateTime}
     * @since 5.4.0
     */
    public static DateTime endOfWeek(Date date, boolean isSundayAsLastDay) {
        return new DateTime(endOfWeek(calendar(date), isSundayAsLastDay));
    }

    /**
     * 获取某月的开始时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime beginOfMonth(Date date) {
        return new DateTime(beginOfMonth(calendar(date)));
    }

    /**
     * 获取某月的结束时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime endOfMonth(Date date) {
        return new DateTime(endOfMonth(calendar(date)));
    }

    /**
     * 获取某季度的开始时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime beginOfQuarter(Date date) {
        return new DateTime(beginOfQuarter(calendar(date)));
    }

    /**
     * 获取某季度的结束时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime endOfQuarter(Date date) {
        return new DateTime(endOfQuarter(calendar(date)));
    }

    /**
     * 获取某年的开始时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime beginOfYear(Date date) {
        return new DateTime(beginOfYear(calendar(date)));
    }

    /**
     * 获取某年的结束时间
     *
     * @param date 日期
     * @return {@link DateTime}
     */
    public static DateTime endOfYear(Date date) {
        return new DateTime(endOfYear(calendar(date)));
    }
    // --------------------------------------------------- Offset for now

    /**
     * 昨天
     *
     * @return 昨天
     */
    public static DateTime yesterday() {
        return offsetDay(new DateTime(), -1);
    }

    /**
     * 明天
     *
     * @return 明天
     * @since 3.0.1
     */
    public static DateTime tomorrow() {
        return offsetDay(new DateTime(), 1);
    }

    /**
     * 上周
     *
     * @return 上周
     */
    public static DateTime lastWeek() {
        return offsetWeek(new DateTime(), -1);
    }

    /**
     * 下周
     *
     * @return 下周
     * @since 3.0.1
     */
    public static DateTime nextWeek() {
        return offsetWeek(new DateTime(), 1);
    }

    /**
     * 上个月
     *
     * @return 上个月
     */
    public static DateTime lastMonth() {
        return offsetMonth(new DateTime(), -1);
    }

    /**
     * 下个月
     *
     * @return 下个月
     * @since 3.0.1
     */
    public static DateTime nextMonth() {
        return offsetMonth(new DateTime(), 1);
    }

    /**
     * 偏移毫秒数
     *
     * @param date   日期
     * @param offset 偏移毫秒数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static DateTime offsetMillisecond(Date date, int offset) {
        return offset(date, DateField.MILLISECOND, offset);
    }

    /**
     * 偏移秒数
     *
     * @param date   日期
     * @param offset 偏移秒数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static DateTime offsetSecond(Date date, int offset) {
        return offset(date, DateField.SECOND, offset);
    }

    /**
     * 偏移分钟
     *
     * @param date   日期
     * @param offset 偏移分钟数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static DateTime offsetMinute(Date date, int offset) {
        return offset(date, DateField.MINUTE, offset);
    }

    /**
     * 偏移小时
     *
     * @param date   日期
     * @param offset 偏移小时数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static DateTime offsetHour(Date date, int offset) {
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
    public static DateTime offsetDay(Date date, int offset) {
        return offset(date, DateField.DAY_OF_YEAR, offset);
    }

    /**
     * 偏移周
     *
     * @param date   日期
     * @param offset 偏移周数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static DateTime offsetWeek(Date date, int offset) {
        return offset(date, DateField.WEEK_OF_YEAR, offset);
    }

    /**
     * 偏移月
     *
     * @param date   日期
     * @param offset 偏移月数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static DateTime offsetMonth(Date date, int offset) {
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
    public static DateTime offset(Date date, DateField dateField, int offset) {
        return dateNew(date).offset(dateField, offset);
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
     * @since 3.3.1
     */
    public static long between(Date beginDate, Date endDate, TimeUnit unit, boolean isAbs) {
        return new DateBetween(beginDate, endDate, isAbs).between(unit);
    }

    /**
     * 判断两个日期相差的毫秒数
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @return 日期差
     * @since 3.0.1
     */
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
     * @since 3.0.1
     */
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
     * @since 3.0.8
     */
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
     * @since 3.0.8
     */
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
     * @since 3.0.8
     */
    public static boolean isIn(Date date, Date beginDate, Date endDate) {
        if (date instanceof DateTime) {
            return ((DateTime) date).isIn(beginDate, endDate);
        } else {
            return new DateTime(date).isIn(beginDate, endDate);
        }
    }

    /**
     * 是否为相同时间<br>
     * 此方法比较两个日期的时间戳是否相同
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否为相同时间
     * @since 4.1.13
     */
    public static boolean isSameTime(Date date1, Date date2) {
        return date1.compareTo(date2) == 0;
    }

    /**
     * 比较两个日期是否为同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否为同一天
     * @since 4.1.13
     */
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
     * @since 5.4.1
     */
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
     * @since 3.1.2
     */
    public static int timeToSecond(String timeStr) {
        if (com.sondertara.common.util.StringUtils.isEmpty(timeStr)) {
            return 0;
        }

        final List<String> hms = com.sondertara.common.util.StringUtils.splitTrim(timeStr,
                com.sondertara.common.util.StringUtils.COLON, 3);
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
     * @since 3.1.2
     */
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
     * @since 4.4.3
     */
    public static String getZodiac(int month, int day) {
        return Zodiac.getZodiac(month, day);
    }

    /**
     * 计算生肖，只计算1900年后出生的人
     *
     * @param year 农历年
     * @return 生肖名
     * @since 4.4.3
     */
    public static String getChineseZodiac(int year) {
        return Zodiac.getChineseZodiac(year);
    }

    /**
     * {@code null}安全的日期比较，{@code null}对象排在末尾
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 比较结果，如果date1 &lt; date2，返回数小于0，date1==date2返回0，date1 &gt; date2 大于0
     * @since 4.6.2
     */
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
     * @author dazer
     * @since 5.6.4
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
     * @since 4.6.6
     */
    public static long nanosToMillis(long duration) {
        return TimeUnit.NANOSECONDS.toMillis(duration);
    }

    /**
     * 纳秒转秒，保留小数
     *
     * @param duration 时长
     * @return 秒
     * @since 4.6.6
     */
    public static double nanosToSeconds(long duration) {
        return duration / 1_000_000_000.0;
    }

    /**
     * Date对象转换为{@link Instant}对象
     *
     * @param date Date对象
     * @return {@link Instant}对象
     * @since 5.0.2
     */
    public static Instant toInstant(Date date) {
        return null == date ? null : date.toInstant();
    }

    /**
     * Date对象转换为{@link Instant}对象
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     * @since 5.0.2
     */
    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        return TemporalAccessorUtils.toInstant(temporalAccessor);
    }

    /**
     * {@link Instant} 转换为 {@link LocalDateTime}，使用系统默认时区
     *
     * @param instant {@link Instant}
     * @return {@link LocalDateTime}
     * @since 5.0.5
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTimeUtils.of(instant);
    }

    /**
     * {@link Date} 转换为 {@link LocalDateTime}，使用系统默认时区
     *
     * @param date {@link Date}
     * @return {@link LocalDateTime}
     * @since 5.0.5
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTimeUtils.of(date);
    }

    /**
     * {@link Date} 转换时区
     *
     * @param date   {@link Date}
     * @param zoneId {@link ZoneId}
     * @return {@link DateTime}
     * @since 5.8.3
     */
    public static DateTime convertTimeZone(Date date, ZoneId zoneId) {
        return new DateTime(date, TimeZone.getTimeZone(zoneId));
    }

    /**
     * {@link Date} 转换时区
     *
     * @param date     {@link Date}
     * @param timeZone {@link TimeZone}
     * @return {@link DateTime}
     * @since 5.8.3
     */
    public static DateTime convertTimeZone(Date date, TimeZone timeZone) {
        return new DateTime(date, timeZone);
    }

    /**
     * 获得指定年份的总天数
     *
     * @param year 年份
     * @return 天
     * @since 5.3.6
     */
    public static int lengthOfYear(int year) {
        return Year.of(year).length();
    }

    /**
     * 获得指定月份的总天数
     *
     * @param month      月份
     * @param isLeapYear 是否闰年
     * @return 天
     * @since 5.4.2
     */
    public static int lengthOfMonth(int month, boolean isLeapYear) {
        return java.time.Month.of(month).length(isLeapYear);
    }

    /**
     * 创建{@link SimpleDateFormat}，注意此对象非线程安全！<br>
     * 此对象默认为严格格式模式，即parse时如果格式不正确会报错。
     *
     * @param pattern 表达式
     * @return {@link SimpleDateFormat}
     * @since 5.5.5
     */
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
     * @since 5.5.5
     */
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
     * @since 5.7.16
     */
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
     * @since 5.7.22
     */
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
        if (com.sondertara.common.util.StringUtils.isBlank(dateStr)) {
            return com.sondertara.common.util.StringUtils.str(dateStr);
        }

        // 日期时间分开处理
        final List<String> dateAndTime = com.sondertara.common.util.StringUtils.splitTrim(dateStr, ' ');
        final int size = dateAndTime.size();
        if (size < 1 || size > 2) {
            // 非可被标准处理的格式
            return com.sondertara.common.util.StringUtils.str(dateStr);
        }

        final StringBuilder builder = com.sondertara.common.util.StringUtils.builder();

        // 日期部分（"\"、"/"、"."、"年"、"月"都替换为"-"）
        String datePart = dateAndTime.get(0).replaceAll("[/.年月]", "-");
        datePart = com.sondertara.common.util.StringUtils.removeSuffix(datePart, "日");
        builder.append(datePart);

        // 时间部分
        if (size == 2) {
            builder.append(' ');
            String timePart = dateAndTime.get(1).replaceAll("[时分秒]", ":");
            timePart = com.sondertara.common.util.StringUtils.removeSuffix(timePart, ":");
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
     * @param startDateInclusive
     * @param endDateExclusive
     * @return
     */
    public static final Date random(Date startDateInclusive, Date endDateExclusive) {
        int betweenSeconds = (int) ((endDateExclusive.getTime() - startDateInclusive.getTime()) / 1000);

        Calendar instance = Calendar.getInstance();
        instance.setTime(startDateInclusive);
        instance.add(Calendar.SECOND, RandomUtils.nextInt(0, betweenSeconds));
        return instance.getTime();
    }

    /**
     * 随机生成日期
     *
     * @return
     */
    public static Date random() {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(0L);
        instance.add(Calendar.MINUTE, RandomUtils.nextInt(0, Integer.MAX_VALUE));
        return instance.getTime();
    }

    /**
     * 获取当月的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        eraseTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取上个月日期
     *
     * @param date
     * @return
     */
    public static Date getLastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, -1);
        eraseTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取下个月日期
     *
     * @param date
     * @return
     */
    public static Date getNextMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 1);
        eraseTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取某天的昨天
     *
     * @param date
     * @return
     */
    public static Date getYesterday(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusDays(1);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取昨天
     *
     * @return
     */
    public static Date getYesterday() {
        return getYesterday(Calendar.getInstance().getTime());
    }

    /**
     * 获取某天的明天
     *
     * @param date
     * @return
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
     * 获取指定年份的第一天
     *
     * @param year
     * @return
     */
    public static Date getFirstDayOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * 获取指定年份的最后一天
     *
     * @param year
     * @return
     */
    public static Date getLastDayOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     * 判断指定日期是否在某个时间区间内
     *
     * @param date
     * @param startDateInclusive
     * @param endDateInclusive
     * @return
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
     * @param dateTimeStr
     * @return
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
                dateFormats = FREQUENTLEY_USED_UTC_WITH_Z_DATE_FORMATS;
            } else if (StringUtils.contains(utcString, '+')) {
                // 去除类似2019-06-01T19:45:43 +08:00加号前的空格
                utcString = utcString.replace(" +", "+");
                final String zoneOffset = StringUtils.substringAfterLast(utcString, '+');
                if (StringUtils.isBlank(zoneOffset)) {
                    throw new TaraException("Invalid UTC format: [{}]", dateTimeStr);
                }

                dateFormats = new String[] { DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT.getPattern(),
                        DatePattern.UTC_WITH_XXX_OFFSET_FORMAT.getPattern() };
            } else {
                if (length == DatePattern.UTC_SIMPLE_PATTERN.length() - 2) {
                    // 格式类似：2018-09-13T05:34:31
                    dateFormats = new String[] { DatePattern.UTC_SIMPLE_FORMAT.getPattern() };
                } else if (StringUtils.contains(utcString, CharUtils.DOT)) {
                    // 可能为： 2021-03-17T06:31:33.99
                    dateFormats = new String[] { DatePattern.UTC_SIMPLE_MS_FORMAT.getPattern() };
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
     * @param date
     * @return
     * @since 1.0.3
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
     * @param calendar
     * @return
     * @since 1.0.3
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
     * @param timeLength
     * @param timeUnit
     * @return
     * @since 1.0.3
     */
    public static String getGapTime(long timeLength, TimeUnit timeUnit) {
        String SEPARATOR_CHAR = ":";
        StringBuilder gapTime = new StringBuilder();
        long days = timeUnit.toDays(timeLength);
        if (days > 0) {
            gapTime.append(StringUtils.leftPad(String.valueOf(days % 365), 2, "0"));
            gapTime.append(SEPARATOR_CHAR);
        }

        long hours = timeUnit.toHours(timeLength);
        if (hours > 0) {
            gapTime.append(StringUtils.leftPad(String.valueOf(hours % 24), 2, "0"));
            gapTime.append(SEPARATOR_CHAR);
        }

        long minutes = timeUnit.toMinutes(timeLength);
        if (minutes > 0) {
            gapTime.append(StringUtils.leftPad(String.valueOf(minutes % 60), 2, "0"));
            gapTime.append(SEPARATOR_CHAR);
        }

        long seconds = timeUnit.toSeconds(timeLength);
        if (seconds > 0) {
            gapTime.append(StringUtils.leftPad(String.valueOf(seconds % 60), 2, "0"));
        }

        return gapTime.toString();
    }

    public static String getNow() {
        return now(DATE_TIME_FORMATTER);
    }

    public static String now() {
        return getNow();
    }

    public static String today() {
        return now(DatePattern.NORM_DATE_PATTERN);
    }

    public static String now(String formatPattern) {

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        return dateTime.format(formatter);
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
                if (com.sondertara.common.util.StringUtils.startWith(formatPattern,
                        DatePattern.PURE_DATETIME_PATTERN)) {
                    final String fraction = com.sondertara.common.util.StringUtils.removePrefix(formatPattern,
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
        throw new IllegalArgumentException("Invalid date format: " + date);
    }

    /**
     * @param date jdk8之前的date
     */
    public static String format(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }

    public static String format(Date date) {
        if (null == date) {
            return null;
        }
        return format(date, DatePattern.NORM_DATETIME_PATTERN);
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        if (null == localDateTime) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return localDateTime.format(formatter);
        } catch (Exception e) {
            log.error("parser date str error,pattern=[{}]", pattern, e);
            return null;
        }
    }

    public static String formatLocalDate(LocalDate localDate, String pattern) {
        if (null == localDate) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDate.format(formatter);
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        return formatLocalDateTime(localDateTime, DATE_TIME_FORMATTER);
    }

    public static DateTime parse(String dateTime) {
        return parse(dateTime, DATE_TIME_FORMATTER);
    }

    public static DateTime parse(String dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        Date date = null;
        try {
            LocalDateTime localDateTime = parseLocalDateTime(dateTime, pattern);
            assert localDateTime != null;
            date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
        }
        if (date == null) {
            try {
                final LocalDate parse = parseLocalDate(dateTime);
                date = Date.from(parse.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            } catch (Exception e) {
            }

        }
        return new DateTime(date);
    }

    /**
     * 获取两个日期间隔天数，只要不是同一天，间隔就为1
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return the two date delay
     */
    public static Integer getDelay(String startDate, String endDate) {

        final LocalDate start = parseLocalDate(startDate);
        final LocalDate end = parseLocalDate(endDate);
        if (start != null && end != null) {
            final long l = end.toEpochDay() - start.toEpochDay();
            return (int) l;
        }
        return null;
    }

    /**
     * 获取某一天零点
     *
     * @param dateStr 20120920
     * @return 20120920
     */
    public static String getDayStart(String dateStr) {
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMATTER));

        return getDayStart(localDate);
    }

    /**
     * 获取某一天零点
     *
     * @param localDate the local date
     * @return the
     */
    public static String getDayStart(LocalDate localDate) {

        LocalDateTime dateTime = LocalDateTime.of(localDate, LocalTime.MIN);
        return formatLocalDateTime(dateTime);
    }

    /**
     * 获取某一天结束
     *
     * @param dateStr 20120920
     * @return the end str 2012-09-20 23:59:59
     */
    public static String getDayEnd(String dateStr) {
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMATTER));
        return getDayEnd(localDate);
    }

    /**
     * get end of a day
     *
     * @return the end str 2012-09-20 23:59:59
     */
    public static String getDayEnd(LocalDate localDate) {

        LocalDateTime dateTime = LocalDateTime.of(localDate, LocalTime.MAX);
        return formatLocalDateTime(dateTime);
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

        if (date instanceof DateTime) {
            return of(date.toInstant(), ((DateTime) date).getZoneId());
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
     * @since 5.3.10
     */
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
     * @since 5.3.10
     */
    public static LocalDate parseLocalDate(CharSequence text) {
        return parseLocalDate(text, (DateTimeFormatter) null);
    }

    /**
     * 解析日期时间字符串为{@link LocalDate}，格式支持日期
     *
     * @param text      日期时间字符串
     * @param formatter 日期格式化器，预定义的格式见：{@link DateTimeFormatter}
     * @return {@link LocalDate}
     * @since 5.3.10
     */
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
     * 格式化日期时间为yyyy-MM-dd HH:mm:ss格式
     *
     * @param time {@link LocalDateTime}
     * @return 格式化后的字符串
     * @since 5.3.11
     */
    public static String formatNormal(LocalDateTime time) {
        return format(time, DatePattern.NORM_DATETIME_FORMATTER);
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
     * 格式化日期时间为yyyy-MM-dd格式
     *
     * @param date {@link LocalDate}
     * @return 格式化后的字符串
     * @since 5.3.11
     */
    public static String formatNormal(LocalDate date) {
        return format(date, DatePattern.NORM_DATE_FORMATTER);
    }

    /**
     * 格式化日期时间为指定格式
     *
     * @param date      {@link LocalDate}
     * @param formatter 日期格式化器，预定义的格式见：{@link DateTimeFormatter}; 常量如：
     *                  {@link DatePattern#NORM_DATE_FORMATTER},
     *                  {@link DatePattern#NORM_DATETIME_FORMATTER}
     * @return 格式化后的字符串
     * @since 5.3.10
     */
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
     * @since 5.3.10
     */
    public static String format(LocalDate date, String format) {
        if (null == date) {
            return null;
        }
        return format(date, DateTimeFormatter.ofPattern(format));
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
     * @since 5.4.5
     */
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
     * @since 5.4.5
     */
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
     * @since 5.7.18
     */
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
     * @since 5.4.1
     */
    public static long toEpochMilli(TemporalAccessor temporalAccessor) {
        return TemporalAccessorUtils.toEpochMilli(temporalAccessor);
    }

    /**
     * 是否为周末（周六或周日）
     *
     * @param localDateTime 判定的日期{@link LocalDateTime}
     * @return 是否为周末（周六或周日）
     * @since 5.7.6
     */
    public static boolean isWeekend(LocalDateTime localDateTime) {
        return isWeekend(localDateTime.toLocalDate());
    }

    /**
     * 是否为周末（周六或周日）
     *
     * @param localDate 判定的日期{@link LocalDate}
     * @return 是否为周末（周六或周日）
     * @since 5.7.6
     */
    public static boolean isWeekend(LocalDate localDate) {
        final DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return DayOfWeek.SATURDAY == dayOfWeek || DayOfWeek.SUNDAY == dayOfWeek;
    }

    /**
     * 获取{@link LocalDate}对应的星期值
     *
     * @param localDate 日期{@link LocalDate}
     * @return {@link Week}
     * @since 5.7.14
     */
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
     * @since 5.7.20
     */
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
     * @since 5.7.21
     */
    public static int weekOfYear(TemporalAccessor date) {
        return TemporalAccessorUtils.get(date, WeekFields.ISO.weekOfYear());
    }

    /**
     * 比较两个日期是否为同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否为同一天
     * @since 5.8.5
     */
    public static boolean isSameDay(final LocalDateTime date1, final LocalDateTime date2) {
        return date1 != null && date2 != null && isSameDay(date1.toLocalDate(), date2.toLocalDate());
    }

    /**
     * 比较两个日期是否为同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否为同一天
     * @since 5.8.5
     */
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
     * @since 5.8.5
     */
    public static boolean isIn(ChronoLocalDateTime<?> date, ChronoLocalDateTime<?> beginDate,
            ChronoLocalDateTime<?> endDate) {
        return TemporalAccessorUtils.isIn(date, beginDate, endDate);
    }

}
