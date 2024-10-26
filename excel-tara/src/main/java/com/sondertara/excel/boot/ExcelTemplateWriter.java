package com.sondertara.excel.boot;

import com.sondertara.excel.context.AnnotationExcelWriterContext;
import com.sondertara.excel.resolver.builder.AbstractExcelWriter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * @author huangxiaohu
 */
public class ExcelTemplateWriter extends AbstractExcelWriter<SXSSFWorkbook> {


    public ExcelTemplateWriter() {
        super(new AnnotationExcelWriterContext());
    }

    public static ExcelTemplateWriter create() {
        return new ExcelTemplateWriter();
    }

    public ExcelTemplateWriter mapping(Class<?>... excelClass) {
        this.getWriterContext().addMapper(excelClass);
        return this;
    }

    @Override
    public SXSSFWorkbook generate() {
        return this.getWriterContext().getResult();

    }

}
