package org.cherubim.common.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: chenxinshi
 * @Since: 2018/7/8
 * @desc:
 */
public class ValidateUtil {

    private static Pattern NUMBER_PATTERN = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$");
    private static Pattern POSITIVE_NUMBER_PATTERN = Pattern.compile("[0-9]+.?[0-9]+");
    private static Pattern INTEGER_PATTERN = Pattern.compile("-?[0-9]+");
    private static Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("[0-9]+");

    /**
     * 校验是否为null
     *
     * @param o 校验的对象
     * @param desc 对象描述
     * @throws Exception 异常信息
     */
    public static void notNull(Object o, String desc) throws Exception {
        if (o == null) {
            String message = String.format("%s is null", desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验是否为空字符串
     *
     * @param s 校验的字符串
     * @param desc 对象描述
     * @throws Exception 异常信息
     */
    public static void notEmptyString(String s, String desc) throws Exception {
        notNull(s, desc);
        if (StringUtil.isEmpty(s)) {
            String message = String.format("%s is empty string", desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验对象是为0或1
     *
     * @param i 校验对象
     * @param desc 对象描述
     * @throws Exception 异常信息
     */
    public static void isLogicValue(Integer i, String desc) throws Exception {
        notNull(i, desc);
        if (i != 0 && i != 1) {
            String message = String.format("%s must be 0 or 1", desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验对象是否为1或2
     *
     * @param i 校验对象
     * @param desc 对象描述
     * @throws Exception 异常信息
     */
    public static void isOneOrTwo(Integer i, String desc) throws Exception {
        notNull(i, desc);
        if (i != 1 && i != 2) {
            String message = String.format("%s must be 1 or 2", desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验对象是否为数值
     *
     * @param o 校验对象
     * @param desc 对象描述
     * @throws Exception 异常信息
     */
    public static void isNumber(Object o, String desc) throws Exception {
        notNull(o, desc);
        Matcher isNum = NUMBER_PATTERN.matcher(o.toString());
        if (!isNum.matches()) {
            String message = String.format("%s is not a number", desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验对象是否为整数
     *
     * @param o 校验对象
     * @param desc 对象描述
     * @throws Exception 异常信息
     */
    public static void isInteger(Object o, String desc) throws Exception {
        notNull(o, desc);
        Matcher isNum = INTEGER_PATTERN.matcher(o.toString());
        if (!isNum.matches()) {
            String message = String.format("%s is not a integer", desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验对象是否为正整数
     *
     * @param o 校验对象
     * @param desc 对象描述
     * @throws Exception 异常信息
     */
    public static void isPositiveInteger(Object o, String desc) throws Exception {
        notNull(o, desc);
        Matcher isNum = POSITIVE_INTEGER_PATTERN.matcher(o.toString());
        if (!isNum.matches()) {
            String message = String.format("%s is not a positive integer", desc);
            throw new Exception(message);
        }
    }

}
