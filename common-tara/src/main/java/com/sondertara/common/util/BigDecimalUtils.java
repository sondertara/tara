package com.sondertara.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author huangxiaohu
 */
public class BigDecimalUtils {

    private static final String TWO_DECIMAL = "0.00";

    private static final DecimalFormat df = new DecimalFormat("0.00");

    /**
     * 格式化精度,默认取两位小数
     *
     * @param bigDecimal 原始
     * @param pattern    格式化的模式
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
     * @param bigDecimal
     * @return String
     */
    public static String formatWithTwoDecimal(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }

        return df.format(bigDecimal);
    }

}
