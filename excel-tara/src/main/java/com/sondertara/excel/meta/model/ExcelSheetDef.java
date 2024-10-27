package com.sondertara.excel.meta.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangxiaohu
 */
@Data
public class ExcelSheetDef implements Serializable {
    /**
     * sheet索引 zero-based
     */

    private Integer sheetIndex;

    private String sheetName;
    private Integer totalRow;

    private boolean date1904;
}
