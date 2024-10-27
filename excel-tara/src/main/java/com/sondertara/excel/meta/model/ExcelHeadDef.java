package com.sondertara.excel.meta.model;

import lombok.Data;

import java.io.Serializable;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/9/27 14:40
 */
@Data
public class ExcelHeadDef implements Serializable {

    private String name;
    int rowSpan; // 表示跨行数
    int colSpan; // 表示跨列数
   private int firstCol;
   private int firstRow;

    public ExcelHeadDef(String name, int firstCol, int firstRow) {
        this.name = name;
        this.firstCol = firstCol;
        this.firstRow = firstRow;
    }
}
