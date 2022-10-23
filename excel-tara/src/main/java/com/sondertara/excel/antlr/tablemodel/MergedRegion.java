package com.sondertara.excel.antlr.tablemodel;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangxiaohu
 */
@Data
public class MergedRegion implements Serializable {

    private static final long serialVersionUID = 5277762596563003264L;

    /**
     * start from 1.
     */
    private int firstRowNum;
    private int lastRowNum;

    /**
     * start from "A"
     */
    private String firstColName;
    private String lastColName;

    public MergedRegion() {
    }

    public MergedRegion(int firstRowNum, int lastRowNum, String firstColName, String lastColName) {
        this.firstRowNum = firstRowNum;
        this.lastRowNum = lastRowNum;
        this.firstColName = firstColName;
        this.lastColName = lastColName;
    }

}
