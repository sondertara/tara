package com.sondertara.excel.context;


import com.sondertara.excel.support.callback.ExcelCellReadExceptionCallback;
import com.sondertara.excel.support.callback.ExcelRowReadExceptionCallback;

import java.io.InputStream;

/**
 * @author chenzw
 */
public interface ExcelReaderContext extends ExcelContext {
    /**
     * get the InputStream
     *
     * @return InputStream
     */
    InputStream getInputStream();

    /**
     * read row error callback
     *
     * @return callback
     */
    ExcelRowReadExceptionCallback getExcelRowReadExceptionCallback();

    /**
     * read cell error callback
     *
     * @return callback
     */
    ExcelCellReadExceptionCallback getExcelCellReadExceptionCallback();

}

