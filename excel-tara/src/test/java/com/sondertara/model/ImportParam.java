package com.sondertara.model;


import lombok.Data;
import com.sondertara.excel.annotation.ImportField;

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
    /**
     * 理赔单号
     */
    @ImportField(index = 1)
    private String recordNo;

    @ImportField(index = 3)
    private Date orderTime;

    @ImportField(index = 6, required = true)
    private String vin;

    @ImportField(index = 7, required = true)
    private String policyNo;

    @ImportField(index = 8)
    private String correctNo;
    @ImportField(index = 11)
    private String bizType;

    @ImportField(index = 13)
    private String insuredName;

    @ImportField(index = 14)
    private String insurerName;

    @ImportField(index = 15)
    private String cityName;
    @ImportField(index = 16)
    private String categoryName;

    @ImportField(index = 17, scale = 4)
    private BigDecimal discount;
    @ImportField(index = 18, required = true)
    private BigDecimal premium;
    @ImportField(index = 21)
    private String specialInvoiceNo;
    @ImportField(index = 26)
    private Date commitTime;
    @ImportField(index = 27)
    private BigDecimal commissionAmount;


}
