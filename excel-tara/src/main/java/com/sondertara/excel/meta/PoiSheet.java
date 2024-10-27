package com.sondertara.excel.meta;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/20 16:39
 */
public class PoiSheet implements MySheet {

    private final SXSSFSheet sxssfSheet;

    public PoiSheet(SXSSFSheet sxssfSheet) {
        this.sxssfSheet = sxssfSheet;
    }

    @Override
    public SXSSFSheet getRaw() {
        return sxssfSheet;
    }

    @Override
    public MyRow getRow(int rowNum) {
        return null;
    }

    @Override
    public MyRow createRow(int rowNum) {
        return null;
    }

    @Override
    public void addMergedRegion(CellRangeAddress cellRangeAddress) {

    }

    @Override
    public void addValidationData(DataValidation dataValidation) {

    }

    @Override
    public void setColumnWidth(int colIndex, int min) {

    }

    @Override
    public int getLastRowNum() {
        return 0;
    }
}
