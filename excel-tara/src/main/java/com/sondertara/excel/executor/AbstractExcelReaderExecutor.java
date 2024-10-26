package com.sondertara.excel.executor;

import com.sondertara.common.reflect.ReflectUtils;
import com.sondertara.common.text.StringUtils;
import com.sondertara.excel.analysis.XlsxAnalysisHandler;
import com.sondertara.excel.common.constants.ExcelExportConstants;
import com.sondertara.excel.context.ExcelRawReaderContext;
import com.sondertara.excel.exception.ExcelAnnotaionReaderException;
import com.sondertara.excel.exception.ExcelReaderException;
import com.sondertara.excel.lifecycle.ExcelReaderLifecycle;
import com.sondertara.excel.meta.annotation.ExcelImportField;
import com.sondertara.excel.meta.annotation.converter.ExcelConverter;
import com.sondertara.excel.meta.annotation.validation.ConstraintValidator;
import com.sondertara.excel.meta.model.AnnotationSheet;
import com.sondertara.excel.meta.model.ExcelCellDef;
import com.sondertara.excel.meta.model.ExcelRowDef;
import com.sondertara.excel.meta.model.TaraSheet;
import com.sondertara.excel.processor.ExcelPerRowProcessor;
import com.sondertara.excel.support.converter.AbstractExcelColumnConverter;
import com.sondertara.excel.support.converter.ExcelDefaultConverter;
import com.sondertara.excel.support.validator.AbstractExcelColumnValidator;
import com.sondertara.excel.support.validator.ExcelDefaultValidator;
import com.sondertara.excel.utils.ExcelFieldUtils;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author huangxiaohu
 */

public abstract class AbstractExcelReaderExecutor<T> implements ExcelReaderLifecycle, TaraExcelExecutor {

    protected int curSheetIndex = 1;
    protected int curRowIndex;
    protected int curColIndex;
    protected int totalRows;


    protected boolean date1904;

    protected ExcelRawReaderContext<T> readerContext;
    protected volatile AnnotationSheet curSheet;


    public AbstractExcelReaderExecutor(final ExcelRawReaderContext<T> readerContext) {
        this.readerContext = readerContext;
    }

    protected abstract ExcelPerRowProcessor getExcelRowProcess();

    @Override
    public void execute() {
        Map<Integer, ? extends TaraSheet> map = readerContext.getSheetDefinitions();
        // 延迟解析比率
        ZipSecureFile.setMinInflateRatio(-1.0d);
        try (final OPCPackage pkg = OPCPackage.open(readerContext.getInputStream())) {
            final XSSFReader xssfReader = new XSSFReader(pkg);
            final XMLReader parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            final ContentHandler xlsxAnalysisHandler = new XlsxAnalysisHandler(xssfReader.getStylesTable(), xssfReader.getSharedStringsTable(), getExcelRowProcess(), readerContext.getReadListener());
            parser.setContentHandler(xlsxAnalysisHandler);
             InputStream data = xssfReader.getWorkbookData();




            final Iterator<InputStream> sheets = xssfReader.getSheetsData();
            while (sheets.hasNext()) {
                final InputStream sheet = sheets.next();
                if (map.containsKey(this.curSheetIndex)) {
                    final InputSource sheetSource = new InputSource(sheet);
                    parser.parse(sheetSource);
                }
                this.curSheetIndex++;
                sheet.close();
            }
        } catch (ExcelAnnotaionReaderException e) {
            throw new ExcelReaderException(e.getShortMessage(),e);
        } catch (final IOException | SAXException | OpenXML4JException | ParserConfigurationException e) {
            throw new ExcelReaderException(e);
        }
    }


