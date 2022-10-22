package com.sondertara.excel.boot;

import com.sondertara.excel.context.AnnotationExcelWriterContext;
import com.sondertara.excel.resolver.builder.AbstractExcelWriter;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author huangxiaohu
 */
public class ExcelTemplateWriter extends AbstractExcelWriter<Workbook> {

    public ExcelTemplateWriter(Class<?> ...excelClass) {
        super(new AnnotationExcelWriterContext());
       this.getWriterContext().addMapper(excelClass);
    }

    public static ExcelTemplateWriter mapper(Class<?> ...excelClass) {
        return new ExcelTemplateWriter(excelClass);
    }

    @Override
    public Workbook generate() {
        // ExcelWriteSheetEntity excelMapping =
        // ExcelMappingFactory.loadExportExcelClass(excelClass);
        // ExcelTemplateWriterResolver resolver = new
        // ExcelTemplateWriterResolver(excelMapping);
        // return resolver.generateTemplate();
        return this.getWriterContext().getExecutor().execute();

    }

}
