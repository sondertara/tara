package com.sondertara.excel.context;

import java.io.InputStream;

public interface ExcelRawReaderContext extends ExcelContext {
    /**
     * get the InputStream
     *
     * @return InputStream
     */
    InputStream getInputStream();
}
