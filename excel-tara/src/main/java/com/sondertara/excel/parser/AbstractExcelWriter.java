package com.sondertara.excel.parser;

import com.sondertara.common.exception.TaraException;
import com.sondertara.excel.context.AnnotationExcelWriterContext;
import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.function.ExportFunction;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public abstract class AbstractExcelWriter<T> implements TaraExcelWriter {

    protected PageQueryParam pageQueryParam;

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

    public void pagination(Integer start, Integer end, Integer pageSize) {
        this.pageQueryParam = PageQueryParam.builder().pageEnd(end).pageStart(start).pageSize(pageSize).build();

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
        try (OutputStream out = httpServletResponse.getOutputStream()) {
            to(out);
            httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String s = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            httpServletResponse.setHeader("Content-disposition", "attachment; filename=\"" + s + "\"");
            out.flush();
        } catch (Exception e) {
            throw new TaraException("Download Excel error", e);

        }
    }

    protected <R> void exportFunction(ExportFunction<R> exportFunction) {
        this.exportFunction = exportFunction;
    }

    public <R> void excelMapping(Class<?> excelClass, ExportFunction<R> exportFunction) {
        this.excelMapping.put(excelClass, exportFunction);
    }

    public AnnotationExcelWriterContext getWriterContext() {
        return writerContext;
    }

    public ExcelDataType getExcelDataType() {
        return excelDataType;
    }

    public void setExcelDataType(ExcelDataType excelDataType) {
        this.excelDataType = excelDataType;
    }
}
