package com.sondertara.excel.context;

import com.sondertara.excel.executor.RawExcelReaderExecutor;
import com.sondertara.excel.executor.TaraExcelExecutor;
import com.sondertara.excel.meta.model.TaraWorkbook;

import java.io.InputStream;

public class RawExcelReaderContext implements ExcelReaderContext {

    private final InputStream inputStream;
    private final TaraExcelExecutor<TaraWorkbook> excelExecutor;

    public RawExcelReaderContext(InputStream is) {
        this.inputStream = is;
        excelExecutor = new RawExcelReaderExecutor(this);

    }

    @Override
    public TaraExcelExecutor<TaraWorkbook> getExecutor() {
        return this.excelExecutor;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }
}
