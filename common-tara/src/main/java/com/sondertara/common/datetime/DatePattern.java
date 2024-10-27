package com.sondertara.common.datetime;


import com.sondertara.common.datetime.format.FastDateFormat;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 日期格式化类，提供常用的日期格式化对象
 *
 * @author huangxiaohu
 */
public class DatePattern {

    /**
     * 标准日期时间正则，每个字段支持单个数字或2个数字，包括：
     *
     * <pre>
     *     yyyy-MM-dd HH:mm:ss.SSSSSS
     *     yyyy-MM-dd HH:mm:ss.SSS
     *     yyyy-MM-dd HH:mm:ss
     *     yyyy-MM-dd HH:mm
     *     yyyy-MM-dd
     * </pre>
     */
    public static final Pattern REGEX_NORM = Pattern
            .compile("\\d{4}-\\d{1,2}-\\d{1,2}(\\s\\d{1,2}:\\d{1,2}(:\\d{1,2})?)?(.\\d{1,6})?");

    // --------------------------------------------------------------------------------------------------------------------------------
    // Normal
    /**
     * 年格式：yyyy
     */
    public static final String NORM_YEAR_PATTERN = "yyyy";
    /**
     * 年月格式：yyyy-MM
     */
    public static final String NORM_MONTH_PATTERN = "yyyy-MM";
    /**
     * 年月格式 {@link FastDateFormat}：yyyy-MM
     */
    public static final FastDateFormat NORM_MONTH_FORMAT = FastDateFormat.getInstance(NORM_MONTH_PATTERN);
    /**
     * 年月格式 {@link FastDateFormat}：yyyy-MM
     */
    public static final DateTimeFormatter NORM_MONTH_FORMATTER = createFormatter(NORM_MONTH_PATTERN);

    /**
     * 简单年月格式：yyyyMM
     */
    public static final String SIMPLE_MONTH_PATTERN = "yyyyMM";
    /**
     * 简单年月格式 {@link FastDateFormat}：yyyyMM
     */
    public static final FastDateFormat SIMPLE_MONTH_FORMAT = FastDateFormat.getInstance(SIMPLE_MONTH_PATTERN);
    /**
     * 简单年月格式 {@link FastDateFormat}：yyyyMM
     */
    public static final DateTimeFormatter SIMPLE_MONTH_FORMATTER = createFormatter(SIMPLE_MONTH_PATTERN);

    /**
     * 标准日期格式：yyyy-MM-dd
     */
    public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyy-MM-dd
     */
    public static final FastDateFormat NORM_DATE_FORMAT = FastDateFormat.getInstance(NORM_DATE_PATTERN);
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyy-MM-dd
     */
    public static final DateTimeFormatter NORM_DATE_FORMATTER = createFormatter(NORM_DATE_PATTERN);

    /**
     * 标准时间格式：HH:mm:ss
     */
    public static final String NORM_TIME_PATTERN = "HH:mm:ss";
    /**
     * 标准时间格式 {@link FastDateFormat}：HH:mm:ss
     */
    public static final FastDateFormat NORM_TIME_FORMAT = FastDateFormat.getInstance(NORM_TIME_PATTERN);
    /**
     * 标准日期格式 {@link FastDateFormat}：HH:mm:ss
     */
    public static final DateTimeFormatter NORM_TIME_FORMATTER = createFormatter(NORM_TIME_PATTERN);

    /**
     * 标准日期时间格式，精确到分：yyyy-MM-dd HH:mm
     */
    public static final String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    /**
     * 标准日期时间格式，精确到分 {@link FastDateFormat}：yyyy-MM-dd HH:mm
     */
    public static final FastDateFormat NORM_DATETIME_MINUTE_FORMAT = FastDateFormat
            .getInstance(NORM_DATETIME_MINUTE_PATTERN);
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyy-MM-dd HH:mm
     */
    public static final DateTimeFormatter NORM_DATETIME_MINUTE_FORMATTER = createFormatter(
            NORM_DATETIME_MINUTE_PATTERN);

