package com.sondertara.excel.context;

import java.io.InputStream;

/**
 * Excel read context
 * @author huangxiaohu
 */
public interface ExcelReaderContext<T> extends ExcelContext<T> {
    /**
     * get the InputStream
     *
     * @return InputStream
     */
    InputStream getInputStream();
}
