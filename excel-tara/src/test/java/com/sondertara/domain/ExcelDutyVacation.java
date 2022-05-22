package com.sondertara.domain;

import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import com.sondertara.excel.meta.annotation.ExcelImport;
import com.sondertara.excel.meta.annotation.ExcelImportColumn;
import com.sondertara.excel.meta.annotation.converter.ExcelKVConvert;
import com.sondertara.excel.meta.annotation.datavalidation.ExcelStringList;

import java.util.Date;

@ExcelImport(sheetIndex = 1, firstDataRow = 2)
@ExcelExport
public class ExcelDutyVacation {

    @ExcelExportField(colIndex = 1, colName = "人员用户名")
    @ExcelImportColumn(colIndex = 1, allowBlank = false)
    private String userName;

    /**
     * 休假类型
     **/
    @ExcelExportField(colIndex = 2, colName = "休假类型")
    @ExcelStringList({"请假", "年休", "出差", "不值班"})
    @ExcelKVConvert(kvmap = {"请假=0", "年休=1", "出差=2", "不值班=3"})
    @ExcelImportColumn(colIndex = 2, allowBlank = false)
    private Integer vacationType;


    /**
     *
     * 休假开始时间
     **/
    @ExcelExportField(colIndex = 3, colName = "休假起始时间", dataFormat = @ExcelDataFormat("yyyy/MM/dd"))
    @ExcelImportColumn(colIndex = 3, allowBlank = false, dateFormat = "yyyy/MM/dd")
    private Date startDate;


    /**
     * 休假结束时间
     **/
    @ExcelExportField(colIndex = 4, colName = "休假结束时间", dataFormat = @ExcelDataFormat("yyyy/MM/dd"))
    @ExcelImportColumn(colIndex = 4, allowBlank = false, dateFormat = "yyyy/MM/dd")
    private Date endDate;

    @ExcelExportField(colIndex = 5, colName = "备注")
    @ExcelImportColumn(colIndex = 5)
    private String remark;

    /**
     * 登记方式：0-普通员工自行录入、1-普通员工自行xls导入、2-管理员录入、3-管理员xls导入
     **/
    private Integer recordType;

    /**
     * 登记人
     **/
    private Long creator;

    /**
     * 休假天数
     **/
    private Integer vacationDays;

    /**
     * 状态：0-审核中、1-有效、2-无效
     **/
    private Integer state;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getVacationType() {
        return vacationType;
    }

    public void setVacationType(Integer vacationType) {
        this.vacationType = vacationType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getRecordType() {
        return recordType;
    }

    public void setRecordType(Integer recordType) {
        this.recordType = recordType;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Integer getVacationDays() {
        return vacationDays;
    }

    public void setVacationDays(Integer vacationDays) {
        this.vacationDays = vacationDays;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "ExcelDutyVacation{" + "userName='" + userName + '\'' + ", vacationType=" + vacationType + ", startDate="
                + startDate + ", endDate=" + endDate + ", remark='" + remark + '\'' + ", recordType=" + recordType
                + ", creator=" + creator + ", vacationDays=" + vacationDays + ", state=" + state + '}';
    }
}
