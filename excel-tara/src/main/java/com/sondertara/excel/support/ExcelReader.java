package com.sondertara.excel.support;

import com.sondertara.excel.context.AnnotationExcelReaderContext;
import com.sondertara.excel.context.RawExcelReaderContext;
import com.sondertara.excel.meta.model.TaraWorkbook;
import com.sondertara.excel.support.callback.CellReadExCallback;
import com.sondertara.excel.support.callback.RowReadExCallback;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author huangxiaohu
 */
public class ExcelReader {

    private final ByteArrayOutputStream baos;

    private RowReadExCallback rowReadExceptionCallback;

    private CellReadExCallback cellReadExceptionCallback;

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

    public ExcelReader configRowReadExceptionCallback(RowReadExCallback rowReadExceptionCallback) {
        this.rowReadExceptionCallback = rowReadExceptionCallback;
        return this;
    }

    public ExcelReader configCellReadExceptionCallback(CellReadExCallback cellReadExceptionCallback) {
        this.cellReadExceptionCallback = cellReadExceptionCallback;
        return this;
    }

    public <T> List<T> read(Class<T> clazz) {
        return new AnnotationExcelReaderContext<>(new ByteArrayInputStream(baos.toByteArray()), clazz,
                rowReadExceptionCallback, cellReadExceptionCallback).getExecutor().execute();
    }

    public TaraWorkbook rawRead() {
        return new RawExcelReaderContext(new ByteArrayInputStream(baos.toByteArray())).getExecutor().execute();
    }

}
