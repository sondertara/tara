package com.sondertara.excel.resolver.builder;

import com.sondertara.common.exception.TaraException;
import com.sondertara.excel.base.TaraExcelWriter;
import com.sondertara.excel.context.ExcelRawWriterContext;
import com.sondertara.excel.utils.ExcelResponseUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;

/**
 * @author huangxiaohu
 */
public abstract class AbstractExcelWriter<T> implements TaraExcelWriter {

    /**
     * the class with {@link com.sondertara.excel.meta.annotation.ExcelExport } annotation and pagination query function
     */
    /**
     * Excel Write context
     */
    private final ExcelRawWriterContext<T> writerContext;

    public AbstractExcelWriter(ExcelRawWriterContext<T> writerContext) {
        this.writerContext = writerContext;
    }


    /**
     * the file
     *
     * @return the target file
     */
    public abstract T generate();

    @Override
    public void to(OutputStream out) {
        T t = generate();
        try {
            if (String.class.equals(t.getClass())) {
                out.write(FileUtils.readFileToByteArray(new File((String) t)));
            } else if (Workbook.class.isAssignableFrom(t.getClass())) {
                if (t instanceof SXSSFWorkbook) {
                    try (SXSSFWorkbook wb = (SXSSFWorkbook) t) {
                        wb.write(out);
                        wb.dispose();
                    }
                }else {
                    throw new IllegalStateException("Workbook only support SXSSFWorkbook");
                }
            }
        } catch (Exception e) {
            throw new TaraException("Write workbook to stream error", e);
        }

    }

    @Override
    public void to(HttpServletResponse httpServletResponse, String fileName) {
        ExcelResponseUtils.writeResponse(httpServletResponse, fileName, this::to);
    }

    protected ExcelRawWriterContext<T> getWriterContext() {
        return writerContext;
    }

}
