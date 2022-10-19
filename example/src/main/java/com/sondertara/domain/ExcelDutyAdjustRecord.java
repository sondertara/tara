package com.sondertara.domain;


import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import com.sondertara.excel.meta.annotation.ExcelImport;
import com.sondertara.excel.meta.annotation.ExcelImportField;

import java.util.Date;

@ExcelImport(sheetIndex = 1, firstDataRow = 2)
@ExcelExport
public class ExcelDutyAdjustRecord {

    @ExcelExportField(colIndex = 1, colName = "值班组名称")
    @ExcelImportField(colIndex = 1, allowBlank = false)
    private String dutyName;


    @ExcelExportField(colIndex = 2, colName = "调班人员岗位名称")
    @ExcelImportField(colIndex = 2, allowBlank = false)
    private String adjustOrder;

    /**
     * 调班人员
     **/
    @ExcelExportField(colIndex = 3, colName = "调班人员用户名")
    @ExcelImportField(colIndex = 3, allowBlank = false)
    private String adjustStaff;

    /**
     * 调班时间
     **/
    @ExcelExportField(colIndex = 4, colName = "调班时间", dataFormat = @ExcelDataFormat("yyyy/MM/dd"))
    @ExcelImportField(colIndex = 4, allowBlank = false, dateFormat = "yyyy/MM/dd")
    private Date adjustDate;


    @ExcelExportField(colIndex = 5, colName = "被调班人员岗位名称")
    @ExcelImportField(colIndex = 5, allowBlank = false)
    private String beAdjustOrder;

    /**
     * 被调班人员
     **/
    @ExcelExportField(colIndex = 6, colName = "被调班人员用户名")
    @ExcelImportField(colIndex = 6, allowBlank = false)
    private String beAdjustStaff;

    /**
     * 被调班时间
     **/
    @ExcelExportField(colIndex = 7, colName = "被调班日期", dataFormat = @ExcelDataFormat("yyyy/MM/dd"))
    @ExcelImportField(colIndex = 7, allowBlank = false, dateFormat = "yyyy/MM/dd")
    private Date beAdjustDate;

    /**
     * 备注
     **/
    @ExcelExportField(colIndex = 8, colName = "备注")
    @ExcelImportField(colIndex = 8)
    private String remark;


    /**
     * 状态：0-审核中，1-有效，2-无效
     **/
    private Integer state;


    /**
     * 登记人
     **/
    private Long creator;


    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public String getAdjustOrder() {
        return adjustOrder;
    }

    public void setAdjustOrder(String adjustOrder) {
        this.adjustOrder = adjustOrder;
    }

    public String getAdjustStaff() {
        return adjustStaff;
    }

    public void setAdjustStaff(String adjustStaff) {
        this.adjustStaff = adjustStaff;
    }

    public Date getAdjustDate() {
        return adjustDate;
    }

    public void setAdjustDate(Date adjustDate) {
        this.adjustDate = adjustDate;
    }

    public String getBeAdjustOrder() {
        return beAdjustOrder;
    }

    public void setBeAdjustOrder(String beAdjustOrder) {
        this.beAdjustOrder = beAdjustOrder;
    }

    public String getBeAdjustStaff() {
        return beAdjustStaff;
    }

    public void setBeAdjustStaff(String beAdjustStaff) {
        this.beAdjustStaff = beAdjustStaff;
    }

    public Date getBeAdjustDate() {
        return beAdjustDate;
    }

    public void setBeAdjustDate(Date beAdjustDate) {
        this.beAdjustDate = beAdjustDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "ExcelDutyAdjustRecord{" + "dutyName='" + dutyName + '\'' + ", adjustOrder='" + adjustOrder + '\'' + ", adjustStaff='" + adjustStaff + '\'' + ", adjustDate=" + adjustDate + ", beAdjustOrder='" + beAdjustOrder + '\'' + ", beAdjustStaff=" + beAdjustStaff + ", beAdjustDate=" + beAdjustDate + ", remark='" + remark + '\'' + ", state=" + state + ", creator=" + creator + '}';
    }
}
