package com.sondertara.excel.support;


import com.sondertara.excel.context.AnnotationExcelReaderContext;
import com.sondertara.excel.support.callback.ExcelCellReadExceptionCallback;
import com.sondertara.excel.support.callback.ExcelRowReadExceptionCallback;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author chenzw
 */
public class ExcelReader {

    private ByteArrayOutputStream baos;

    private ExcelRowReadExceptionCallback rowReadExceptionCallback;

    private ExcelCellReadExceptionCallback cellReadExceptionCallback;


    public ExcelReader(InputStream is) {
        this.baos = new ByteArrayOutputStream();
        try {
            IOUtils.copy(is, baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ExcelReader newInstance(InputStream is) {
        return new ExcelReader(is);
    }

    public ExcelReader configRowReadExceptionCallback(ExcelRowReadExceptionCallback rowReadExceptionCallback) {
        this.rowReadExceptionCallback = rowReadExceptionCallback;
        return this;
    }

    public ExcelReader configCellReadExceptionCallback(ExcelCellReadExceptionCallback cellReadExceptionCallback) {
        this.cellReadExceptionCallback = cellReadExceptionCallback;
        return this;
    }

    public <T> List<T> read(Class<T> clazz) {
        return new AnnotationExcelReaderContext(new ByteArrayInputStream(baos.toByteArray()), clazz, rowReadExceptionCallback, cellReadExceptionCallback).getExecutor()
                .executeRead();
    }

}
