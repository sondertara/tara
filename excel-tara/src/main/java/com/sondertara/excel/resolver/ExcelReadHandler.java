package com.sondertara.excel.resolver;

import com.sondertara.common.exception.TaraException;
import com.sondertara.excel.function.ImportErrorResolver;
import com.sondertara.excel.function.ImportRowParser;

import java.io.InputStream;

/**
 * @author huangxiaohu
 */
public class ExcelReadHandler {

    private final ExcelReader excelReader;
    public final InputStream inputStream;

    public ExcelReadHandler(ExcelReader excelReader, InputStream inputStream) {
        this.excelReader = excelReader;
        this.inputStream = inputStream;
    }

    public void read() {
        if (null != excelReader) {
            try {
                excelReader.process(this.inputStream);
            } catch (Exception e) {

                throw new TaraException("Read Excel error", e);
            }
        }
    }

    public ExcelReadHandler onRow(ImportRowParser importRowParser) {
        if (null != excelReader) {
            excelReader.setImportRowParser(importRowParser);
        }
        return this;
    }

    public ExcelReadHandler onError(ImportErrorResolver importErrorResolver) {

        if (null != excelReader) {
            excelReader.setImportErrorResolver(importErrorResolver);
        }
        return this;
    }
}
