package com.sondertara.excel.tablemodel;

import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * this class corresponds to the entire excel [workbook].
 *
 * @author Chimm Huang
 */
public class ExcelWorkbook {

    private final SXSSFWorkbook xssfWorkbook;

    public ExcelWorkbook(SXSSFWorkbook xssfWorkbook) {
        this.xssfWorkbook = xssfWorkbook;
    }

    /**
     * get the specified sheet by index
     *
     * @param index sheet index in excel. start from 0
     */
    public SheetTable getSheet(int index) {
        SXSSFSheet sheetAt = xssfWorkbook.getSheetAt(index);
        return new SheetTable(sheetAt);
    }

    /**
     * get the specified sheet by sheet name
     *
     * @param sheetName sheet name
     */
    public SheetTable getSheet(String sheetName) {
        SXSSFSheet sheet = xssfWorkbook.getSheet(sheetName);
        return new SheetTable(sheet);
    }

    /**
     * get the apache-poi object and perform custom operations
     */
    public SXSSFWorkbook getXssfWorkbook() {
        return xssfWorkbook;
    }
}
