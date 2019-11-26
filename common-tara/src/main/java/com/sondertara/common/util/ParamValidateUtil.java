package com.sondertara.common.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 请求参数校验工具类
 *
 * @author fanqijun
 * @date 2018-10-12 11:29
 */
public class ParamValidateUtil {

    private static Pattern NUMBER_PATTERN = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$");
    private static Pattern INTEGER_PATTERN = Pattern.compile("-?[0-9]+");
    private static Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("[0-9]+");

    /**
     * 校验参数是否为空
     *
     * @param o          校验对象
     * @param desc       错误描述
     * @param descSuffix 错误描述后缀
     * @throws Exception
     */
    public static void notNull(Object o, String desc, String descSuffix) throws Exception {
        if (o == null) {
            String message = String.format("%s" + descSuffix, desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验参数是否为空串
     *
     * @param s          校验对象
     * @param desc       错误描述
     * @param descSuffix 错误描述后缀
     * @throws Exception
     */
    public static void notEmptyString(String s, String desc, String descSuffix) throws Exception {
        notNull(s, desc, descSuffix);
        if (StringUtil.isEmpty(s)) {
            String message = String.format("%s" + descSuffix, desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验参数是否为逻辑值(0 or 1)
     *
     * @param i          校验对象
     * @param desc       错误描述
     * @param descSuffix 错误描述后缀
     * @throws Exception
     */
    public static void isLogicValue(Integer i, String desc, String descSuffix) throws Exception {
        notNull(i, desc, descSuffix);
        if (i != 0 && i != 1) {
            String message = String.format("%s" + descSuffix, desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验参数是否为1或2
     *
     * @param i          校验对象
     * @param desc       错误描述
     * @param descSuffix 错误描述后缀
     * @throws Exception
     */
    public static void isOneOrTwo(Integer i, String desc, String descSuffix) throws Exception {
        notNull(i, desc, descSuffix);
        if (i != 1 && i != 2) {
            String message = String.format("%s" + descSuffix, desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验参数是否为数字
     *
     * @param o          校验对象
     * @param desc       错误描述
     * @param descSuffix 错误描述后缀
     * @throws Exception
     */
    public static void isNumber(Object o, String desc, String descSuffix) throws Exception {
        notNull(o, desc, descSuffix);
        Matcher isNum = NUMBER_PATTERN.matcher(o.toString());
        if (!isNum.matches()) {
            String message = String.format("%s" + descSuffix, desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验参数是否为整数
     *
     * @param o          校验对象
     * @param desc       错误描述
     * @param descSuffix 错误描述后缀
     * @throws Exception
     */
    public static void isInteger(Object o, String desc, String descSuffix) throws Exception {
        notNull(o, desc, descSuffix);
        Matcher isNum = INTEGER_PATTERN.matcher(o.toString());
        if (!isNum.matches()) {
            String message = String.format("%s" + descSuffix, desc);
            throw new Exception(message);
        }
    }

    /**
     * 校验参数是否为正整数
     *
     * @param o          校验对象
     * @param desc       错误描述
     * @param descSuffix 错误描述后缀
     * @throws Exception
     */
    public static void isPositiveInteger(Object o, String desc, String descSuffix) throws Exception {
        notNull(o, desc, descSuffix);
        Matcher isNum = POSITIVE_INTEGER_PATTERN.matcher(o.toString());
        if (!isNum.matches()) {
            String message = String.format("%s" + descSuffix, desc);
            throw new Exception(message);
        }
    }

}
