package com.sondertara.excel.meta.style;

import com.sondertara.excel.meta.model.ExcelCellStyleDefinition;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author chenzw
 */
public interface CellStyleBuilder {

    CellStyle build(Workbook workbook, ExcelCellStyleDefinition cellStyleDefinition, Cell cell);
}
