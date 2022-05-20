package com.sondertara.excel.parser;

import com.sondertara.common.exception.TaraException;
import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.function.ExportFunction;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class AbstractExcelWriter<T> implements TaraExcelWriter {

    protected PageQueryParam pageQueryParam;
    protected ExportFunction<?> exportFunction;

    protected Class<?> excelClass;

    public AbstractExcelWriter(Class<?> excelClass) {
        this.excelClass = excelClass;
    }
    public AbstractExcelWriter(){

    };

    public void pagination(Integer start, Integer end, Integer pageSize) {
        this.pageQueryParam = PageQueryParam.builder().pageEnd(end).pageStart(start).pageSize(pageSize).build();

    }


    /**
     * the file
     *
     * @return the target file
     */
    public abstract T generate();

    public void exportFunction() {

    }


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

    public <R> void exportFunction(ExportFunction<R> exportFunction) {
        this.exportFunction = exportFunction;
    }
}
