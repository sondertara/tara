package com.sondertara.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @description: BigDecimalUtils 处理数据库金额工具类
 * @author: lvqun
 * @create: 2018-10-31 14:43
 **/
public class BigDecimalUtils {

    private static final String TWO_DECIMAL = "0.00";


    private static final DecimalFormat df = new DecimalFormat("0.00");

    /**
     * 格式化精度,默认取两位小数
     *
     * @param pattern 格式化的模式
     * @return String
     */
    @Deprecated
    public static String format(BigDecimal bigDecimal, String pattern) {
        if (bigDecimal == null) {
            return null;
        }
        df.applyPattern(pattern);

        return df.format(bigDecimal);
    }

    /**
     * 格式化精度,默认取两位小数
     *
     * @return String
     */
    public static String formatWithTwoDecimal(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }

        return df.format(bigDecimal);
    }

    /**
     * 获取Double
     */
    public static Double getDoubleValue(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }

        return bigDecimal.doubleValue();
    }

    public static void main(String[] args) {
        System.out.println(format(new BigDecimal("0.1012"), BigDecimalUtils.TWO_DECIMAL));
    }

}
