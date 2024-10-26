package com.sondertara.excel.context;

import com.sondertara.excel.lifecycle.ExcelReadListener;
import com.sondertara.excel.meta.model.TaraSheet;

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
     * read listener
     *
     * @return callback
     */
    ExcelReadListener<T> getReadListener();


}