    /**
     * 标准日期时间格式，精确到秒：yyyy-MM-dd HH:mm:ss
     */
    public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 标准日期时间格式，精确到秒 {@link FastDateFormat}：yyyy-MM-dd HH:mm:ss
     */
    public static final FastDateFormat NORM_DATETIME_FORMAT = FastDateFormat.getInstance(NORM_DATETIME_PATTERN);
    /**
     * 标准日期时间格式，精确到秒 {@link FastDateFormat}：yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter NORM_DATETIME_FORMATTER = createFormatter(NORM_DATETIME_PATTERN);

    /**
     * 标准日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final String NORM_DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * 标准日期时间格式，精确到毫秒 {@link FastDateFormat}：yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final FastDateFormat NORM_DATETIME_MS_FORMAT = FastDateFormat.getInstance(NORM_DATETIME_MS_PATTERN);
    /**
     * 标准日期时间格式，精确到毫秒 {@link FastDateFormat}：yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final DateTimeFormatter NORM_DATETIME_MS_FORMATTER = createFormatter(NORM_DATETIME_MS_PATTERN);

    /**
     * ISO8601日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss,SSS
     */
    public static final String ISO8601_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";
    /**
     * ISO8601日期时间格式，精确到毫秒 {@link FastDateFormat}：yyyy-MM-dd HH:mm:ss,SSS
     */
    public static final FastDateFormat ISO8601_FORMAT = FastDateFormat.getInstance(ISO8601_PATTERN);
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyy-MM-dd HH:mm:ss,SSS
     */
    public static final DateTimeFormatter ISO8601_FORMATTER = createFormatter(ISO8601_PATTERN);

    /**
     * yyyy年MM月dd日  <pre> 2020年05月23日</pre>
     */
    public static final String YYYY_MM_DD_CN = "yyyy年MM月dd日";


    /**
     * yyyy年M月d日 不补0  <pre> 2020年5月23日</pre>
     */
    public static final String YYYY_M_D_CN = "yyyy年M月d日";

    /**
     * yyyy.MM.dd  <pre>2020.05.23</pre>
     */
    public static final String YYYY_MM_DD_POINT = "yyyy.MM.dd";

    /**
     * yyyy.M.d 不补0  <pre>2020.5.23</pre>
     */
    public static final String YYYY_M_D_POINT = "yyyy.M.d";

    /**
     * yy/MM/dd  <pre>20/05/23</pre>
     */
    public static final String YY_MM_DD_EN = "yy/MM/dd";

    /**
     * yy/M/d  <pre>20/5/23</pre>
     */
    public static final String YY_M_D_EN = "yy/M/d";

    /**
     * MM/dd/yy  <pre>05/23/20</pre>
     */
    public static final String MM_DD_YY_EN = "MM/dd/yy";
    public static final String dd_MM_yyyy = "dd/MM/yyyy";
    public static final String MM_dd_yyyy = "MM/dd/yyyy";
    public static final String M_d_yyyy = "M/d/yyyy";
    /**
     * M/d/yy  <pre>5/23/20</pre>
     */
    public static final String M_D_YY_EN = "M/d/yy";

    /**
     * yyyy-MM-dd E  <pre>2020-05-23 星期六</pre>
     */
    public static final String YYYY_MM_DD_E = "yyyy-MM-dd E";

    /**
     * 标准日期格式 {@link FastDateFormat}：yyyy年MM月dd日
     */
    public static final FastDateFormat CHINESE_DATE_FORMAT = FastDateFormat.getInstance(YYYY_MM_DD_CN);
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyy年MM月dd日
     */
    public static final DateTimeFormatter CHINESE_DATE_FORMATTER = createFormatter(YYYY_MM_DD_CN);

    /**
     * 标准日期格式：yyyy年MM月dd日 HH时mm分ss秒
     */
    public static final String CHINESE_DATE_TIME_PATTERN = "yyyy年MM月dd日HH时mm分ss秒";
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyy年MM月dd日HH时mm分ss秒
     */
    public static final FastDateFormat CHINESE_DATE_TIME_FORMAT = FastDateFormat.getInstance(CHINESE_DATE_TIME_PATTERN);
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyy年MM月dd日HH时mm分ss秒
     */
    public static final DateTimeFormatter CHINESE_DATE_TIME_FORMATTER = createFormatter(CHINESE_DATE_TIME_PATTERN);

