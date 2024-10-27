package com.sondertara.excel.domain.export;


import com.sondertara.excel.meta.annotation.CellRange;
import com.sondertara.excel.meta.annotation.ExcelComplexHeader;
import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import com.sondertara.excel.meta.style.DefaultTitleCellStyleBuilder;

import java.util.Date;

/**
 * @author huangxiaohu
 */
@ExcelComplexHeader({
        @CellRange(firstCol = 1, lastCol = 4, firstRow = 1, lastRow = 2, title = "用户数据", cellStyleBuilder = DefaultTitleCellStyleBuilder.class),
        @CellRange(firstRow = 3, lastRow = 3, firstCol = 1, lastCol = 2, title = "基本信息", cellStyleBuilder = DefaultTitleCellStyleBuilder.class),
        @CellRange(firstRow = 3, lastRow = 3, firstCol = 3, lastCol = 4, title = "扩展信息", cellStyleBuilder = DefaultTitleCellStyleBuilder.class)})
@ExcelExport(sheetName = "用户数据",rowStriped = true)
public class UserComplexHeader {
    @ExcelExportField(colIndex = 1, colName = {"用户数据","基本信息","姓名"})
    private String name;

    @ExcelExportField(colIndex = 2, colName = {"用户数据","基本信息","年龄"})
    private Integer age;

    @ExcelExportField(colIndex = 3, colName = {"用户数据","扩展信息","生日"}, dataFormat = @ExcelDataFormat("yyyy-MM-dd"))
    private Date birth;

    @ExcelExportField(colIndex = 4, colName = {"用户数据","扩展信息","体重"}, dataFormat = @ExcelDataFormat("0.00"))
    private Double height;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "UserComplexHeader{" + "name='" + name + '\'' + ", age=" + age + ", birth=" + birth + ", height="
                + height + '}';
    }
}
