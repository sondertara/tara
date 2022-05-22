package com.sondertara.domain;


import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import com.sondertara.excel.meta.annotation.ExcelImport;
import com.sondertara.excel.meta.annotation.ExcelImportColumn;

import java.util.Date;

@ExcelImport
@ExcelExport(sheetName = "排班模版")
public class ExcelDutyStaffArrangementTemplate {

    @ExcelImportColumn(colIndex = 1, allowBlank = false)
    @ExcelExportField(colName = "值班线名称", colIndex = 1)
    private String dutyName;

    @ExcelImportColumn(colIndex = 2, allowBlank = false)
    @ExcelExportField(colName = "岗位名称", colIndex = 2)
    private String orderName;

    @ExcelImportColumn(colIndex = 3, allowBlank = false, dateFormat = "yyyy-MM-dd")
    @ExcelExportField(colName = "值班日期", colIndex = 3, dataFormat = @ExcelDataFormat("yyyy-MM-dd"))
    private Date dutyDate;

    @ExcelImportColumn(colIndex = 4, allowBlank = false)
    @ExcelExportField(colName = "值班员工姓名", colIndex = 4)
    private String dutyStaffNames;

    @ExcelImportColumn(colIndex = 5, allowBlank = false)
    @ExcelExportField(colName = "值班员工用户名", colIndex = 5)
    private String dutyUserNames;

    private String dutyStaffIds;

    private Long dutyId;

    private Long orderId;

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public Date getDutyDate() {
        return dutyDate;
    }

    public void setDutyDate(Date dutyDate) {
        this.dutyDate = dutyDate;
    }

    public String getDutyStaffNames() {
        return dutyStaffNames;
    }

    public String getDutyStaffIds() {
        return dutyStaffIds;
    }

    public void setDutyStaffIds(String dutyStaffIds) {
        this.dutyStaffIds = dutyStaffIds;
    }

    public void setDutyStaffNames(String dutyStaffNames) {
        this.dutyStaffNames = dutyStaffNames;
    }

    public String getDutyUserNames() {
        return dutyUserNames;
    }

    public void setDutyUserNames(String dutyUserNames) {
        this.dutyUserNames = dutyUserNames;
    }

    public Long getDutyId() {
        return dutyId;
    }

    public void setDutyId(Long dutyId) {
        this.dutyId = dutyId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "ExcelDutyStaffArrangementTemplate{" + "dutyName='" + dutyName + '\'' + ", orderName='" + orderName
                + '\'' + ", dutyDate=" + dutyDate + ", dutyStaffNames='" + dutyStaffNames + '\'' + ", dutyUserNames='"
                + dutyUserNames + '\'' + ", dutyStaffIds='" + dutyStaffIds + '\'' + ", dutyId=" + dutyId + ", orderId="
                + orderId + '}';
    }
}
