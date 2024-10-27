package com.sondertara.excel.lifecycle;

import com.sondertara.excel.meta.model.ExcelCellDef;
import com.sondertara.excel.meta.model.ExcelRowDef;
import com.sondertara.excel.meta.model.ExcelSheetDef;

public interface ExcelReadListener<T> {
    /**
     * 行读取异常
     *
     * @param rowDef 行定义
     * @param ex     异常信息
     * @throws Exception 抛出异常
     */
    default void onRowException( Exception ex,ExcelRowDef rowDef) throws Exception {

        throw ex;

    }

    /**
     * called when cell reading cause exception
     *
     * @param ex      the Exception
     * @param cellDef
     * @param rowDef
     */
    default void onCellException(Exception ex, ExcelCellDef cellDef, ExcelRowDef rowDef) throws Exception {
        throw ex;
    }

    void doAfterRowAnalysed(T data);

    default void doAfterSheetAnalysed(ExcelSheetDef sheetDef){

    }
}
