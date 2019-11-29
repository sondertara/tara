package com.sondertara.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * date util
 *
 * @author huangxiaohu
 * @since 2019-03-19 16:25
 */
public class LocalDateTimeUtil {

    private static final String FORMATTER = "yyyy-MM-dd HH:mm:ss";

    public static String getNow() {
        return now(FORMATTER);
    }

    public static String now() {
        return getNow();
    }

    public static String now(String formatPattern) {

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        return dateTime.format(formatter);
    }

    public static LocalDateTime convertToLocalDateTime(String dateTimeStr) {

        DateTimeFormatter formatter;
        LocalDateTime localDateTime = null;
        try {
            formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
        }
        if (localDateTime == null) {
            try {
                formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
                localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            } catch (Exception e) {
            }

        }
        if (localDateTime == null) {
            try {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            } catch (Exception e) {
            }
        }
        return localDateTime;
    }

    /**
     * @param date    jdk8之前的date 
     * @param pattern
     * @return date str
     */
    public static String format(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        ;
        return localDateTime.format(formatter);
    }

    public static String format(Date date) {
        if (null == date) {
            return null;
        }
        return format(date, FORMATTER);
    }

    public static String format(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER);
        return localDateTime.format(formatter);
    }

    public static Date parse(String dateTime) {
        if (dateTime == null) {
            return null;
        }
        Date date = null;
        try {
            LocalDateTime localDateTime = convertToLocalDateTime(dateTime);
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

    public static LocalDate convertToLocalDate(String date) {
        DateTimeFormatter formatter = null;
        LocalDate parse = null;
        try {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            parse = LocalDate.parse(date, formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (parse == null) {
            try {
                formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                parse = LocalDate.parse(date, formatter);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (parse == null) {
            try {
                formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                parse = LocalDate.parse(date, formatter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parse;
    }

}
