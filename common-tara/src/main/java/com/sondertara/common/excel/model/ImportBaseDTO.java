package com.sondertara.common.excel.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chenxinshi
 * @date: 2018/12/21
 * @desc: excel解析后存放的dto请继承这个类，调用ExcelUtil.parse()
 */
@Data
public class ImportBaseDTO implements Serializable {

    /**
     * excel中的行号
     */
    private Integer rowNum;

    /**
     * 解析是否成功
     */
    private Boolean success;

    /**
     * 失败编码
     */
    private String code;

    /**
     * 失败原因
     */
    private String message;

    public void fail(String code, String message) {
        this.success = false;
        this.code = code;
        this.message = message;
    }
}
