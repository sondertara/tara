package com.sondertara.excel.enums;


/**
 * ExcelImport bind type
 *
 * @author huangxiaohu
 * @see com.sondertara.excel.meta.annotation.ExcelImport
 */

public enum ExcelColBindType {
    /**
     * Bind column by the filed definition order
     */
    ORDER,
    /**
     * Bind column by col index
     */
    COL_INDEX,
    /**
     * Bind column by col title,just for import
     */
    TITLE;

    ExcelColBindType() {

    }
}