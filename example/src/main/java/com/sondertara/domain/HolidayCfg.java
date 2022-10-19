package com.sondertara.domain;

import com.sondertara.excel.enums.ExcelColBindType;
import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import com.sondertara.excel.meta.annotation.ExcelImport;
import com.sondertara.excel.meta.annotation.ExcelImportField;
import com.sondertara.excel.meta.annotation.converter.ExcelKVConvert;
import com.sondertara.excel.meta.annotation.datavalidation.ExcelStringList;

import java.util.Date;

@ExcelImport(sheetIndex = 1, firstDataRow = 2,bindType = ExcelColBindType.TITLE)
@ExcelExport(sheetName = "节假日")
public class HolidayCfg {

    @ExcelImportField(colIndex = 1, dateFormat = "yyyy-MM-dd", allowBlank = false,title = "节假日日期")
    @ExcelExportField(colName = "节假日日期", colIndex = 1, dataFormat = @ExcelDataFormat("yyyy-MM-dd HH:mm:ss"))
    private Date holidayDate;

    @ExcelImportField(colIndex = 2, allowBlank = false,title = "节假日名称")
    @ExcelExportField(colName = "节假日名称", colIndex = 2)
    private String holidayName;

    @ExcelKVConvert(kvMap = {"是=0", "否=1"})
    @ExcelImportField(colIndex = 3, allowBlank = false,title = "是否上班")
    @ExcelStringList({"是", "否"})
    @ExcelExportField(colName = "是否上班", colIndex = 3)
    private String isWork;

    @ExcelImportField(colIndex = 4,title = "备注")
    @ExcelExportField(colName = "备注", colIndex = 4)
    private String remark;


    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(final String holidayName) {
        this.holidayName = holidayName;
    }

    public String getIsWork() {
        return isWork;
    }

    public void setIsWork(final String isWork) {
        this.isWork = isWork;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public Date getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(final Date holidayDate) {
        this.holidayDate = holidayDate;
    }

    @Override
    public String toString() {
        return "HolidayCfg{" + "holidayDate='" + holidayDate + '\'' + ", holidayName='" + holidayName + '\'' + ", isWork='" + isWork + '\'' + ", remark='" + remark + '\'' + '}';
    }
}
