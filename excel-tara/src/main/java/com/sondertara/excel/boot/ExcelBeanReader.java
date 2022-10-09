package com.sondertara.excel.boot;// package com.sondertara.excel.boot;


import com.sondertara.excel.base.TaraExcelBeanReader;
import com.sondertara.excel.context.AnnotationExcelReaderContext;
import com.sondertara.excel.exception.ExcelException;
import com.sondertara.excel.support.callback.CellReadExCallback;
import com.sondertara.excel.support.callback.RowReadExCallback;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

/**
 * @author huangxiaohu
 */
public class ExcelBeanReader implements TaraExcelBeanReader {

    private final ByteArrayOutputStream bao;

    private RowReadExCallback rowReadExCallback;
    private CellReadExCallback cellReadExCallback;


    ExcelBeanReader(ByteArrayOutputStream bao) {
        this.bao = bao;
    }

    public static ExcelBeanReader load(File file) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(Files.newInputStream(file.toPath()), stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ExcelBeanReader(stream);
    }

    public static ExcelBeanReader load(InputStream inputStream) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(inputStream, stream);
        } catch (IOException e) {
            throw new ExcelException(e);
        }
        return new ExcelBeanReader(stream);
    }


    public ExcelBeanReader rowError(RowReadExCallback callback) {
        this.rowReadExCallback = callback;
        return this;
    }


    public ExcelBeanReader cellError(CellReadExCallback callback) {
        this.cellReadExCallback = callback;
        return this;
    }

    @Override
    public <T> List<T> read(Class<T> clazz) {
        return new AnnotationExcelReaderContext<>(new ByteArrayInputStream(bao.toByteArray()), clazz, rowReadExCallback, cellReadExCallback).getExecutor().execute();

    }
}