    // --------------------------------------------------------------------------------------------------------------------------------
    // Pure
    /**
     * 标准日期格式：yyyyMMdd
     */
    public static final String PURE_DATE_PATTERN = "yyyyMMdd";

    /**
     * yyyy-M-d 不补0 <pre>  2020-5-23</pre>
     */
    public static final String YYYY_M_D = "yyyy-M-d";


    /**
     * yyyy/M/d 不补0  <pre>  2020/5/23</pre>
     */
    public static final String YYYY_M_D_EN = "yyyy/M/d";

    /**
     * yyyy/MM/dd  <pre>  2020/05/23</pre>
     */
    public static final String YYYY_MM_DD_EN = "yyyy/MM/dd";


    /**
     * 标准日期格式 {@link FastDateFormat}：yyyyMMdd
     */
    public static final FastDateFormat PURE_DATE_FORMAT = FastDateFormat.getInstance(PURE_DATE_PATTERN);
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyyMMdd
     */
    public static final DateTimeFormatter PURE_DATE_FORMATTER = createFormatter(PURE_DATE_PATTERN);
    /**
     * 常用日期数组
     */
    public static final String[] GENERIC_DATE_FORMATTERS = new String[]{NORM_DATE_PATTERN, PURE_DATE_PATTERN,
            YYYY_MM_DD_EN};

    /**
     * 标准日期格式：HHmmss
     */
    public static final String PURE_TIME_PATTERN = "HHmmss";
    /**
     * 标准日期格式 {@link FastDateFormat}：HHmmss
     */
    public static final FastDateFormat PURE_TIME_FORMAT = FastDateFormat.getInstance(PURE_TIME_PATTERN);
    /**
     * 标准日期格式 {@link FastDateFormat}：HHmmss
     */
    public static final DateTimeFormatter PURE_TIME_FORMATTER = createFormatter(PURE_TIME_PATTERN);

    /**
     * 标准日期格式：yyyyMMddHHmmss
     */
    public static final String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyyMMddHHmmss
     */
    public static final FastDateFormat PURE_DATETIME_FORMAT = FastDateFormat.getInstance(PURE_DATETIME_PATTERN);
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyyMMddHHmmss
     */
    public static final DateTimeFormatter PURE_DATETIME_FORMATTER = createFormatter(PURE_DATETIME_PATTERN);

    /**
     * 标准日期格式：yyyyMMddHHmmssSSS
     */
    public static final String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyyMMddHHmmssSSS
     */
    public static final FastDateFormat PURE_DATETIME_MS_FORMAT = FastDateFormat.getInstance(PURE_DATETIME_MS_PATTERN);
    /**
     * 标准日期格式 {@link FastDateFormat}：yyyyMMddHHmmssSSS
     */
    public static final DateTimeFormatter PURE_DATETIME_MS_FORMATTER = createFormatter(PURE_DATETIME_MS_PATTERN);

    // --------------------------------------------------------------------------------------------------------------------------------
    // Others
    /**
     * HTTP头中日期时间格式：EEE, dd MMM yyyy HH:mm:ss z
     */
    public static final String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    /**
     * HTTP头中日期时间格式 {@link FastDateFormat}：EEE, dd MMM yyyy HH:mm:ss z
     */
    public static final FastDateFormat HTTP_DATETIME_FORMAT = FastDateFormat.getInstance(HTTP_DATETIME_PATTERN,
            TimeZone.getTimeZone("GMT"), Locale.US);

