package com.sondertara.domain;


import com.sondertara.excel.annotation.ExcelImportField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author huangxiaohu
 * Date: 2019-08-13
 * Time: 下午03:51
 */
@Data
public class ImportParam implements Serializable {
    @ExcelImportField(index = 1)
    private String recordNo;

    @ExcelImportField(index = 3)
    private Date orderTime;

    @ExcelImportField(index = 6, required = true)
    private String vin;

    @ExcelImportField(index = 7, required = true)
    private String policyNo;

    @ExcelImportField(index = 8)
    private String correctNo;
    @ExcelImportField(index = 11)
    private String bizType;

    @ExcelImportField(index = 13)
    private String insuredName;

    @ExcelImportField(index = 14)
    private String insurerName;

    @ExcelImportField(index = 15)
    private String cityName;
    @ExcelImportField(index = 16)
    private String categoryName;

    @ExcelImportField(index = 17, scale = 4)
    private BigDecimal discount;
    @ExcelImportField(index = 18, required = true)
    private BigDecimal premium;
    @ExcelImportField(index = 21)
    private String specialInvoiceNo;
    @ExcelImportField(index = 26)
    private Date commitTime;
    @ExcelImportField(index = 27, range = {"100", "500"})
    private BigDecimal commissionAmount;


}
