package com.sondertara.excel.boot;

import com.sondertara.excel.context.AnnotationExcelWriterContext;
import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.parser.builder.AbstractExcelWriter;
import com.sondertara.excel.parser.builder.DataCollectionBuilder;
import com.sondertara.excel.parser.builder.DateQueryBuilder;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

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
        // ExcelWriteSheetEntity excelMapping =
        // ExcelMappingFactory.loadExportExcelClass(excelClass);
        // ExcelWriterResolver resolver = new ExcelWriterResolver(excelMapping,
        // excelHelperBuilder.build());
        // if (this.multiSheet) {
        // return resolver.generateMultiSheetWorkbook(pageQueryParam, exportFunction);
        // } else {
        // return resolver.generateWorkbook(pageQueryParam, exportFunction);
        // }
        if (ExcelDataType.QUERY.equals(this.getExcelDataType())) {
            for (Map.Entry<Class<?>, ExportFunction<?>> entry : this.excelMapping.entrySet()) {
                this.getWriterContext().addMapper(entry.getKey(), entry.getValue());
            }
        }
        return this.getWriterContext().getExecutor().execute();
    }
}