    @Override
    public boolean isEmptyRow(final ExcelRowDef row) {
        final List<ExcelCellDef> excelCells = row.getExcelCells();
        for (final ExcelCellDef excelCell : excelCells) {
            if (!StringUtils.isBlank(excelCell.getCellValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void preSet(final ExcelRowDef row) {
        final Map<Integer, String> columnTitles = this.curSheet.getTitles();
        final List<ExcelCellDef> excelCells = row.getExcelCells();

        for (final ExcelCellDef excelCell : excelCells) {
            final String title = columnTitles.get(excelCell.getColIndex());
            excelCell.setColTitle(title);
            excelCell.setSheetIndex(this.curSheetIndex);
        }
    }

    @SneakyThrows
    @Override
    public boolean validate(final ExcelRowDef row) {
        final List<ExcelCellDef> excelCells = row.getExcelCells();
        final Map<Integer, Field> columnFields = this.curSheet.getColFields();
        boolean allPassed = true;
        for (final Map.Entry<Integer, Field> columnFieldEntity : columnFields.entrySet()) {
            this.curColIndex = columnFieldEntity.getKey();
            final Field field = columnFieldEntity.getValue();
            final ExcelCellDef cell = getCell(excelCells, columnFieldEntity.getKey());
            try {
                columnValidate(cell, field);
            } catch (final Exception ex) {
                allPassed = false;
                readerContext.getReadListener().onCellException(ex, cell, row);
            }
        }
        return allPassed;
    }

    private boolean columnValidate(final ExcelCellDef cell, final Field field) {
        // 空值
        if (StringUtils.isBlank(cell.getCellValue())) {
            // 非空校验
            final ExcelImportField importColumn = field.getAnnotation(ExcelImportField.class);
            if (!importColumn.allowBlank()) {
                throw new ExcelAnnotaionReaderException(this.curSheetIndex, this.curRowIndex, this.curColIndex, cell.getAbcColIndex(), "该字段为空");
            }
        }

        List<AbstractExcelColumnValidator<Annotation>> columnValidators = ExcelExportConstants.getColValidatorCache().get(field.getName());
        if (columnValidators == null) {
            columnValidators = findColumnValidators(field);
            ExcelExportConstants.getColValidatorCache().put(field.getName(), columnValidators);
        }

        for (final AbstractExcelColumnValidator<? extends Annotation> columnValidator : columnValidators) {
            if (!columnValidator.validate(cell.getCellValue())) {
                throw new ExcelAnnotaionReaderException(this.curSheetIndex, this.curRowIndex, this.curColIndex, cell.getAbcColIndex(), "该字段数据校验不通过!");
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void format(final ExcelRowDef row) throws Exception {
        final Map<Integer, Field> columnFields = this.curSheet.getColFields();
        final List<ExcelCellDef> excelCells = row.getExcelCells();
        boolean allPassed = true;
        T instance;
        instance = (T) ReflectUtils.newInstance(this.curSheet.getMappingClass());

        for (int i = 0; i < excelCells.size(); i++) {
            this.curColIndex = i + 1;
            final ExcelCellDef cell = excelCells.get(i);

            if (!StringUtils.isBlank(cell.getCellValue())) {
                final Field field = columnFields.get(cell.getColIndex());
                Object cellValue = cell.getCellValue();
                // 值转换
                List<AbstractExcelColumnConverter<Annotation, ?>> columnConverters =ExcelExportConstants.getColConverterCache().get(field.getName());
                if (columnConverters == null) {
                    columnConverters = findColumnConverter(field);
                    ExcelExportConstants.getColConverterCache().put(field.getName(), columnConverters);
                }

                try {
                    for (final AbstractExcelColumnConverter<Annotation, ?> columnConverter : columnConverters) {
                        cellValue = columnConverter.convert(cellValue);
                    }
                } catch (final Exception ex) {
                    allPassed = false;
                    readerContext.getReadListener().onCellException(ex, cell, row);
                    break;
                }

                try {
                    ExcelFieldUtils.setFieldValue(field, instance, cellValue, field.getAnnotation(ExcelImportField.class).dataFormat(),this.date1904);
                } catch (final Exception ex) {
                    allPassed = false;
                    readerContext.getReadListener().onCellException(new ExcelReaderException("字段赋值失败!", ex), cell, row);
                    break;
                }
            }
        }

        // 只有全部字段都解析成功，才将数据添加到返回的列表中
        if (allPassed) {
            readerContext.getReadListener().doAfterRowAnalysed(instance);
        }
    }

    /**
     * 是否标题行
     *
     * @param row
     * @return
     */
    protected boolean isTitleRow(final ExcelRowDef row) {
        if (row.getRowIndex() < this.curSheet.getFirstDataRow()) {
            this.curSheet.reConfigCol(row);
            return true;
        }
        return false;
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

    @SuppressWarnings("unchecked")
    private List<AbstractExcelColumnValidator<Annotation>> findColumnValidators(final Field field) {
        List<AbstractExcelColumnValidator<Annotation>> columnValidators = new ArrayList<>();
        final Annotation[] annotations = field.getAnnotations();
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> aClass = annotation.annotationType();
            if (aClass.isAnnotationPresent(ConstraintValidator.class)) {
                final ConstraintValidator constraintValidator = aClass.getAnnotation(ConstraintValidator.class);
                final AbstractExcelColumnValidator<Annotation> columnValidator = ReflectUtils.newInstance(constraintValidator.validator());
                columnValidator.initialize(annotation);
                columnValidators.add(columnValidator);
            }
        }

        if (columnValidators.size() == 0) {
            columnValidators = Collections.singletonList(new ExcelDefaultValidator<>());
        }
        return columnValidators;
    }

    @SuppressWarnings("unchecked")
    private List<AbstractExcelColumnConverter<Annotation, ?>> findColumnConverter(final Field field) {
        List<AbstractExcelColumnConverter<Annotation, ?>> columnConverters = new ArrayList<>();
        final Annotation[] annotations = field.getAnnotations();
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> aClass = annotation.annotationType();
            if (aClass.isAnnotationPresent(ExcelConverter.class)) {
                final ExcelConverter excelConverter = aClass.getAnnotation(ExcelConverter.class);
                AbstractExcelColumnConverter<Annotation, ?> columnConverter = ReflectUtils.newInstance(excelConverter.convertBy());
                columnConverter.initialize(annotation);
                columnConverters.add(columnConverter);
            }
        }

        if (columnConverters.size() == 0) {
            columnConverters = Collections.singletonList(new ExcelDefaultConverter());
        }

        return columnConverters;
    }
}
