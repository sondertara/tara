package com.sondertara.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @since 1.0
 * date 2019-03-19 16:25
 */
public class LocalDateTimeUtil {


    public final static Logger log = LoggerFactory.getLogger(LocalDateTimeUtil.class);

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

    public static LocalDateTime convertLocalDateTime(String dateTimeStr, String formatter) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
            return LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
        } catch (Exception e) {
            log.error("parse time error,dateTimeStr==>{}", dateTimeStr, e);
        }
        return null;
    }


    public static LocalDateTime convertToLocalDateTime(String dateTimeStr) {
        DateTimeFormatter formatter;
        LocalDateTime localDateTime;
        try {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            return localDateTime;
        } catch (Exception e) {
            log.error("convert localDateTime error,dateTimeStr==>{}", dateTimeStr, e);
        }

        try {
            formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            return localDateTime;
        } catch (Exception e) {
            log.error("convert localDateTime error,dateTimeStr==>{}", dateTimeStr, e);
        }

        try {
            formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
            localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            return localDateTime;
        } catch (Exception e) {
            log.error("convert localDateTime error,dateTimeStr==>{}", dateTimeStr, e);
        }

        return null;
    }

    /**
     * @param date    jdk8之前的date
     * @param pattern 格式化
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

    public static Date parse(String dateTimeStr) {
        if (dateTimeStr == null) {
            return null;
        }
        Date date = null;
        try {
            LocalDateTime localDateTime = convertToLocalDateTime(dateTimeStr);
            assert localDateTime != null;
            date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            log.error("parse date error,dateTimeStr==>{}", dateTimeStr, e);
        }
        if (date == null) {
            try {
                final LocalDate parse = convertToLocalDate(dateTimeStr);
                date = Date.from(parse.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            } catch (Exception e) {
                log.error("parse date error,dateTimeStr==>{}", dateTimeStr, e);
            }

        }
        return date;
    }

    public static LocalDate convertToLocalDate(String date) {
        DateTimeFormatter formatter;
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
