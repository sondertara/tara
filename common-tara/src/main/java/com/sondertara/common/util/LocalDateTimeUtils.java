package com.sondertara.common.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sondertara.common.exception.TaraException;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * date util
 *
 * @author huangxiaohu
 * @since 2019-03-19 16:25
 */
public class LocalDateTimeUtils {

    private static final Logger log = LoggerFactory.getLogger(LocalDateTimeUtils.class);


    public static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_MILLS_FORMATTER = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMATTER = "yyyy-MM-dd";


    /**
     * java.util.Date EEE MMM zzz 缩写数组
     */
    private final static String[] wtb = {"sun", "mon", "tue", "wed", "thu", "fri", "sat", "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec", "gmt", "ut", "utc", "est", "edt", "cst", "cdt", "mst", "mdt", "pst", "pdt"};

    /**
     * 常用的UTC时间格式
     */
    private final static String[] FREQUENTLEY_USED_UTC_WITH_Z_DATE_FORMATS = new String[]{"yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"};

    private final static String[] FREQUENTLEY_USED_UTC_DATE_FORMATS = new String[]{"yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"};

    /**
     * 常用纯数字时间格式
     */
    private final static String[] FREQUENTLY_USED_NUMBER_DATE_FORMATS = new String[]{"yyyyMMddHHmmss", "yyyyMMddHHmmssSSS", "yyyyMMdd", "yyyyMMss", "HHmmss"};

    /**
     * CST格式
     */
    private final static String[] FREQUENTLY_USED_CST_DATE_FORMATS = new String[]{"EEE, dd MMM yyyy HH:mm:ss z", "EEE MMM dd HH:mm:ss zzz yyyy"};

