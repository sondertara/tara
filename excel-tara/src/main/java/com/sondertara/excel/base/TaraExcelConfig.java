package com.sondertara.excel.base;

import com.sondertara.excel.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author huangxiaohu
 */
@Accessors(chain = true)
@Getter
@Setter
public class TaraExcelConfig {
    public static TaraExcelConfig CONFIG = new TaraExcelConfig();

    int defaultRowPeerSheet = Constants.DEFAULT_RECORD_COUNT_PEER_SHEET;

    int chineseMinColWidth = Constants.CHINESE_AUTO_SIZE_COLUMN_WIDTH_MIN;
    int chineseMaxColWidth = Constants.CHINESE_AUTO_SIZE_COLUMN_WIDTH_MAX;
    int csvProducerThread = Constants.PRODUCER_COUNT;
    int csvConsumerThread = Constants.CONSUMER_COUNT;

    boolean openAutoColWidth = Constants.OPEN_AUTO_COLUMN_WIDTH;


    private TaraExcelConfig() {

    }

    /**
     * Override the default config
     *
     * @param config
     */
    public void setConfig(TaraExcelConfig config) {
        CONFIG.chineseMaxColWidth = config.getChineseMaxColWidth();
        CONFIG.chineseMinColWidth = config.getChineseMinColWidth();
        CONFIG.csvConsumerThread = config.getCsvConsumerThread();
        CONFIG.csvProducerThread = config.getCsvProducerThread();
        CONFIG.openAutoColWidth = config.isOpenAutoColWidth();
        CONFIG.defaultRowPeerSheet = config.getDefaultRowPeerSheet();
    }


}
