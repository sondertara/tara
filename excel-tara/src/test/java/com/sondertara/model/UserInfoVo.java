package com.sondertara.model;

import com.sondertara.excel.annotation.ExcelImportFiled;
import lombok.Data;

@Data
public class UserInfoVo {

    @ExcelImportFiled(columnName = "姓名")
    private String name;
    @ExcelImportFiled(columnName = "年龄")
    private Integer age;
    @ExcelImportFiled(columnName = "住址")
    private String address;
}
