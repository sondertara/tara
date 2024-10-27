//package com.sondertara.excel.boot;
//
//import com.sondertara.excel.context.AnnotationFastExcelWriterContext;
//import com.sondertara.excel.resolver.builder.AbstractExcelWriter;
//import com.sondertara.excel.resolver.builder.DateQueryBuilder;
//import lombok.extern.slf4j.Slf4j;
//import org.dhatim.fastexcel.Workbook;
//
///**
// * @author huangxiaohu
// */
//@Slf4j
//public class ExcelBeanFastWriter extends AbstractExcelWriter<Workbook> {
//
//    public ExcelBeanFastWriter() {
//        super(new AnnotationFastExcelWriterContext());
//    }
//
//
//    public static ExcelBeanFastWriter create() {
//        return new ExcelBeanFastWriter();
//    }
//
//    public <R> DateQueryBuilder<ExcelBeanFastWriter, R> mapping(Class<R> rClass) {
//        return new DateQueryBuilder<>(this, rClass);
//    }
//
//    @Override
//    public Workbook generate() {
//        if (log.isDebugEnabled()) {
//            log.debug("Start writing excel.");
//        }
//        final long startTimeMillis = System.currentTimeMillis();
//        try {
//            return this.getWriterContext().getResult();
//        } finally {
//            if (log.isDebugEnabled()) {
//                log.debug("finish write excel,total cost {}ms", (System.currentTimeMillis() - startTimeMillis));
//            }
//        }
//    }
//}
