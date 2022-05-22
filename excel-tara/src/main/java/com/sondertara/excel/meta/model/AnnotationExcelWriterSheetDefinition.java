package com.sondertara.excel.meta.model;


import com.sondertara.excel.ColorUtils;
import com.sondertara.excel.exception.ExcelException;
import com.sondertara.excel.exception.ExcelWriterException;
import com.sondertara.excel.meta.annotation.CellRange;
import com.sondertara.excel.meta.annotation.ExcelComplexHeader;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 */
public class AnnotationExcelWriterSheetDefinition<T> implements ExcelWriterSheetDefinition {
    private Class<T> cls;
    private Map<Integer, Field> columnFields;
    private int firstDataRow = 1;
    private List<?> rowDatas;
    private int order;
    private String sheeName;
    private int maxRowsPerSheet;
    private boolean isRowStriped;
    private Color rowStripeColor;
    private int titleRowHeight;
    private int dataRowHeight;
    private Map<Integer, ExcelCellStyleDefinition> columnCellStyles;


    public AnnotationExcelWriterSheetDefinition(Class<T> clazz, List<?> rowDatas) {
        this.cls = clazz;
        this.columnFields = new HashMap<>();
        this.rowDatas = rowDatas;

        init();
    }

    private void init() {
        initSheetMeta();
        initColumnFields();
        this.firstDataRow = calFirstDataRow();

    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> clazz) {
        return (A)this.cls.getAnnotation(clazz);
    }

    @Override
    public Class<T> getBindingModel() {
        return this.cls;
    }

    @Override
    public Map<Integer, Field> getColumnFields() {
        return this.columnFields;
    }

    @Override
    public Map<Integer, String> getColumnTitles() {
        return null;
    }

    @Override
    public int getFirstDataRow() {
        return this.firstDataRow;
    }

    @Override
    public List<?> getRowDatas() {
        return this.rowDatas;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public String getSheetName() {
        return this.sheeName;
    }

    @Override
    public void setSheetName(String sheetName) {
        this.sheeName = sheetName;
    }

    @Override
    public int getMaxRowsPerSheet() {
        return this.maxRowsPerSheet;
    }

    @Override
    public boolean isRowStriped() {
        return this.isRowStriped;
    }

    @Override
    public Color getRowStripeColor() {
        return this.rowStripeColor;
    }

    @Override
    public int getTitleRowHeight() {
        return this.titleRowHeight;
    }

    @Override
    public int getDataRowHeight() {
        return this.dataRowHeight;
    }

    @Override
    public Map<Integer, ExcelCellStyleDefinition> getColumnCellStyles(Workbook workbook) {
        if (columnCellStyles == null) {
            columnCellStyles = new HashMap<>();

            if (this.isRowStriped) {
                // 单双行样式
                for (Map.Entry<Integer, Field> columnFieldEntry : columnFields.entrySet()) {
                    CellStyle oddCellStyle = workbook.createCellStyle();
                    ((XSSFCellStyle) oddCellStyle).setFillForegroundColor(new XSSFColor(this.rowStripeColor, null));
                    oddCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    columnCellStyles.put(columnFieldEntry.getKey() * 2 - 1, new ExcelCellStyleDefinition(oddCellStyle, workbook.createFont()));

                    columnCellStyles.put(columnFieldEntry.getKey() * 2, new ExcelCellStyleDefinition(workbook.createCellStyle(), workbook.createFont()));
                }
            } else {
                for (Map.Entry<Integer, Field> columnFieldEntry : columnFields.entrySet()) {
                    columnCellStyles.put(columnFieldEntry.getKey(), new ExcelCellStyleDefinition(workbook.createCellStyle(), workbook.createFont()));
                }
            }
        }
        return columnCellStyles;
    }

    private void initColumnFields() {
        Field[] fields = this.cls.getDeclaredFields();
        for (Field field : fields) {
            ExcelExportField exportColumn = field.getAnnotation(ExcelExportField.class);
            if (exportColumn != null) {
                if (exportColumn.colIndex() < 1) {
                    throw new ExcelException("The @ExcelExportColumn on Field [" + field.getName() + "] of Class[" + this.cls.getCanonicalName() + "] miss \"colIndex\" attribute or less than 1 !");
                }

                // exists colIndex
                if (this.columnFields.containsKey(exportColumn.colIndex())) {
                    throw new ExcelException("The @ExcelExportColumn on Field [" + field.getName() + "] of Class[" + this.cls.getCanonicalName() + "] has conflicting \"colIndex\" value => [" + exportColumn.colIndex() + "] !");
                }

                field.setAccessible(true);
                this.columnFields.put(exportColumn.colIndex(), field);
            }
        }
    }

    private void initSheetMeta() {
        ExcelExport excelExport = this.cls.getAnnotation(ExcelExport.class);
        if (excelExport == null) {
            throw new ExcelWriterException("Class[" + this.cls.getCanonicalName() + "] miss @ExcelExport!");
        }
        this.order = excelExport.order();
        this.sheeName = excelExport.sheetName();
        this.maxRowsPerSheet = excelExport.maxRowsPerSheet();
        this.isRowStriped = excelExport.rowStriped();
        if (this.isRowStriped && !StringUtils.isBlank(excelExport.rowStripeColor())) {
            this.rowStripeColor = ColorUtils.hexToRgb(excelExport.rowStripeColor());
        }
        this.titleRowHeight = excelExport.titleRowHeight();
        this.dataRowHeight = excelExport.dataRowHeight();

    }

    /**
     * 根据复杂表头，计算出数据起始行号
     *
     * @return
     */
    private int calFirstDataRow() {
        ExcelComplexHeader complexHeader = this.cls.getAnnotation(ExcelComplexHeader.class);
        if (complexHeader != null) {
            CellRange[] cellRanges = complexHeader.value();
            int startRow = 1;
            for (CellRange cellRange : cellRanges) {
                if (cellRange.lastRow() > startRow) {
                    startRow = cellRange.lastRow();
                }
            }
            return (startRow + 1);
        }
        return 1;
    }


    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(ExcelSheetDefinition o) {
        AnnotationExcelWriterSheetDefinition<T> sheetDefinition = (AnnotationExcelWriterSheetDefinition<T>) o;
        if (this.order == sheetDefinition.getOrder()) {
            return 0;
        }
        if (this.order > sheetDefinition.getOrder()) {
            return 1;
        }
        return -1;
    }
}
