package com.sondertara.excel.meta.model;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.meta.support.ExcelMappingFactory;
import lombok.Getter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

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


    @Getter
    private ExcelWriteSheetEntity sheetMeta;


    private Map<Integer, ExcelCellStyleDefinition> columnCellStyles;

    public AnnotationExcelWriterSheetDefinition(Class<T> clazz, List<?> rows) {
        super(clazz);
        if (CollectionUtils.isNotEmpty(rows)) {
            AtomicInteger i = new AtomicInteger();
            this.rows = rows.stream().map(o -> {
                TaraRow row = new TaraRow(i.getAndIncrement(), i.get());
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
        this.firstDataRow = hasTitle ? 2 : 1;

    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> clazz) {
        return this.mappingClass.getAnnotation(clazz);
    }


    @Override
    public int getOrder() {
        return this.order;
    }


    @Override
    public int maxRowsPerSheet() {
        return sheetMeta.getMaxRowsPerSheet();
    }

    @Override
    public ExportFunction<?> getQueryFunction() {
        return this.queryFunction;
    }

    public Map<Integer, ExcelCellStyleDefinition> getColumnCellStyles(Workbook workbook) {
        boolean rowStriped = sheetMeta.isRowStriped();
        if (columnCellStyles == null) {
            columnCellStyles = new HashMap<>();

            if (rowStriped) {
                // 单双行样式
                for (Map.Entry<Integer, Field> columnFieldEntry : colFields.entrySet()) {
                    CellStyle oddCellStyle = workbook.createCellStyle();
//                    oddCellStyle.setBorderBottom(BorderStyle.THIN);
//                    oddCellStyle.setBorderLeft(BorderStyle.THIN);
//                    oddCellStyle.setBorderRight(BorderStyle.THIN);
//                    oddCellStyle.setBorderTop(BorderStyle.THIN);
                    ((XSSFCellStyle) oddCellStyle).setFillForegroundColor(new XSSFColor(sheetMeta.getRowStripeColor(), null));
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
        this.sheetMeta.getPropertyList().forEach(e -> this.colFields.put(e.getColIndex(), e.getFieldEntity()));
    }

    private void initSheetMeta() {
        ExcelWriteSheetEntity writeSheetEntity = ExcelMappingFactory.loadExportExcelClass(this.mappingClass);
        this.sheetMeta = writeSheetEntity;
        this.order = writeSheetEntity.getOrder();
        this.name = writeSheetEntity.getSheetName();
        this.bindType = writeSheetEntity.getBindType();
    }


}
