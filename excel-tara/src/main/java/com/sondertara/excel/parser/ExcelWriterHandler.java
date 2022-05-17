package com.sondertara.excel.parser;

import com.sondertara.common.exception.TaraException;
import com.sondertara.excel.function.ImportErrorResolver;
import com.sondertara.excel.function.ImportRowParser;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author huangxiaohu
 */
public class ExcelWriterHandler {

    private final ExcelWriter excelWriter;
    public final InputStream inputStream;

    public ExcelWriterHandler(ExcelWriter excelWriter, InputStream inputStream) {
        this.excelWriter = excelWriter;
        this.inputStream = inputStream;
    }

    public void run() {
        if (null != excelWriter) {
            try {
                excelWriter.process(this.inputStream);
            } catch (Exception e) {

                throw new TaraException("Read Excel error", e);
            }
        }
    }

    public ExcelWriterHandler fileName(String fileName) {
        if (null != excelWriter) {
            excelWriter.setImportRowParser(importRowParser);
        }
        return this;
    }

    public ExcelWriterHandler out(HttpServletResponse httpResponse) {

        if (null != excelWriter) {
            excelWriter.setImportErrorResolver(importErrorResolver);
        }
        return this;
    }
}
