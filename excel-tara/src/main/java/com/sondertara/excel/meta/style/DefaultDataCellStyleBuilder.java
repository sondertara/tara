package com.sondertara.excel.meta.style;

import com.sondertara.excel.meta.model.ExcelCellStyleDefinition;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public class DefaultDataCellStyleBuilder implements CellStyleBuilder {

    @Override
    public CellStyle build(Workbook workbook, ExcelCellStyleDefinition cellStyleDefinition, Cell cell) {
        CellStyle cellStyle = cellStyleDefinition.getCellStyle();

        // 设置对齐方式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return cellStyle;
    }
}
