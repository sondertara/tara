//package com.sondertara.excel.context;
//
//import com.sondertara.common.io.stream.ByteArrayOutputStream;
//import com.sondertara.excel.common.constants.Constants;
//import com.sondertara.excel.executor.ExcelWriterExecutor;
//import com.sondertara.excel.executor.TaraExcelExecutor;
//import com.sondertara.excel.meta.FastWorkbook;
//import com.sondertara.excel.meta.MyWorkBook;
//import org.apache.poi.xssf.streaming.SXSSFWorkbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.dhatim.fastexcel.Workbook;
//
///**
// * @author huangxiaohu
// */
//public class AnnotationFastExcelWriterContext extends BaseAnnotationExcelWriterContext<MyWorkBook> {
//
//    private final MyWorkBook workbook;
//
//    private volatile boolean executed = false;
//
//    public AnnotationFastExcelWriterContext() {
//        this.workbook =new FastWorkbook( new Workbook(new ByteArrayOutputStream()));
//    }
//
//    @Override
//    public MyWorkBook getResult() {
//        if (!executed) {
//            getExecutor().execute();
//            executed = true;
//        }
//        return this.workbook;
//    }
//
//    @Override
//    public TaraExcelExecutor getExecutor() {
//        return new ExcelWriterExecutor(sxssfWorkbook, this);
//    }
//}
