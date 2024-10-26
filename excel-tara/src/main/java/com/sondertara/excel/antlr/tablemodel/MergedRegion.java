package com.sondertara.excel.antlr.tablemodel;

import com.sondertara.excel.antlr.ExcelHelper;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huangxiaohu
 */
@Data
public class MergedRegion implements Serializable {

    private static final long serialVersionUID = 5277762596563003264L;

    /**
     * start from "A"
     */
    private String firstColName;
    private String lastColName;
    /**
     * start from 0.
     */
    private int firstRow;
    private int lastRow;
    private int firstCol;
    private int lastCol;

    public MergedRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.firstCol = firstCol;
        this.lastCol = lastCol;
        this.firstColName = ExcelHelper.getColName(firstCol);
        this.lastColName =  ExcelHelper.getColName(lastCol);;
    }

    public MergedRegion() {
    }

    public MergedRegion(int firstRowNum, int lastRowNum, String firstColName, String lastColName) {
        this.firstRow = firstRowNum;
        this.lastRow = lastRowNum;
        this.firstColName = firstColName;
        this.lastColName = lastColName;
        this.firstCol = ExcelHelper.getColIndex(firstColName);
        this.lastCol = ExcelHelper.getColIndex(lastColName);
    }

    public static void main(String[] args) {
        MergedRegion region = new MergedRegion(0, 0, 1, 2);
        System.out.println(region);

    }

}
