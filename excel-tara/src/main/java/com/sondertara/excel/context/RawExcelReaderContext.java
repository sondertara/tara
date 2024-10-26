package com.sondertara.excel.context;

import com.sondertara.excel.executor.RawExcelReaderExecutor;
import com.sondertara.excel.executor.TaraExcelExecutor;

import java.io.InputStream;

public class RawExcelReaderContext implements ExcelReaderContext {

    private final InputStream inputStream;
    private final TaraExcelExecutor excelExecutor;

    public RawExcelReaderContext(InputStream is) {
        this.inputStream = is;
        excelExecutor = new RawExcelReaderExecutor(this);

    }

    @Override
    public TaraExcelExecutor getExecutor() {
        return this.excelExecutor;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }
}
