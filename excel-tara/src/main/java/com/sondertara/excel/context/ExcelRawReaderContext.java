package com.sondertara.excel.context;

import com.sondertara.excel.meta.model.TaraSheet;
import com.sondertara.excel.support.callback.CellReadExCallback;
import com.sondertara.excel.support.callback.RowReadExCallback;

import java.util.Map;

/**
 * @author huangxiaohu
 */
public interface ExcelRawReaderContext<T> extends ExcelReaderContext<T> {

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
    RowReadExCallback getExcelRowReadExCallback();

    /**
     * read cell error callback
     *
     * @return callback
     */
    CellReadExCallback getExcelCellReadExCallback();

}
