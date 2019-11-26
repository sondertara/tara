package com.sondertara.model;

import com.sondertara.excel.annotation.ExportField;
import lombok.Data;

@Data
public class UserInfoVo {

    @ExportField(columnName = "姓名")
    private String name;
    @ExportField(columnName = "年龄")
    private Integer age;
    @ExportField(columnName = "住址")
    private String address;
}
