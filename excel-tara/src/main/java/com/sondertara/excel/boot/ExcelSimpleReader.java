package com.sondertara.excel.boot;

import com.sondertara.excel.base.TaraExcelReader;
import com.sondertara.excel.exception.ExcelReaderException;
import com.sondertara.excel.fast.reader.ReadableWorkbook;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author huangxiaohu
 */
public class ExcelSimpleReader implements TaraExcelReader<ReadableWorkbook> {

    private final ReadableWorkbook readableWorkbook;

    ExcelSimpleReader(ReadableWorkbook readableWorkbook) {
        this.readableWorkbook = readableWorkbook;
    }

    public static ExcelSimpleReader load(File file) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(Files.newInputStream(file.toPath()), stream);
            return new ExcelSimpleReader(new ReadableWorkbook(new ByteArrayInputStream(stream.toByteArray())));
        } catch (IOException e) {
            throw new ExcelReaderException(e);
        }
    }

    public static ExcelSimpleReader load(InputStream inputStream) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(inputStream, stream);
            return new ExcelSimpleReader(new ReadableWorkbook(new ByteArrayInputStream(stream.toByteArray())));
        } catch (IOException e) {
            throw new ExcelReaderException(e);
        }
    }

    @Override
    public ReadableWorkbook read() {
        return this.readableWorkbook;
    }
}
