package com.sondertara.domain.export;


import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.annotation.ExcelExportField;

import java.util.Date;

@ExcelExport(sheetName = "用户数据", maxRowsPerSheet = 50)
public class UserPaging {
    @ExcelExportField(colIndex = 1, colName = "姓名")
    private String name;

    @ExcelExportField(colIndex = 2, colName = "年龄")
    private Integer age;

    @ExcelExportField(colIndex = 3, colName = "生日", dataFormat = @ExcelDataFormat("yyyy-MM-dd"))
    private Date birth;

    @ExcelExportField(colIndex = 4, colName = "体重", dataFormat = @ExcelDataFormat("0.00"))
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
        return "UserPaging{" + "name='" + name + '\'' + ", age=" + age + ", birth=" + birth + ", height=" + height + '}';
    }
}
