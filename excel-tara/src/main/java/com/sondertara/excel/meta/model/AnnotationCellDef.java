package com.sondertara.excel.meta.model;

import com.sondertara.excel.exception.ExcelException;
import com.sondertara.excel.meta.annotation.ExcelImportField;
import com.sondertara.excel.meta.annotation.validation.ConstraintValidator;
import com.sondertara.excel.support.validator.AbstractExcelColumnValidator;
import com.sondertara.excel.support.validator.ExcelDefaultValidator;
import com.sondertara.excel.utils.CacheUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnnotationCellDef extends ExcelCellDef {
    private final Field field;

    public AnnotationCellDef(Field field) {
        this.field = field;

    }

    public static AnnotationCellDef of(ExcelCellDef excelCellDef, Field field) {
        AnnotationCellDef cellDef = new AnnotationCellDef(field);
        cellDef.setCellType(excelCellDef.getCellType());
        cellDef.setCellValue(excelCellDef.getCellValue());
        cellDef.setColIndex(excelCellDef.getColIndex());
        cellDef.setRowIndex(excelCellDef.getRowIndex());
        cellDef.setColTitle(excelCellDef.getColTitle());
        return cellDef;
    }

    public boolean validate() {
        // 空值
        if (StringUtils.isBlank(this.getCellValue())) {
            // 非空校验
            final ExcelImportField importColumn = field.getAnnotation(ExcelImportField.class);
            if (!importColumn.allowBlank()) {
                throw new ExcelException("该字段值为空!");
            }
        }

        List<AbstractExcelColumnValidator<Annotation>> columnValidators = CacheUtils.getColValidatorCache()
                .getIfPresent(field.getName());

        if (columnValidators == null) {
            columnValidators = findColumnValidators(field);
            CacheUtils.getColValidatorCache().put(field.getName(), columnValidators);
        }

        for (final AbstractExcelColumnValidator columnValidator : columnValidators) {
            if (!columnValidator.validate(this.getCellValue())) {
                throw new ExcelException("该字段数据校验不通过!");
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private List<AbstractExcelColumnValidator<Annotation>> findColumnValidators(final Field field) {
        List<AbstractExcelColumnValidator<Annotation>> columnValidators = new ArrayList<>();
        final Annotation[] annotations = field.getAnnotations();
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> aClass = annotation.annotationType();
            if (aClass.isAnnotationPresent(ConstraintValidator.class)) {
                final ConstraintValidator constraintValidator = aClass.getAnnotation(ConstraintValidator.class);
                try {
                    final AbstractExcelColumnValidator<Annotation> columnValidator = constraintValidator.validator()
                            .newInstance();
                    columnValidator.initialize(annotation);
                    columnValidators.add(columnValidator);
                } catch (final InstantiationException | IllegalAccessException e) {
                    throw new ExcelException("实例化校验器[" + constraintValidator.validator() + "]时失败!", e);
                }
            }
        }

        if (columnValidators.size() == 0) {
            columnValidators = Collections.singletonList(new ExcelDefaultValidator());
        }
        return columnValidators;
    }
}
