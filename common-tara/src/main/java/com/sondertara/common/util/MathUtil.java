package com.sondertara.common.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: chenxinshi
 * @Since: 2018/7/3
 * @desc:
 */
public class MathUtil {

    private static Pattern NUMBER_PATTERN = Pattern.compile("^[1-9][0-9]*$");

    private static Pattern pattern = Pattern.compile("[A-Z0-9]{17}");

    /**
     * 分转换成元
     *
     * @param i
     * @return
     */
    public static Double fenToYuan(Integer i) {
        if (i == null) {
            return null;
        }
        return i / 100.0;
    }

    /**
     * 获取随机数
     *
     * @param num 位数
     * @return 随机数
     */
    public static String getRandomNumber(int num) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        Matcher isNum = NUMBER_PATTERN.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 校验对象是为0或1
     *
     * @param s 校验对象
     */
    public static Boolean isLogicValue(String s) {

        Integer i = null;
        try {
            i = Integer.parseInt(s);
            if (i == 0 || i == 1) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    public static void main(String[] args) {

        String s = "东风标致LWVDA2020HB056766多用途LWVDA2020HB056666乘用车";

        Matcher matcher = pattern.matcher(s);


        System.out.println(matcher.matches());

        matcher.find();
        System.out.println(matcher.group(0));


    }
}
