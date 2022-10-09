package com.sondertara.excel.context;

import com.sondertara.excel.meta.model.TaraSheet;
import com.sondertara.excel.support.callback.CellReadExCallback;
import com.sondertara.excel.support.callback.RowReadExCallback;

import java.util.Map;

/**
 * @author huangxiaohu
 */
public interface ExcelReaderContext extends ExcelRawReaderContext {

    /**
     * 获取sheet 定义
     *
     * @return the map
     */
    Map<Integer, ? extends TaraSheet> getSheetDefinitions();

    /**
     * read row error callback
     *
     * @return callback
     */
    RowReadExCallback getExcelRowReadExceptionCallback();

    /**
     * read cell error callback
     *
     * @return callback
     */
    CellReadExCallback getExcelCellReadExceptionCallback();

}
