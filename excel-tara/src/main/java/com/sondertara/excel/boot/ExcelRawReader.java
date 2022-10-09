package com.sondertara.excel.boot;

import com.sondertara.excel.base.TaraExcelReader;
import com.sondertara.excel.exception.ExcelReaderException;
import com.sondertara.excel.fast.ReadableWorkbook;

import java.io.File;
import java.io.IOException;

/**
 * @author huangxiaohu
 */
public class ExcelRawReader implements TaraExcelReader<ReadableWorkbook> {

    private final ReadableWorkbook readableWorkbook;

    ExcelRawReader(ReadableWorkbook readableWorkbook) {
        this.readableWorkbook = readableWorkbook;
    }

    public static ExcelRawReader load(File file) {
        try {
            return new ExcelRawReader(new ReadableWorkbook(file));
        } catch (IOException e) {
            throw new ExcelReaderException(e);
        }
    }

    @Override
    public ReadableWorkbook read() {
        return this.readableWorkbook;
    }
}
