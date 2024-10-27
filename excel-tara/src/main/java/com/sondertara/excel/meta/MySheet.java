package com.sondertara.excel.meta;

import com.sondertara.common.struct.weapper.Wrapper;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/20 16:39
 */
public interface MySheet extends  Wrapper<SXSSFSheet> {

    MyRow getRow(int rowNum);

    MyRow  createRow(int rowNum);


    void addMergedRegion(CellRangeAddress cellRangeAddress);

    void addValidationData(DataValidation dataValidation);


    void setColumnWidth(int colIndex, int min);

    int getLastRowNum();
}
