package com.sondertara.excel.boot;

import com.sondertara.excel.constants.Constants;
import com.sondertara.excel.context.AnnotationCsvWriterContext;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.parser.ExcelReader;
import com.sondertara.excel.parser.builder.AbstractExcelWriter;
import com.sondertara.excel.utils.ExcelResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * excel write
 *
 * @author huangxiaohu
 */
public class ExcelCsvWriter extends AbstractExcelWriter<String> {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);


    public ExcelCsvWriter() {
        super(new AnnotationCsvWriterContext());
    }

    public static <R> ExcelCsvWriter mapper(Class<?> excelClass, ExportFunction<R> query) {
        ExcelCsvWriter csvWriter = new ExcelCsvWriter();
        csvWriter.getWriterContext().addMapper(excelClass, query);
        return csvWriter;
    }


    @Override
    public String generate() {
        logger.info("CSV exporting is starting...");
        return this.getWriterContext().getExecutor().execute();
    }


    @Override
    public void to(HttpServletResponse httpServletResponse, String fileName) {
        if (!fileName.endsWith(Constants.CSV_SUFFIX)) {
            fileName = fileName + Constants.CSV_SUFFIX;
        }
        ExcelResponseUtils.writeResponse(httpServletResponse, fileName, this::to);
    }

}
