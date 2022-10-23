package com.sondertara.excel.boot;

import com.sondertara.excel.context.AnnotationExcelWriterContext;
import com.sondertara.excel.resolver.builder.AbstractExcelWriter;
import com.sondertara.excel.resolver.builder.DataCollectionBuilder;
import com.sondertara.excel.resolver.builder.DateQueryBuilder;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author huangxiaohu
 */

public class ExcelBeanWriter extends AbstractExcelWriter<Workbook> {

    public ExcelBeanWriter() {
        super(new AnnotationExcelWriterContext());
    }

    public static DateQueryBuilder<Workbook> fromQuery() {

        return new DateQueryBuilder<>(new ExcelBeanWriter());

    }

    public static DataCollectionBuilder<Workbook> fromData() {
        return new DataCollectionBuilder<>(new ExcelBeanWriter());
    }

    @Override
    public Workbook generate() {
        return this.getWriterContext().getExecutor().execute();
    }
}
