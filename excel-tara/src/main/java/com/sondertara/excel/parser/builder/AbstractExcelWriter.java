package com.sondertara.excel.parser.builder;

import com.sondertara.common.exception.TaraException;
import com.sondertara.excel.base.TaraExcelWriter;
import com.sondertara.excel.context.AnnotationExcelWriterContext;
import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.utils.ExcelResponseUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public abstract class AbstractExcelWriter<T> implements TaraExcelWriter {

    protected ExcelDataType excelDataType;
    protected ExportFunction<?> exportFunction;
    protected Map<Class<?>, ExportFunction<?>> excelMapping = new HashMap<>();

    protected Class<?> excelClass;
    private AnnotationExcelWriterContext writerContext;

    public AbstractExcelWriter() {
        writerContext = new AnnotationExcelWriterContext();
    }

    protected AbstractExcelWriter(Class<?> clazz) {
        this.excelClass = clazz;

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
                try (Workbook wb = (Workbook) t) {
                    wb.write(out);
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


    protected <R> void excelMapping(Class<?> excelClass, ExportFunction<R> exportFunction) {
        this.excelMapping.put(excelClass, exportFunction);
    }

    protected AnnotationExcelWriterContext getWriterContext() {
        return writerContext;
    }

    public void addData(List<?>... dataList) {
        this.writerContext.addData(Arrays.asList(dataList));
    }

    protected ExcelDataType getExcelDataType() {
        return excelDataType;
    }

    protected void setExcelDataType(ExcelDataType excelDataType) {
        this.excelDataType = excelDataType;
    }
}
