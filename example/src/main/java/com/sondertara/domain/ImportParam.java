package com.sondertara.domain;


import com.sondertara.excel.meta.annotation.ExcelImportField;
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
    @ExcelImportField(colIndex = 1)
    private String recordNo;

    @ExcelImportField(colIndex = 3)
    private Date orderTime;

    @ExcelImportField(colIndex = 6, allowBlank = false)
    private String vin;

    @ExcelImportField(colIndex = 7, allowBlank = false)
    private String policyNo;

    @ExcelImportField(colIndex = 8)
    private String correctNo;
    @ExcelImportField(colIndex = 11)
    private String bizType;

    @ExcelImportField(colIndex = 13)
    private String insuredName;

    @ExcelImportField(colIndex = 14)
    private String insurerName;

    @ExcelImportField(colIndex = 15)
    private String cityName;
    @ExcelImportField(colIndex = 16)
    private String categoryName;

    @ExcelImportField(colIndex = 17)
    private BigDecimal discount;
    @ExcelImportField(colIndex = 18, allowBlank = false)
    private BigDecimal premium;
    @ExcelImportField(colIndex = 21)
    private String specialInvoiceNo;
    @ExcelImportField(colIndex = 26)
    private Date commitTime;
    @ExcelImportField(colIndex = 27)
    private BigDecimal commissionAmount;

}
