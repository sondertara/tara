package com.sondertara.excel.context;


import com.sondertara.excel.support.callback.ExcelCellReadExceptionCallback;
import com.sondertara.excel.support.callback.ExcelRowReadExceptionCallback;

import java.io.InputStream;

/**
 * @author chenzw
 */
public interface ExcelReaderContext extends ExcelContext {

    InputStream getInputStream();

    /**
     * @return
     * @since 1.0.5
     */
    ExcelRowReadExceptionCallback getExcelRowReadExceptionCallback();

    /**
     * @return
     * @since 1.0.5
     */
    ExcelCellReadExceptionCallback getExcelCellReadExceptionCallback();

}

