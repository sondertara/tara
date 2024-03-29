package com.sondertara.excel.meta.model;

import com.sondertara.common.util.CollectionUtils;
import com.sondertara.excel.enums.ExcelColBindType;
import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.exception.ExcelAnnotationWriterException;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.meta.annotation.CellRange;
import com.sondertara.excel.meta.annotation.ExcelComplexHeader;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import com.sondertara.excel.utils.ColorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author huangxiaohu
 */
public class AnnotationExcelWriterSheetDefinition<T> extends AnnotationSheet {


    private int maxRowsPerSheet;
    private boolean isRowStriped;
    private Color rowStripeColor;
    private int titleRowHeight;
    private int dataRowHeight;

    private Map<Integer, ExcelCellStyleDefinition> columnCellStyles;

    public AnnotationExcelWriterSheetDefinition(Class<T> clazz, List<?> rows) {
        super(clazz);
        if (CollectionUtils.isNotEmpty(rows)) {
            AtomicInteger i = new AtomicInteger();
            this.rows = rows.stream().map(o -> {
                i.getAndIncrement();
                TaraRow row = new TaraRow(i.get(), i.get());
                row.setRowData(o);
                return row;
            }).collect(Collectors.toList());
        }
        this.excelDataType = ExcelDataType.DIRECT;
        init();
    }

    public AnnotationExcelWriterSheetDefinition(Class<T> clazz, ExportFunction<?> exportFunction) {
        super(clazz);
        this.queryFunction = exportFunction;
        this.excelDataType = ExcelDataType.QUERY;
        init();
    }

    private void init() {
        initSheetMeta();
        initColumnFields();
        this.firstDataRow = calFirstDataRow();

    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> clazz) {
        return this.mappingClass.getAnnotation(clazz);
    }

    @Override
    public int getFirstDataRow() {
        return this.firstDataRow;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public int getMaxRowsPerSheet() {
        return this.maxRowsPerSheet;
    }

    public boolean isRowStriped() {
        return this.isRowStriped;
    }

    public Color getRowStripeColor() {
        return this.rowStripeColor;
    }

    public int getTitleRowHeight() {
        return this.titleRowHeight;
    }

    public int getDataRowHeight() {
        return this.dataRowHeight;
    }


    @Override
    public ExportFunction<?> getQueryFunction() {
        return this.queryFunction;
    }

    public Map<Integer, ExcelCellStyleDefinition> getColumnCellStyles(Workbook workbook) {
        if (columnCellStyles == null) {
            columnCellStyles = new HashMap<>();

            if (this.isRowStriped) {
                // 单双行样式
                for (Map.Entry<Integer, Field> columnFieldEntry : colFields.entrySet()) {
                    CellStyle oddCellStyle = workbook.createCellStyle();
                    oddCellStyle.setBorderBottom(BorderStyle.THIN);
                    oddCellStyle.setBorderLeft(BorderStyle.THIN);
                    oddCellStyle.setBorderRight(BorderStyle.THIN);
                    oddCellStyle.setBorderTop(BorderStyle.THIN);
                    ((XSSFCellStyle) oddCellStyle).setFillForegroundColor(new XSSFColor(this.rowStripeColor, null));
                    oddCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    columnCellStyles.put(columnFieldEntry.getKey() * 2 - 1, new ExcelCellStyleDefinition(oddCellStyle, workbook.createFont()));

                    columnCellStyles.put(columnFieldEntry.getKey() * 2, new ExcelCellStyleDefinition(workbook.createCellStyle(), workbook.createFont()));
                }
            } else {
                for (Map.Entry<Integer, Field> columnFieldEntry : colFields.entrySet()) {
                    columnCellStyles.put(columnFieldEntry.getKey(), new ExcelCellStyleDefinition(workbook.createCellStyle(), workbook.createFont()));
                }
            }
        }
        return columnCellStyles;
    }

    private void initColumnFields() {
        Field[] fields = this.mappingClass.getDeclaredFields();
        int colIndex = 1;
        for (Field field : fields) {
            ExcelExportField exportColumn = field.getAnnotation(ExcelExportField.class);
            if (exportColumn != null) {
                if (bindType.equals(ExcelColBindType.COL_INDEX)) {
                    if (exportColumn.colIndex() < 1) {
                        throw new ExcelAnnotationWriterException("The @ExcelExportColumn on Field [" + field.getName() + "] of Class[" + this.mappingClass.getCanonicalName() + "] miss \"colIndex\" attribute or less than 1 !");
                    }

                    // exists colIndex
                    if (this.colFields.containsKey(exportColumn.colIndex())) {
                        throw new ExcelAnnotationWriterException("The @ExcelExportColumn on Field [" + field.getName() + "] of Class[" + this.mappingClass.getCanonicalName() + "] has conflicting \"colIndex\" value => [" + exportColumn.colIndex() + "] !");
                    }

                    field.setAccessible(true);
                    this.colFields.put(exportColumn.colIndex(), field);
                } else {
                    field.setAccessible(true);
                    this.colFields.put(colIndex++, field);
                }
            }
        }
    }

    private void initSheetMeta() {
        ExcelExport excelExport = this.mappingClass.getAnnotation(ExcelExport.class);
        if (excelExport == null) {
            throw new ExcelAnnotationWriterException("Class[" + this.mappingClass.getCanonicalName() + "] miss @ExcelExport!");
        }
        this.order = excelExport.order();
        this.name = excelExport.sheetName();
        this.bindType = excelExport.bindType();
        this.autoColWidth = excelExport.autoWidth();
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
     * @return the data row num begin
     */
    private int calFirstDataRow() {
        ExcelComplexHeader complexHeader = this.mappingClass.getAnnotation(ExcelComplexHeader.class);
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

}