    /**
     * JDK中日期时间格式：EEE MMM dd HH:mm:ss zzz yyyy
     * <pre>
     * Sat May 23 17:06:30 CST 2020
     * </pre>
     */
    public static final String JDK_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";
    /**
     * JDK中日期时间格式 {@link FastDateFormat}：EEE MMM dd HH:mm:ss zzz yyyy
     */
    public static final FastDateFormat JDK_DATETIME_FORMAT = FastDateFormat.getInstance(JDK_DATETIME_PATTERN,
            Locale.US);

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss
     * <pre>
     *     2020-05-23T17:06:30
     *     2020-05-23T09:06:30
     * </pre>
     */
    public static final String UTC_SIMPLE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    /**
     * UTC时间{@link FastDateFormat}：yyyy-MM-dd'T'HH:mm:ss
     */
    public static final FastDateFormat UTC_SIMPLE_FORMAT = FastDateFormat.getInstance(UTC_SIMPLE_PATTERN,
            TimeZone.getTimeZone("UTC"));

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss.SSS
     */
    public static final String UTC_SIMPLE_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    /**
     * UTC时间{@link FastDateFormat}：yyyy-MM-dd'T'HH:mm:ss.SSS
     */
    public static final FastDateFormat UTC_SIMPLE_MS_FORMAT = FastDateFormat.getInstance(UTC_SIMPLE_MS_PATTERN,
            TimeZone.getTimeZone("UTC"));

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss'Z'
     */
    public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    /**
     * UTC时间{@link FastDateFormat}：yyyy-MM-dd'T'HH:mm:ss'Z'
     */
    public static final FastDateFormat UTC_FORMAT = FastDateFormat.getInstance(UTC_PATTERN,
            TimeZone.getTimeZone("UTC"));

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ssZ
     * <pre>
     *     2020-05-23T17:06:30+0800
     *     2020-05-23T09:06:30+0000
     * </pre>
     */
    public static final String UTC_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    /**
     * UTC时间{@link FastDateFormat}：yyyy-MM-dd'T'HH:mm:ssZ
     */
    public static final FastDateFormat UTC_WITH_ZONE_OFFSET_FORMAT = FastDateFormat
            .getInstance(UTC_WITH_ZONE_OFFSET_PATTERN, TimeZone.getTimeZone("UTC"));

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ssXXX
     * <pre>
     *     2020-05-23T17:06:30+08:00
     *     2020-05-23T09:06:30Z 0时区时末尾 为Z
     * </pre>
     */
    public static final String UTC_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
    /**
     * UTC时间{@link FastDateFormat}：yyyy-MM-dd'T'HH:mm:ssXXX
     */
    public static final FastDateFormat UTC_WITH_XXX_OFFSET_FORMAT = FastDateFormat
            .getInstance(UTC_WITH_XXX_OFFSET_PATTERN);

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    public static final String UTC_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    /**
     * UTC时间{@link FastDateFormat}：yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    public static final FastDateFormat UTC_MS_FORMAT = FastDateFormat.getInstance(UTC_MS_PATTERN,
            TimeZone.getTimeZone("UTC"));

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ssZ
     * <pre>
     *   2020-05-23T17:06:30.272+0800
     *   2020-05-23T09:06:30.272+0000
     * </pre>
     */
    public static final String UTC_MS_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    /**
     * UTC时间{@link FastDateFormat}：yyyy-MM-dd'T'HH:mm:ssZ
     */
    public static final FastDateFormat UTC_MS_WITH_ZONE_OFFSET_FORMAT = FastDateFormat
            .getInstance(UTC_MS_WITH_ZONE_OFFSET_PATTERN, TimeZone.getTimeZone("UTC"));


    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSxxx
     * <pre>
     * 2020-05-23T17:06:30.272+08:00
     * 2020-05-23T09:06:30.272+00:00
     * </pre>
     */
    public static final String YYYY_MM_DD_T_HH_MM_SS_SSS_XXX = "yyyy-MM-dd'T'HH:mm:ss.SSSxxx";

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss.SSSXXX
     * yyyy-MM-dd'T'HH:mm:ss.SSSXXX
     * <pre>
     * 2020-05-23T17:06:30.272+08:00
     * 2020-05-23T09:06:30.272Z 0时区时末尾 为Z
     * </pre>
     */
    public static final String UTC_MS_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    /**
     * UTC时间{@link FastDateFormat}：yyyy-MM-dd'T'HH:mm:ss.SSSXXX
     */
    public static final FastDateFormat UTC_MS_WITH_XXX_OFFSET_FORMAT = FastDateFormat
            .getInstance(UTC_MS_WITH_XXX_OFFSET_PATTERN);

