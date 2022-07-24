package com.sondertara.excel.meta.model;


import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.function.ExportFunction;
import org.apache.poi.ss.usermodel.Workbook;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public interface ExcelWriterSheetDefinitionBak1 extends ExcelSheetDefinition {
    /**
     * row data
     *
     * @return rows
     */
    List<?> getRows();

    ExcelDataType dataType();

    int getOrder();

    String getSheetName();

    void setSheetName(String sheetName);

    int getMaxRowsPerSheet();

    boolean isRowStriped();

    Color getRowStripeColor();

    int getTitleRowHeight();

    int getDataRowHeight();

    Map<Integer, ExcelCellStyleDefinition> getColumnCellStyles(Workbook workbook);

    ExportFunction<?> getQueryFunction();


   PageQueryParam getPagination();


}
