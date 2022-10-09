package com.sondertara.excel.meta.model;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Slf4j
public class AnnotationRowDef extends ExcelRowDef {

    private final AnnotationSheet curSheet;

    public AnnotationRowDef(AnnotationSheet curSheet) {
        this.curSheet = curSheet;
    }

    public boolean validate() {
        final List<ExcelCellDef> excelCells = this.getExcelCells();
        final Map<Integer, Field> columnFields = this.curSheet.getColFields();
        boolean allPassed = true;
        for (final Map.Entry<Integer, Field> columnFieldEntity : columnFields.entrySet()) {
            final Field field = columnFieldEntity.getValue();
            final ExcelCellDef cell = getCell(excelCells, columnFieldEntity.getKey());
            try {
                AnnotationCellDef cellDef = AnnotationCellDef.of(cell, field);
                cellDef.validate();
                return true;
            } catch (final Exception ex) {
                allPassed = false;
                log.error("Error while validating column:{}", ex.getMessage(), ex);
                throw new RuntimeException(ex);

            }
        }
        return allPassed;
    }

    private ExcelCellDef getCell(final List<ExcelCellDef> excelCells, final int colIndex) {
        for (final ExcelCellDef excelCell : excelCells) {
            if (excelCell.getColIndex() == colIndex) {
                return excelCell;
            }
        }
        final ExcelCellDef cloneCellDefinition = cloneExcelCellDefinition(excelCells.get(0));
        cloneCellDefinition.setColIndex(colIndex);
        return cloneCellDefinition;
    }

    private ExcelCellDef cloneExcelCellDefinition(final ExcelCellDef cellDefinition) {
        final ExcelCellDef _cellDefinition = new ExcelCellDef();
        _cellDefinition.setSheetIndex(cellDefinition.getSheetIndex());
        _cellDefinition.setColIndex(cellDefinition.getColIndex());
        _cellDefinition.setRowIndex(cellDefinition.getRowIndex());
        return _cellDefinition;
    }

}