    /**
     * 创建并为 {@link DateTimeFormatter} 赋予默认时区和位置信息，默认值为系统默认值。
     *
     * @param pattern 日期格式
     * @return {@link DateTimeFormatter}
     */
    public static DateTimeFormatter createFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).withZone(ZoneId.systemDefault());
    }


    /**
     * yy 年的后2位  <pre> 20</pre>
     */
    public static final String YY = "yy";

    /**
     * yyyy  <pre>2020</pre>
     */
    public static final String YYYY = "yyyy";

    /**
     * yyyy-MM  <pre>2020-05</pre>
     */
    public static final String YYYY_MM = "yyyy-MM";

    /**
     * yyyyMM  <pre>202005</pre>
     */
    public static final String YYYYMM = "yyyyMM";

    /**
     * yyyy/MM  <pre>2020/05</pre>
     */
    public static final String YYYY_MM_EN = "yyyy/MM";

    /**
     * yyyy年MM月  <pre>2020年05月</pre>
     */
    public static final String YYYY_MM_CN = "yyyy年MM月";

    /**
     * yyyy年M月  <pre>2020年5月</pre>
     */
    public static final String YYYY_M_CN = "yyyy年M月";

    /**
     * MM-dd  <pre>05-23</pre>
     */
    public static final String MM_DD = "MM-dd";

    /**
     * MMdd  <pre>0523</pre>
     */
    public static final String MMDD = "MMdd";

    /**
     * MM/dd  <pre>05/23</pre>
     */
    public static final String MM_DD_EN = "MM/dd";

    /**
     * M/d 不补0  <pre>5/23</pre>
     */
    public static final String M_D_EN = "M/d";

    /**
     * MM月dd日  <pre>05月23日</pre>
     */
    public static final String MM_DD_CN = "MM月dd日";

    /**
     * M月d日 不补0  <pre>5月23日</pre>
     */
    public static final String M_D_CN = "M月d日";


    // ==================================HH:mm:ss 相关Pattern==================================

    /**
     * HH:mm:ss  <pre>17:26:30</pre>
     */
    public static final String HH_MM_SS = "HH:mm:ss";

    /**
     * H:m:s  <pre>17:6:30</pre>
     */
    public static final String H_M_S = "H:m:s";

    /**
     * HHmmss  <pre>170630</pre>
     */
    public static final String HHMMSS = "HHmmss";

    /**
     * HH时mm分ss秒  <pre>17时06分30秒</pre>
     */
    public static final String HH_MM_SS_CN = "HH时mm分ss秒";

    /**
     * HH:mm  <pre>17:06</pre>
     */
    public static final String HH_MM = "HH:mm";

    /**
     * H:m  <pre>17:6</pre>
     */
    public static final String H_M = "H:m";

    /**
     * HH时mm分 <pre>17时06分</pre>
     */
    public static final String HH_MM_CN = "HH时mm分";

    /**
     * hh:mm a <pre>05:06 下午 如果需要 显示PM 需要设置 Locale.ENGLISH</pre>
     */
    public static final String HH_MM_A = "hh:mm a";

    // ==================================HH:mm:ss.SSS 相关Pattern==================================

    /**
     * HH:mm:ss.SSS  <pre>17:26:30.272</pre>
     */
    public static final String HH_MM_SS_SSS = "HH:mm:ss.SSS";


    // ==================================HH:mm:ss.SSSSSS 相关Pattern==================================


    // ==================================yyyy-MM-dd HH:mm:ss 相关Pattern==================================

    /**
     * yyyy-MM-dd HH:mm:ss <pre>2020-05-23 17:06:30</pre>
     */
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * yyyy-M-d H:m:s <pre>2020-5-23 17:6:30</pre>
     */
    public static final String YYYY_M_D_H_M_S = "yyyy-M-d H:m:s";

    /**
     * yyyyMMddHHmmss <pre>20200523170630</pre>
     */
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    /**
     * yyyy/MM/dd HH:mm:ss <pre>2020/05/23 17:06:30</pre>
     */
    public static final String YYYY_MM_DD_HH_MM_SS_EN = "yyyy/MM/dd HH:mm:ss";

    /**
     * yyyy/M/d H:m:s <pre>2020/5/23 17:6:30</pre>
     */
    public static final String YYYY_M_D_H_M_S_EN = "yyyy/M/d H:m:s";

    /**
     * yyyy年MM月dd日 HH:mm:ss <pre>2020年05月23日 17:06:30</pre>
     */
    public static final String YYYY_MM_DD_HH_MM_SS_CN = "yyyy年MM月dd日 HH:mm:ss";

    /**
     * yyyy年MM月dd日 HH时mm分ss秒 <pre>2020年05月23日 17时06分30秒</pre>
     */
    public static final String YYYY_MM_DD_HH_MM_SS_CN_ALL = "yyyy年MM月dd日 HH时mm分ss秒";

    /**
     * yyyy-MM-dd HH:mm <pre>2020-05-23 17:06</pre>
     */
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    /**
     * yyyy-M-d H:m <pre>2020-5-23 17:6</pre>
     */
    public static final String YYYY_M_D_H_M = "yyyy-M-d H:m";

    /**
     * yyyyMMddHHmm <pre>202005231706</pre>
     */
    public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";

    /**
     * yyyy/MM/dd HH:mm <pre>2020/05/23 17:06</pre>
     */
    public static final String YYYY_MM_DD_HH_MM_EN = "yyyy/MM/dd HH:mm";

    /**
     * yyyy/M/d H:m <pre>2020/5/23 17:6</pre>
     */
    public static final String YYYY_M_D_H_M_EN = "yyyy/M/d H:m";

    /**
     * yyyy/M/d h:m a <pre>2020/5/23 5:6 下午</pre>
     */
    public static final String YYYY_M_D_H_M_A_EN = "yyyy/M/d h:m a";

    /**
     * MM-dd HH:mm <pre>05-23 17:06</pre>
     */
    public static final String MM_DD_HH_MM = "MM-dd HH:mm";

    /**
     * MM月dd日 HH:mm <pre>05月23日 17:06</pre>
     */
    public static final String MM_DD_HH_MM_CN = "MM月dd日 HH:mm";

    /**
     * MM-dd HH:mm:ss <pre>05-23 17:06:30</pre>
     */
    public static final String MM_DD_HH_MM_SS = "MM-dd HH:mm:ss";

    /**
     * MM月dd日 HH:mm:ss <pre>05月23日 17:06:30</pre>
     */
    public static final String MM_DD_HH_MM_SS_CN = "MM月dd日 HH:mm:ss";

    /**
     * yyyy年MM月dd日 hh:mm:ss a <pre>2020年05月23日 05:06:30 下午  如果需要 显示PM 需要设置 Locale.ENGLISH</pre>
     */
    public static final String YYYY_MM_DD_HH_MM_SS_A_CN = "yyyy年MM月dd日 hh:mm:ss a";

    /**
     * yyyy年MM月dd日 hh时mm分ss秒 a <pre>2020年05月23日 17时06分30秒 下午  如果需要 显示PM 需要设置 Locale.ENGLISH</pre>
     */
    public static final String YYYY_MM_DD_HH_MM_SS_A_CN_ALL = "yyyy年MM月dd日 hh时mm分ss秒 a";


    // ==================================yyyy-MM-dd HH:mm:ss.SSS 相关Pattern==================================


    /**
     * yyyy-M-d H:m:s.SSS <pre>2020-5-23 17:6:30.272</pre>
     */
    public static final String YYYY_M_D_H_M_S_SSS = "yyyy-M-d H:m:s.SSS";

    /**
     * yyyy/M/d H:m:s.SSS <pre>2020/5/23 17:6:30.272</pre>
     */
    public static final String YYYY_M_D_H_M_S_SSS_EN = "yyyy/M/d H:m:s.SSS";

    /**
     * yyyy-M-d H:m:s,SSS <pre>2020-5-23 17:6:30,272</pre>
     */
    public static final String YYYY_M_D_H_M_S_SSS_COMMA = "yyyy-M-d H:m:s,SSS";


    // ==================================yyyy-MM-dd HH:mm:ss.SSSSSS 相关Pattern==================================


    /**
     * yyyy-MM-dd'T'HH:mm:ssxxx
     * <pre>2020-05-23T17:06:30+08:00
     * 2020-05-23T09:06:30+00:00 0时区时末尾 为+00:00
     * </pre>
     */
    public static final String YYYY_MM_DD_T_HH_MM_SS_XXX = "yyyy-MM-dd'T'HH:mm:ssxxx";


    public static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

}
