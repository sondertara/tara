package com.sondertara.excel.base;

import com.sondertara.excel.common.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dhatim.fastexcel.Worksheet;

/**
 * @author huangxiaohu
 */
@Accessors(chain = true)
@Getter
@Setter
public class TaraExcelConfig {
    public static TaraExcelConfig CONFIG = new TaraExcelConfig();


    volatile int defaultRowPerSheet = Constants.DEFAULT_RECORD_COUNT_PER_SHEET;

    volatile int chineseMinColWidth = Constants.CHINESE_AUTO_SIZE_COLUMN_WIDTH_MIN;
    volatile int chineseMaxColWidth = Constants.CHINESE_AUTO_SIZE_COLUMN_WIDTH_MAX;
    volatile int csvConsumerThread = 4;

    volatile int excelProducerThread = 5;

    volatile boolean openAutoColWidth = Constants.OPEN_AUTO_COLUMN_WIDTH;

    volatile boolean useLegacy = false;


    private TaraExcelConfig() {
        Worksheet.MAX_COL_WIDTH=64;
    }

    public void setDefaultRowPerSheet(int defaultRowPerSheet) {
        if (defaultRowPerSheet < 1) {
            throw new IllegalArgumentException("Sheet row count must be positive");

        }
        if (defaultRowPerSheet > Constants.MAX_RECORD_PER_SHEET) {
            throw new IllegalArgumentException("Sheet row count must less than " + Constants.MAX_RECORD_PER_SHEET);
        }
        this.defaultRowPerSheet = defaultRowPerSheet;
    }
}
