package com.sondertara.excel.meta;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.dhatim.fastexcel.Workbook;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/20 17:06
 */
public class FastWorkbook implements MyWorkBook {

    private final Workbook workbook;

    public FastWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public Workbook getRaw() {
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
