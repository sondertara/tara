package com.sondertara.excel.boot;


import com.sondertara.common.io.IOUtils;
import com.sondertara.excel.base.TaraExcelBeanReader;
import com.sondertara.excel.context.AnnotationExcelReaderContext;
import com.sondertara.excel.exception.ExcelWriterException;
import com.sondertara.excel.lifecycle.ExcelReadListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangxiaohu
 */
public class ExcelBeanReader implements TaraExcelBeanReader {

    private final ByteArrayOutputStream bao;


    ExcelBeanReader(ByteArrayOutputStream bao) {
        this.bao = bao;
    }

    public static ExcelBeanReader load(File file) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(Files.newInputStream(file.toPath()), stream);
        } catch (IOException e) {
            throw new ExcelWriterException(e);
        }
        return new ExcelBeanReader(stream);
    }

    public static ExcelBeanReader load(InputStream inputStream) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, stream);
        return new ExcelBeanReader(stream);
    }

    @Override
    public   <T> void read(Class<T> clazz, ExcelReadListener<T> readListener) {
        new AnnotationExcelReaderContext<>(new ByteArrayInputStream(bao.toByteArray()), clazz, readListener).getExecutor().execute();

    }


    @Override
    public <T> List<T> read(Class<T> clazz) {
        List<T> dataList = new ArrayList<>();
        new AnnotationExcelReaderContext<>(new ByteArrayInputStream(bao.toByteArray()), clazz, dataList::add).getExecutor().execute();
        return dataList;
    }
}
