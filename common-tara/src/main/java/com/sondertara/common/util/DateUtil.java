package com.sondertara.common.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * @author huangxiaohu
 */
public class DateUtil {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";


    private static final LoadingCache<String, SimpleDateFormat> LOAD_CACHE =
            CacheBuilder.newBuilder()
                    .maximumSize(5)
                    .build(new CacheLoader<String, SimpleDateFormat>() {
                        @Override
                        public SimpleDateFormat load(String pattern) {
                            return new SimpleDateFormat(pattern);
                        }
                    });

    public static Date parse(String pattern, String value) throws ExecutionException, ParseException {
        return LOAD_CACHE.get(pattern).parse(value);
    }

    public static String format(String pattern, Date value) throws ExecutionException {
        return LOAD_CACHE.get(pattern).format(value);
    }

    /**
     * 判断两个时间内的时间间隔（毫秒）
     *
     * @param startDate 开始时间
     * @param endDate 终止时间
     */
    public static long afterTime(Date startDate, Date endDate) {
        if (startDate != null & endDate != null) {
            return endDate.getTime() - startDate.getTime();
        } else {
            return 0;
        }
    }



    /**
     * 尝试转换日期
     *
     * @param dateString 日期字符串
     * @return 转换后的date，尝试转换失败时返回null
     */
    public static Date parse(String dateString) throws ExecutionException {
        Date date = null;
        if (date == null) {

            try {
                date = LOAD_CACHE.get("yyyy-MM-dd HH:mm:ss").parse(dateString);
            } catch (Exception e) {
            }
        }
        if (date == null) {
            try {
                date = LOAD_CACHE.get("yyyy-MM-dd").parse(dateString);
            } catch (Exception e) {
            }
        }
        if (date == null) {
            try {
                date = LOAD_CACHE.get("yyyy-MM").parse(dateString);
            } catch (Exception e) {
            }
        }
        if (date == null) {
            try {
                date = LOAD_CACHE.get("yyyy/MM/dd HH:mm:ss").parse(dateString);
            } catch (Exception e) {
            }
        }
        if (date == null) {
            try {
                date = LOAD_CACHE.get("yyyy/MM/dd").parse(dateString);
            } catch (Exception e) {
            }
        }
        if (date == null) {
            try {
                date = LOAD_CACHE.get("yyyy/MM").parse(dateString);
            } catch (Exception e) {
            }
        }
        return date;
    }

    /**
     * 格式化日期
     *
     * @param date   日期
     * @param format 格式
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date, String format) throws ExecutionException {
        if (date == null) {
            return null;
        }
        return LOAD_CACHE.get(format).format(date);

    }

    /**
     * 以️"yyyy-MM-dd HH:mm:ss"格式化日期
     *
     * @param date 日期
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date) throws ExecutionException {
        return LOAD_CACHE.get(PATTERN).format(date);
    }

    /**
     * 时间加n年
     *
     * @param date 基准时间
     * @param n    增加的年数
     * @return 基准时间加n年后的时间
     */
    public static Date addYear(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, n);
        return cal.getTime();
    }

    /**
     * 时间加n小时
     *
     * @param date 基准时间
     * @param n    小时数
     * @return 基准时间加n小时后的时间
     */
    public static Date addHour(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, n);
        return cal.getTime();
    }

    /**
     * 时间加n分钟
     *
     * @param date 基准时间
     * @param n    分钟数
     * @return 基准时间加n分钟后的时间
     */
    public static Date addMinute(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, n);
        return cal.getTime();
    }



    //日期转为字符串，格式为月日
    public static String dateToMD(Date d) {
        if (d == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int month = calendar.get((Calendar.MONTH));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return (month + 1) + "月" + day + "日";
    }

    //日期转为年月日
    public static String dateToStringYMM(Date d) {
        if (d == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int year = calendar.get(Calendar.YEAR);
        return year + "年" + dateToMD(d);
    }

}
