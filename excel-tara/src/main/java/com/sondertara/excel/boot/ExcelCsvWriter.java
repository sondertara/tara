package com.sondertara.excel.boot;

import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.context.AnnotationCsvWriterContext;
import com.sondertara.excel.resolver.builder.AbstractExcelWriter;
import com.sondertara.excel.resolver.builder.DateQueryBuilder;
import com.sondertara.excel.utils.ExcelResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;

/**
 * Excel write by csv
 * It write csv file with huge data
 *
 * @author huangxiaohu
 */
public class ExcelCsvWriter extends AbstractExcelWriter<Path> {

    private static final Logger logger = LoggerFactory.getLogger(ExcelCsvWriter.class);


    public ExcelCsvWriter() {
        super(new AnnotationCsvWriterContext());
    }


    public static ExcelCsvWriter create() {
        return new ExcelCsvWriter();
    }

    public <R> DateQueryBuilder<ExcelCsvWriter, R> mapping(Class<R> rClass) {
        return new DateQueryBuilder<>(this, rClass);
    }

    @Override
    public Path generate() {
        logger.info("CSV exporting is starting...");
        return this.getWriterContext().getResult();
    }


    @Override
    public void to(HttpServletResponse httpServletResponse, String fileName) {
        if (!fileName.endsWith(Constants.CSV_SUFFIX)) {
            fileName = fileName + Constants.CSV_SUFFIX;
        }
        ExcelResponseUtils.writeResponse(httpServletResponse, fileName, this::to);
    }

}
