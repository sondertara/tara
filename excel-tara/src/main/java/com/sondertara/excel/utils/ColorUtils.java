package com.sondertara.excel.utils;


import com.sondertara.common.text.StringUtils;

import java.awt.*;

/**
 * 颜色工具类
 *
 * @author
 */
public final class ColorUtils {


    public static final String EXCEL_GREEN_TITLE = "70AD47";
    public static final String EXCEL_GREEN_STRIPE = "E2EFDA";

    private ColorUtils() {
    }

    /**
     * 将十六进制颜色转RGB格式
     *
     * @param hexColor
     * @return
     */
    public static Color hexToRgb(String hexColor) {

        if (StringUtils.isBlank(hexColor)) {
            throw new IllegalArgumentException("hex color is null!");
        }

        if (StringUtils.contains(hexColor, "#")) {
            hexColor = StringUtils.split(hexColor, "#")[1];
        }
        return new Color(Integer.parseInt(hexColor, 16));
    }

}
