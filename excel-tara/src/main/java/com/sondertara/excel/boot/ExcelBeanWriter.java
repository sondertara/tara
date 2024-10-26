package com.sondertara.excel.boot;

import com.sondertara.excel.context.AnnotationExcelWriterContext;
import com.sondertara.excel.resolver.builder.AbstractExcelWriter;
import com.sondertara.excel.resolver.builder.DateQueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * @author huangxiaohu
 */
@Slf4j
public class ExcelBeanWriter extends AbstractExcelWriter<SXSSFWorkbook> {

    public ExcelBeanWriter() {
        super(new AnnotationExcelWriterContext());
    }


    public static ExcelBeanWriter create() {
        return new ExcelBeanWriter();
    }

    public <R> DateQueryBuilder<ExcelBeanWriter, R> mapping(Class<R> rClass) {
        return new DateQueryBuilder<>(this, rClass);
    }

    @Override
    public SXSSFWorkbook generate() {
        if (log.isDebugEnabled()) {
            log.debug("Start writing excel.");
        }
        final long startTimeMillis = System.currentTimeMillis();
        try {
            return this.getWriterContext().getResult();
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("finish write excel,total cost {}ms", (System.currentTimeMillis() - startTimeMillis));
            }
        }
    }
}