    /**
     * 常用的时间格式
     */
    private final static String[] FREQUENTLY_USED_DATE_FORMATS = new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss", "yyyy年MM月dd日 HH时mm分ss秒", "yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd", "HH:mm:ss", "HH时mm分ss秒", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss.SSS"};


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
    public static final Date random() {
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
     * 获取当月的第一天
     *
     * @param month
     * @param format 日期格式
     * @return
     * @throws ParseException
     */
    public static Date getFirstDayOfMonth(String month, String format) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.parseDate(month, format));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }


    /**
     * 获取指定日期当月的最后一天
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public static Date getTomorrow() {
        return getTomorrow(Calendar.getInstance().getTime());
    }

    /**
     * 获取指定月份的最后一天
     *
     * @param month
     * @param format 日期格式
     * @return
     * @throws ParseException
     */
    public static Date getLastDayOfMonth(String month, String format) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.parseDate(month, format));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
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
        return ((date.after(startDateInclusive) && date.before(endDateInclusive)) || date.equals(startDateInclusive) || date.equals(endDateInclusive));
    }


    /**
     * 自动解析日期
     *
     * @param dateTimeStr
     * @return
     */
    public static Date parseDate(String dateTimeStr) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(dateTimeStr)) {
            throw new IllegalArgumentException("Date string must not be null");
        }

        String[] dateFormats;

        if (NumberUtils.isCreatable(dateTimeStr)) {
            // 纯数字
            dateFormats = FREQUENTLY_USED_NUMBER_DATE_FORMATS;
        } else if (org.apache.commons.lang3.StringUtils.contains(dateTimeStr, 'T')) {
            // UTC
            if (org.apache.commons.lang3.StringUtils.contains(dateTimeStr, 'Z')) {
                dateFormats = FREQUENTLEY_USED_UTC_WITH_Z_DATE_FORMATS;
            } else {
                dateFormats = FREQUENTLEY_USED_UTC_DATE_FORMATS;
            }
        } else if (org.apache.commons.lang3.StringUtils.containsAny(dateTimeStr, wtb)) {
            // CST格式
            dateFormats = FREQUENTLY_USED_CST_DATE_FORMATS;
        } else {
            // 其它
            dateFormats = FREQUENTLY_USED_DATE_FORMATS;
        }
        try {
            return DateUtils.parseDate(dateTimeStr, dateFormats);
        } catch (ParseException e) {
            throw new TaraException("Parse Date error,the dateTimeStr is:" + dateTimeStr, e);
        }
    }

    /**
     * Date => {@link Instant}
     *
     * @param date
     * @return {@link Instant}对象
     */
    public static Instant toInstant(Date date) {
        return date == null ? null : date.toInstant();
    }


    /**
     * {@link TemporalAccessor} => {@link Instant}
     *
     * @param temporalAccessor
     * @return {@link Instant}对象
     */
    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }
        Instant result;
        if (temporalAccessor instanceof Instant) {
            result = (Instant) temporalAccessor;
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof LocalDate) {
            result = ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((LocalTime) temporalAccessor).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((OffsetTime) temporalAccessor).atDate(LocalDate.now()).toInstant();
        } else {
            result = Instant.from(temporalAccessor);
        }

        return result;
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
            gapTime.append(org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(days % 365), 2, "0"));
            gapTime.append(SEPARATOR_CHAR);
        }

        long hours = timeUnit.toHours(timeLength);
        if (hours > 0) {
            gapTime.append(org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(hours % 24), 2, "0"));
            gapTime.append(SEPARATOR_CHAR);
        }

        long minutes = timeUnit.toMinutes(timeLength);
        if (minutes > 0) {
            gapTime.append(org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(minutes % 60), 2, "0"));
            gapTime.append(SEPARATOR_CHAR);
        }

        long seconds = timeUnit.toSeconds(timeLength);
        if (seconds > 0) {
            gapTime.append(StringUtils.leftPad(String.valueOf(seconds % 60), 2, "0"));
        }

        return gapTime.toString();
    }


    private static final LoadingCache<String, DateTimeFormatter> LOAD_CACHE = CacheBuilder.newBuilder().maximumSize(16).build(new CacheLoader<String, DateTimeFormatter>() {
        @Override
        public DateTimeFormatter load(@Nonnull String pattern) {
            return DateTimeFormatter.ofPattern(pattern);
        }
    });

    public static String getNow() {
        return now(DATE_TIME_FORMATTER);
    }

    public static String now() {
        return getNow();
    }

    public static String now(String formatPattern) {

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        return dateTime.format(formatter);
    }

    public static LocalDateTime convertToLocalDateTime(String dateTimeStr, String formatPattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            log.error("parser date error,params=[{}],pattern=[{}]", dateTimeStr, formatPattern, e);
            return null;
        }
    }

    public static LocalDateTime convertToLocalDateTime(String dateTimeStr) {
        return convertToLocalDateTime(dateTimeStr, DATE_TIME_FORMATTER);
    }


    private static LocalDate convertToLocalDate(String date) {
        DateTimeFormatter formatter;
        LocalDate parse = null;
        try {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            parse = LocalDate.parse(date, formatter);
        } catch (Exception e) {
        }
        if (parse == null) {
            try {
                formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                parse = LocalDate.parse(date, formatter);
            } catch (Exception e) {
            }

        }
        if (parse == null) {
            try {
                formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                parse = LocalDate.parse(date, formatter);
            } catch (Exception e) {
            }
        }

        return parse;
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
        return format(date, DATE_TIME_FORMATTER);
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

    public static Date parseToDate(String dateTime) {
        return parseToDate(dateTime, DATE_TIME_FORMATTER);
    }

    public static Date parseToDate(String dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        Date date = null;
        try {
            LocalDateTime localDateTime = convertToLocalDateTime(dateTime, pattern);
            assert localDateTime != null;
            date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
        }
        if (date == null) {
            try {
                final LocalDate parse = convertToLocalDate(dateTime);
                date = Date.from(parse.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            } catch (Exception e) {
            }

        }
        return date;
    }

    /**
     * 获取两个日期间隔天数，只要不是同一天，间隔就为1
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return the two date delay
     */
    public static Integer getDelay(String startDate, String endDate) {

        final LocalDate start = convertToLocalDate(startDate);
        final LocalDate end = convertToLocalDate(endDate);
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

        LocalDateTime dateTime = LocalDateTime.of(localDate, LocalTime.MIN);
        return formatLocalDateTime(dateTime);
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

        LocalDateTime dateTime = LocalDateTime.of(localDate, LocalTime.MAX);
        return formatLocalDateTime(dateTime);
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
            LocalDateTime localDateTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern(DATE_TIME_MILLS_FORMATTER));
            return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        } catch (Exception e) {

        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER));
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

}
