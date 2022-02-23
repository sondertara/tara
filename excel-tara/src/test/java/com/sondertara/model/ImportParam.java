package com.sondertara.model;


import com.sondertara.excel.annotation.ExcelExportField;
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
    @ExcelExportField(index = 1)
    private String recordNo;

    @ExcelExportField(index = 3)
    private Date orderTime;

    @ExcelExportField(index = 6, required = true)
    private String vin;

    @ExcelExportField(index = 7, required = true)
    private String policyNo;

    @ExcelExportField(index = 8)
    private String correctNo;
    @ExcelExportField(index = 11)
    private String bizType;

    @ExcelExportField(index = 13)
    private String insuredName;

    @ExcelExportField(index = 14)
    private String insurerName;

    @ExcelExportField(index = 15)
    private String cityName;
    @ExcelExportField(index = 16)
    private String categoryName;

    @ExcelExportField(index = 17, scale = 4)
    private BigDecimal discount;
    @ExcelExportField(index = 18, required = true)
    private BigDecimal premium;
    @ExcelExportField(index = 21)
    private String specialInvoiceNo;
    @ExcelExportField(index = 26)
    private Date commitTime;
    @ExcelExportField(index = 27, range = {"100", "500"})
    private BigDecimal commissionAmount;


}
