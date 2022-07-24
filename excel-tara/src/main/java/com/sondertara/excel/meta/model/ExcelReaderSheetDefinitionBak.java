package com.sondertara.excel.meta.model;

/**
 * @author huangxiaohu
 */
public interface ExcelReaderSheetDefinitionBak extends ExcelSheetDefinition {
    /**
     * sheet index
     *
     * @return index
     */
    int getSheetIndex();

    /**
     * multiple sheet index
     *
     * @return indexes
     */
    int[] getSheetIndexes();

}
