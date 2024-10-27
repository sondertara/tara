package com.sondertara.excel.meta.style.border;

import org.apache.poi.ss.usermodel.BorderStyle;

/**
 * Provides some default borders that can be used.
 *
 * @see CellBorder
 */
public class CellBorders {

    private static final CellBorder ALL_SIDES_THIN = new CellBorder.CellBorderBuilder()
            .topBorderStyle(BorderStyle.THIN)
            .rightBorderStyle(BorderStyle.THIN)
            .bottomBorderStyle(BorderStyle.THIN)
            .leftBorderStyle(BorderStyle.THIN)
            .build();

    private CellBorders() {
        throw new IllegalStateException("Utility class");
    }

    public static CellBorder allSidesThin() {
        return ALL_SIDES_THIN;
    }
}
