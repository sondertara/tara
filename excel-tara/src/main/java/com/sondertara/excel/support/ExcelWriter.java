package com.sondertara.excel.support;

import com.sondertara.excel.context.AnnotationExcelWriterContext;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author chenzw
 */
public class ExcelWriter {

    private AnnotationExcelWriterContext writerContext;

    public ExcelWriter() {
        this.writerContext = new AnnotationExcelWriterContext();
    }

    public static ExcelWriter newInstance() {
        return new ExcelWriter();
    }

    /**
     * 实例化模版类
     *
     * @param clazzs
     * @return
     */
    public static ExcelWriter newTemplateInstance(Class<?>... clazzs) {
        ExcelWriter excelWriter = new ExcelWriter();
        excelWriter.addModel(clazzs);
        return excelWriter;
    }

    public ExcelWriter addModel(Class<?>... clazzs) {
        this.writerContext.addModel(clazzs);
        return this;
    }

    /**
     * 添加数据
     *
     * @param data
     * @return
     */
    public ExcelWriter addData(List<?>... data) {
        this.writerContext.addData(data);
        return this;
    }


    public void write(OutputStream os) throws IOException {
        Workbook workbook = execute();
        workbook.write(os);
    }

    public void write(HttpServletResponse response) throws IOException {
        OutputStream os = response.getOutputStream();
        Workbook workbook = execute();
        workbook.write(os);
        os.flush();
    }


    private Workbook execute() {
        return this.writerContext.getExecutor().execute();
    }

}
