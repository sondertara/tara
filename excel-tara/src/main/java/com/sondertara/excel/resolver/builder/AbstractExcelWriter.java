package com.sondertara.excel.resolver.builder;

import com.sondertara.common.io.FileUtils;
import com.sondertara.common.io.stream.ByteArrayOutputStream;
import com.sondertara.excel.base.TaraExcelWriter;
import com.sondertara.excel.context.ExcelRawWriterContext;
import com.sondertara.excel.exception.ExcelWriterException;
import com.sondertara.excel.utils.ExcelResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author huangxiaohu
 */
@Slf4j
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
        try {
            T t = generate();
            if (String.class.equals(t.getClass())) {
                out.write(FileUtils.readBytes(new File((String) t)));
            } else if (Workbook.class.isAssignableFrom(t.getClass())) {
                if (t instanceof SXSSFWorkbook) {
                    if (log.isDebugEnabled()) {
                        log.debug("Write SXSSFWorkbook to out stream start...");
                    }
                    byte[] bytes = null;
                    try {
                        try (SXSSFWorkbook wb = (SXSSFWorkbook) t; ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2048)) {
                            wb.write(outputStream);
                            bytes = outputStream.toByteArray();
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Write SXSSFWorkbook to bytes  finish...");
                        }
                        IOUtils.write(bytes, out);
                        out.flush();
                    } finally {
                        if (log.isDebugEnabled()) {
                            log.debug("Write SXSSFWorkbook to out stream finish...");
                        }
                    }
                } else {
                    throw new IllegalStateException("Workbook only support SXSSFWorkbook");
                }
            }
        } catch (IOException e) {
            throw new ExcelWriterException("Write excel cause IO error,{}", e.getMessage(), e);
        }

    }

    @Override
    public void to(HttpServletResponse httpServletResponse, String fileName) {
        try {
            T t = generate();
            ExcelResponseUtils.wrapBuiltinResponse(httpServletResponse, fileName);
            if (String.class.equals(t.getClass())) {

                ServletOutputStream outputStream = httpServletResponse.getOutputStream();
                outputStream.write(FileUtils.readBytes(new File((String) t)));
                outputStream.flush();
            } else if (Workbook.class.isAssignableFrom(t.getClass())) {
                if (t instanceof SXSSFWorkbook) {
                    if (log.isDebugEnabled()) {
                        log.debug("Write SXSSFWorkbook to out stream start...");
                    }
                    byte[] bytes = null;
                    try {
                        try (SXSSFWorkbook wb = (SXSSFWorkbook) t; ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2048)) {
                            wb.write(outputStream);
                            bytes = outputStream.toByteArray();
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Write SXSSFWorkbook to bytes  finish...");
                        }
                    } finally {
                        if (log.isDebugEnabled()) {
                            log.debug("Write SXSSFWorkbook to out stream finish...");
                        }
                    }
                    ServletOutputStream outputStream = httpServletResponse.getOutputStream();
                    IOUtils.write(bytes, outputStream);
                    outputStream.flush();
                } else {
                    throw new IllegalStateException("Workbook only support SXSSFWorkbook");
                }
            }
        } catch (IOException e) {
            throw new ExcelWriterException("Write excel cause IO error,{}", e.getMessage(), e);
        }
    }

    protected ExcelRawWriterContext<T> getWriterContext() {
        return writerContext;
    }

}
