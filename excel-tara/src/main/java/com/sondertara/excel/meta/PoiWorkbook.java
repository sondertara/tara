package com.sondertara.excel.meta;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/20 17:04
 */
public class PoiWorkbook implements MyWorkBook{

    private final SXSSFWorkbook workbook;

    public PoiWorkbook(SXSSFWorkbook workbook) {
        this.workbook = workbook;
        this.workbook.setCompressTempFiles(true);
    }

    @Override
    public SXSSFWorkbook getRaw() {
        return workbook;
    }

    @Override
    public CellStyle createCellStyle() {
        return null;
    }

    @Override
    public Font createFont() {
        return null;
    }
}
