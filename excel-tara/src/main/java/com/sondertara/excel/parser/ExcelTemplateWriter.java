package com.sondertara.excel.parser;

import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.factory.ExcelMappingFactory;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public class ExcelTemplateWriter extends AbstractExcelWriter<Workbook> {

    private final Map<Integer, Integer> columnWidthMap = new HashMap<>();


    public ExcelTemplateWriter(Class<?> excelClass) {
        super(excelClass);
    }

    public static ExcelTemplateWriter mapper(Class<?> excelClass) {
        return new ExcelTemplateWriter(excelClass);
    }

    @Override
    public Workbook generate() {
        ExcelWriteSheetEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(excelClass);
        ExcelTemplateWriterResolver resolver = new ExcelTemplateWriterResolver(excelMapping);
        return resolver.generateTemplate();
    }

}
